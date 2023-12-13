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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DECLARE_STATEMENT;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks variables in region.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureVariablesInRegionCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "module-structure-var-in-region"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ModuleStructureVariablesInRegionCheck(IV8ProjectManager v8ProjectManager)
    {
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ModuleStructureVariablesInRegionCheck_Title)
            .description(Messages.ModuleStructureVariablesInRegionCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(455, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.excludeTypes(ModuleType.COMMON_MODULE, ModuleType.COMMAND_MODULE,
                ModuleType.MANAGER_MODULE, ModuleType.SESSION_MODULE))
            .module()
            .checkedObjectType(DECLARE_STATEMENT);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        DeclareStatement declareStatement = (DeclareStatement)object;
        Method method = EcoreUtil2.getContainerOfType((EObject)declareStatement, Method.class);
        if (method != null)
        {
            return;
        }

        if (monitor.isCanceled())
        {
            return;
        }

        Collection<ExplicitVariable> variables = declareStatement.getVariables();
        if (variables.isEmpty())
        {
            return;
        }

        Optional<RegionPreprocessor> region = getTopParentRegion(declareStatement);

        ScriptVariant scriptVariant = v8ProjectManager.getProject(declareStatement).getScriptVariant();
        String variablesName = ModuleStructureSection.VARIABLES.getName(scriptVariant);
        for (ExplicitVariable variable : variables)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (region.isEmpty() || !variablesName.equalsIgnoreCase(region.get().getName()))
            {
                resultAceptor.addIssue(
                    MessageFormat.format(Messages.ModuleStructureVariablesInRegionCheck_Issue__0, variablesName),
                    variable, McorePackage.Literals.NAMED_ELEMENT__NAME);
            }
        }
    }
}
