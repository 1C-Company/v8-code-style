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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * The check finds modules (except Overridable and Localization modules) without construct-function
 * 
 * @author Artem Samohvalov
 */
public class ProgramInterfaceConstructFunctionExistenceCheck
    extends BasicCheck<Object>
{
    private static final String OVERRIDABLE_STRING = "overridable"; //$NON-NLS-1$
    private static final String OVERRIDABLE_STRING_RU = "переопределяемый"; //$NON-NLS-1$

    private static final String LOCALIZATION_STRING = "localization"; //$NON-NLS-1$
    private static final String LOCALIZATION_STRING_RU = "локализация"; //$NON-NLS-1$ 

    private static final String API_NAME = "Public"; //$NON-NLS-1$
    private static final String API_NAME_RU = "ПрограммныйИнтерфейс"; //$NON-NLS-1$

    private static final Set<String> CHECKED_RETURN_TYPES = Set.of("Structure", "ValueTable", "ValueTree"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private final TypesComputer typesComputer;

    @Inject
    public ProgramInterfaceConstructFunctionExistenceCheck(TypesComputer typesComputer)
    {
        this.typesComputer = typesComputer;
    }

    @Override
    public String getCheckId()
    {
        return "program-interface-construct-function-existence"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ProgramInterfaceConstructFunctionExistence_Title)
            .description(Messages.ProgramInterfaceConstructFunctionExistence_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(641, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;

        if (!(module.getOwner() instanceof CommonModule))
        {
            return;
        }

        // if not overridable and not localization module
        if (!hasPrefix(LOCALIZATION_STRING, LOCALIZATION_STRING_RU, module)
            && !hasPrefix(OVERRIDABLE_STRING, OVERRIDABLE_STRING_RU, module))
        {
            List<RegionPreprocessor> regions = findCheckedRegions(module);

            for (RegionPreprocessor region : regions)
            {
                // if no construct function
                if (!containsConstructorInRegion(region, module))
                {
                    resultAcceptor.addIssue(Messages.ProgramInterfaceConstructFunctionExistence_Issue, region,
                        NAMED_ELEMENT__NAME);
                }
            }
        }
    }

    private boolean hasPrefix(String prefix, String prefixRu, Module module)
    {
        return module.getOwner() instanceof CommonModule commonModule
            && (commonModule.getName().toLowerCase().endsWith(prefixRu)
                || commonModule.getName().toLowerCase().endsWith(prefix));
    }

    private List<RegionPreprocessor> findCheckedRegions(Module module)
    {
        return BslUtil.getAllRegionPreprocessors(module)
            .stream()
            .filter(Objects::nonNull)
            .filter(
                region -> API_NAME_RU.equalsIgnoreCase(region.getName()) || API_NAME.equalsIgnoreCase(region.getName()))
            .toList();
    }

    private boolean containsConstructorInRegion(RegionPreprocessor region, Module module)
    {
        for (Method method : module.allMethods())
        {
            if (!(method instanceof Function function))
            {
                continue;
            }
            if (!isInRegion(function, region))
            {
                continue;
            }
            if (isConstructFunction(function))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isInRegion(EObject object, RegionPreprocessor region)
    {
        PreprocessorItem item = region.getItem();
        return item != null && EcoreUtil.isAncestor(item, object);
    }

    /**
     * Checks if is construct function.
     * if return type contains in CHECKED_RETURN_TYPES
     * 
     * don't work if => return SomeFunctionReturnStruct();
     *
     * @param function the function
     * @return true, if is construct function
     */
    private boolean isConstructFunction(Function function)
    {
        TreeIterator<EObject> iterator = EcoreUtil.getAllContents(function, true);

        while (iterator.hasNext())
        {
            EObject element = iterator.next();

            if (element instanceof ReturnStatement returnStatement)
            {
                if (returnStatement.getExpression() instanceof OperatorStyleCreator operatorStyleCreator
                    && operatorStyleCreator.getType().getName() != null
                    && CHECKED_RETURN_TYPES.contains(operatorStyleCreator.getType().getName()))
                {
                    return true;
                }

                if (returnStatement.getExpression() instanceof FeatureAccess featureAccess)
                {
                    Environmental environmental = EcoreUtil2.getContainerOfType(featureAccess, Environmental.class);
                    Environments environments = environmental != null ? environmental.environments() : null;

                    if (environments != null && typesComputer.compute(featureAccess, environments)
                        .stream()
                        .anyMatch(type -> type.getName() != null && CHECKED_RETURN_TYPES.contains(type.getName())))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
