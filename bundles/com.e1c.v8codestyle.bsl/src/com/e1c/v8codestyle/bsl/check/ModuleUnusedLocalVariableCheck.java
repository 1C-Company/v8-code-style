/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.dt.bsl.common.VariableProcessor;
import com._1c.g5.v8.dt.bsl.model.Block;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.ForToStatement;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.ImplicitVariable;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.bsl.model.resource.owner.IBslOwnerComputerService;
import com._1c.g5.v8.dt.bsl.resource.BslResource;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com._1c.g5.v8.dt.bsl.typesystem.util.TypeSystemUtil;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.common.collect.Lists;

/**
 * Unused module local variable check.
 *
 * @author Andrey Volkov
 */
public final class ModuleUnusedLocalVariableCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "module-unused-local-variable"; //$NON-NLS-1$

    private final DynamicFeatureAccessComputer dynamicComputer;

    private final IBslOwnerComputerService ownerService;

    /**
     * Instantiates a new module unused local variable check.
     */
    public ModuleUnusedLocalVariableCheck()
    {
        super();
        IResourceServiceProvider serviceProvider =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$

        dynamicComputer = serviceProvider.get(DynamicFeatureAccessComputer.class);
        ownerService = serviceProvider.get(IBslOwnerComputerService.class);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ModuleUnusedLocalVariableCheck_Title)
            .description(Messages.ModuleUnusedLocalVariableCheck_Description)
            .issueType(IssueType.WARNING)
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        Module module = (Module)object;

        VariableProcessor processor = new VariableProcessor(module.allStatements(), dynamicComputer);
        processor.process();
        Map<Variable, List<INode>> variablesChangeValueModule = processor.variablesForChangeValue();
        Map<Variable, List<INode>> variablesForReadValueModule = processor.variablesForReadValue();
        for (Method method : getProcessMethods(module))
        {
            processor = new VariableProcessor(method.allStatements(), dynamicComputer);
            processor.process();
            Map<Variable, List<INode>> variablesChangeValue = processor.variablesForChangeValue();
            Map<Variable, List<INode>> variablesForReadValue = processor.variablesForReadValue();
            for (Map.Entry<Variable, List<INode>> variable : variablesChangeValue.entrySet())
            {
                if (variable.getKey() instanceof FormalParam)
                {
                    continue;
                }
                Block actualVariableBlock = EcoreUtil2.getContainerOfType(variable.getKey(), Block.class);
                if (actualVariableBlock != method)
                {
                    continue;
                }
                if (variable.getKey() instanceof ExplicitVariable && ((ExplicitVariable)variable.getKey()).isExport())
                {
                    continue;
                }
                if (variable.getKey() instanceof ImplicitVariable)
                {
                    ImplicitVariable implicitVariable = (ImplicitVariable)variable.getKey();
                    if (implicitVariable.eContainer()
                        .eContainingFeature() == BslPackage.Literals.FOR_STATEMENT__VARIABLE_ACCESS
                        && implicitVariable.eContainer().eContainer() instanceof ForToStatement)
                    {
                        continue;
                    }
                    if (isUniqueForStaticFeatureAccess(implicitVariable))
                    {
                        continue;
                    }
                }
                if (actualVariableBlock == module)
                {
                    variablesChangeValueModule.computeIfAbsent(variable.getKey(), item -> Lists.newArrayList())
                        .addAll(variable.getValue());
                }
                List<INode> readValueNodes = variablesForReadValue.get(variable.getKey());
                if (readValueNodes == null)
                {
                    resultAceptor
                        .addIssue(MessageFormat.format(Messages.ModuleUnusedLocalVariableCheck_Unused_local_variable__0,
                            variable.getKey().getName()), variable.getKey());
                }
                else if (variable.getKey() instanceof ImplicitVariable)
                {
                    ImplicitVariable implicitVariable = (ImplicitVariable)variable.getKey();
                    EObject variableOwner = implicitVariable.eContainer();
                    if (variableOwner instanceof StaticFeatureAccess
                        && variableOwner.eContainer() instanceof SimpleStatement)
                    {
                        for (INode readValueNode : readValueNodes)
                        {
                            if (readValueNode.getSemanticElement() == null)
                            {
                                continue;
                            }
                            if (EcoreUtil2.getContainerOfType(readValueNode.getSemanticElement(),
                                SimpleStatement.class) == variableOwner.eContainer())
                            {
                                resultAceptor.addIssue(MessageFormat.format(
                                    Messages.ModuleUnusedLocalVariableCheck_Probably_variable_not_initilized_yet__0,
                                    implicitVariable.getName()), readValueNode.getSemanticElement());
                            }
                        }
                    }
                }
            }
            for (Map.Entry<Variable, List<INode>> variable : variablesForReadValue.entrySet())
            {
                Block actualVariableBlock = EcoreUtil2.getContainerOfType(variable.getKey(), Block.class);
                if (actualVariableBlock == module)
                {
                    variablesForReadValueModule.computeIfAbsent(variable.getKey(), item -> Lists.newArrayList())
                        .addAll(variable.getValue());
                }
            }
            for (DeclareStatement decl : method.allDeclareStatements())
            {
                for (ExplicitVariable variable : decl.getVariables())
                {
                    if (variable.isExport())
                    {
                        continue;
                    }
                    boolean foundInChange = variablesChangeValue.keySet().contains(variable);
                    boolean foundInRead = variablesForReadValue.keySet().contains(variable);
                    if (!foundInChange && !foundInRead)
                    {
                        resultAceptor.addIssue(
                            MessageFormat.format(Messages.ModuleUnusedLocalVariableCheck_Unused_local_variable__0,
                                variable.getName()),
                            variable);
                    }
                    if (!foundInChange)
                    {
                        resultAceptor.addIssue(MessageFormat.format(
                            Messages.ModuleUnusedLocalVariableCheck_Probably_variable_not_initilized_yet__0,
                            variable.getName()), variable);
                    }
                    if (foundInChange && foundInRead)
                    {
                        INode changeValueNode = variablesChangeValue.get(variable).get(0);
                        INode readValueNode = variablesForReadValue.get(variable).get(0);
                        if (readValueNode.getOffset() < changeValueNode.getOffset())
                        {
                            resultAceptor.addIssue(MessageFormat.format(
                                Messages.ModuleUnusedLocalVariableCheck_Probably_variable_not_initilized_yet__0,
                                variable.getName()), readValueNode.getSemanticElement());
                        }
                    }
                }
            }
        }
        for (Map.Entry<Variable, List<INode>> variable : variablesChangeValueModule.entrySet())
        {
            if (EcoreUtil2.getContainerOfType(variable.getKey(), Block.class) != module)
            {
                continue;
            }
            if (variable.getKey() instanceof ExplicitVariable && ((ExplicitVariable)variable.getKey()).isExport())
            {
                continue;
            }
            List<INode> readValueNodes = variablesForReadValueModule.get(variable.getKey());
            if (readValueNodes == null && !(variable.getKey() instanceof ImplicitVariable
                && isUniqueForStaticFeatureAccess((ImplicitVariable)variable.getKey())))
            {
                resultAceptor
                    .addIssue(MessageFormat.format(Messages.ModuleUnusedLocalVariableCheck_Unused_local_variable__0,
                        variable.getKey().getName()), variable.getKey());
            }
        }
    }

    private List<Method> getProcessMethods(Module module)
    {
        List<Method> processingObjects;
        if (!onlyMethodReparse(module))
        {
            processingObjects = Lists.newArrayList(module.allMethods());
        }
        else
        {
            BslResource resource = (BslResource)module.eResource();
            processingObjects = com._1c.g5.v8.dt.bsl.util.BslUtil.getDependMethodsTree(resource.getReparseMethod())
                .stream()
                .filter(item -> item instanceof Method)
                .map(item -> (Method)item)
                .collect(Collectors.toList());
            processingObjects.add(resource.getReparseMethod());
        }
        return processingObjects;
    }

    private boolean onlyMethodReparse(Module module)
    {
        // TODO - only full validation first, optimization later
        //return module.eResource() != null && ((BslResource)module.eResource()).isOnlyMethodReparse()
        //    && getCheckMode() != BslCancelableDiagnostician.ALL_FOR_FULL_MODULE;
        return false;
    }

    // TODO - only full validation first, optimization later
    //private CheckMode getCheckMode()
    //{
    //    return state.get().checkMode;
    //}

    private boolean isUniqueForStaticFeatureAccess(ImplicitVariable implicitVariable)
    {
        return implicitVariable.eContainer() instanceof StaticFeatureAccess && !implicitVariable.getEnvironments()
            .equals(TypeSystemUtil.getActualEnvironments(implicitVariable.eContainer(), ownerService));
    }

}
