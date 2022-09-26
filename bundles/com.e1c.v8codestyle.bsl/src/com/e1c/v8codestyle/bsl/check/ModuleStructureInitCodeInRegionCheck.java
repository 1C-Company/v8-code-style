/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
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
 * Checks that initialize code should be placed in the Initialize region
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureInitCodeInRegionCheck
    extends AbstractModuleStructureCheck
{

    private static final String CHECK_ID = "module-structure-init-code-in-region"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Inject
    public ModuleStructureInitCodeInRegionCheck(IV8ProjectManager v8ProjectManager)
    {
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ModuleStructureInitCodeInRegion_Title)
            .description(Messages.ModuleStructureInitCodeInRegion_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(455, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.excludeTypes(ModuleType.COMMON_MODULE, ModuleType.COMMAND_MODULE,
                ModuleType.MANAGER_MODULE, ModuleType.SESSION_MODULE))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;

        ScriptVariant scriptVariant = v8ProjectManager.getProject(module).getScriptVariant();

        String initialize = ModuleStructureSection.INITIALIZE.getName(scriptVariant);

        List<Statement> statements = module.allStatements();
        for (Statement statement : statements)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (!(statement instanceof EmptyStatement))
            {
                addIssue(resultAceptor, initialize, statement);
            }
        }
    }

    private void addIssue(ResultAcceptor resultAceptor, String initialize, Statement statement)
    {
        if (EcoreUtil2.getContainerOfType(statement, Method.class) == null)
        {
            Optional<RegionPreprocessor> topRegion = getTopParentRegion(statement);
            if (topRegion.isEmpty() || !initialize.equalsIgnoreCase(topRegion.get().getName()))
            {
                resultAceptor.addIssue(
                    MessageFormat.format(Messages.ModuleStructureInitCodeInRegion_Issue__0, initialize), statement);
            }
        }
    }
}
