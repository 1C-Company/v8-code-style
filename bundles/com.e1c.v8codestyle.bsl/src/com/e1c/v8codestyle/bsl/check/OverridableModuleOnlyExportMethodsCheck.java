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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 *  Finds non-export methods in Overridable modules
 *
 *  @author Artem Samohvalov
 */
public class OverridableModuleOnlyExportMethodsCheck
    extends AbstractModuleStructureCheck
{
    private static final String OVERRIDABLE_STRING = "overridable"; //$NON-NLS-1$
    private static final String OVERRIDABLE_STRING_RU = "переопределяемый"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return "overridable-module-only-export-methods"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.OverridableModuleOnlyExportMethodsCheck_Title)
            .description(Messages.OverridableModuleOnlyExportMethodsCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(553, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;

        if (isOverridable(module))
        {
            for (Method method : module.allMethods())
            {
                if (!method.isExport())
                {
                    String message = MessageFormat.format(
                        method instanceof Function ? Messages.OverridableModuleOnlyExportMethodsCheck_Function_Issue
                            : Messages.OverridableModuleOnlyExportMethodsCheck_Procedure_Issue,
                        method.getName());
                    resultAcceptor.addIssue(message, method);
                }
            }
        }
    }

    private boolean isOverridable(Module module)
    {
        return module.getOwner() instanceof CommonModule commonModule
            && (commonModule.getName().toLowerCase().endsWith(OVERRIDABLE_STRING_RU)
                || commonModule.getName().toLowerCase().endsWith(OVERRIDABLE_STRING));
    }
}
