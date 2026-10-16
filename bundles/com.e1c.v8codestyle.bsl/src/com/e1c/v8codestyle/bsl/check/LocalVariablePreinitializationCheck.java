/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.INode;

import com._1c.g5.v8.dt.bsl.common.VariableProcessor;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check for local variable preinitialization.
 * Reports a read of a local variable that is not initialized in the current or an enclosing scope
 *
 * @author Artem Samohvalov
 */
public class LocalVariablePreinitializationCheck
    extends AbstractModuleStructureCheck
{
    private final DynamicFeatureAccessComputer dynamicComputer;

    @Inject
    public LocalVariablePreinitializationCheck(DynamicFeatureAccessComputer dynamicComputer)
    {
        this.dynamicComputer = dynamicComputer;
    }

    @Override
    public String getCheckId()
    {
        return "local-variable-preinitialization"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.LocalVariablePreinitializationCheck_Title)
            .description(Messages.LocalVariablePreinitializationCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(494, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;

        VariableProcessor processor = new VariableProcessor(method.allStatements(), dynamicComputer);
        processor.process();

        Map<Variable, List<INode>> changes = processor.variablesForChangeValue();
        Map<Variable, List<INode>> reads = processor.variablesForReadValue();

        // for each read variable
        for (Map.Entry<Variable, List<INode>> entry : reads.entrySet())
        {
            if (monitor.isCanceled())
                return;

            Variable variable = entry.getKey();

            // if var is method parameter or explicit variable (Var A = 1;)
            // don't need to check init level
            if (variable instanceof FormalParam || variable instanceof ExplicitVariable)
                continue;

            List<INode> changeNodes = changes.get(variable);
            if (changeNodes == null)
                continue;

            for (INode readNode : entry.getValue())
            {
                if (monitor.isCanceled())
                    return;

                if (isPreinitialized(readNode, changeNodes))
                    continue;

                EObject readAccess = readNode.getSemanticElement();
                if (readAccess != null)
                {
                    String message =
                        MessageFormat.format(Messages.LocalVariablePreinitializationCheck_Issue, variable.getName());
                    resultAcceptor.addIssue(message, readAccess);
                }
            }
        }
    }

    private static boolean isPreinitialized(INode readNode, List<INode> changeNodes)
    {
        EObject readAccess = readNode.getSemanticElement();
        if (readAccess == null)
        {
            return false;
        }

        for (INode changeNode : changeNodes)
        {
            if (changeNode.getOffset() < readNode.getOffset()) // required readNode bellow changeNode
            {
                EObject variableAccess = changeNode.getSemanticElement();
                if (variableAccess == null)
                    continue;

                EObject scope = getScopeContainer(variableAccess);
                if (scope == null)
                    continue;

                if (isInScopeOrAbove(readAccess, scope)) // if this this readNode in init scope or higher
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static EObject getScopeContainer(EObject variableAccess)
    {
        Statement statement = EcoreUtil2.getContainerOfType(variableAccess, Statement.class);
        return statement == null ? null : statement.eContainer();
    }

    private static boolean isInScopeOrAbove(EObject readAccess, EObject scope)
    {
        for (EObject container =
            EcoreUtil2.getContainerOfType(readAccess, Statement.class); container != null; container =
                container.eContainer())
        {
            if (container == scope)
            {
                return true;
            }
        }
        return false;
    }
}
