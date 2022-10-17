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

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks a variable was found in the object module.
 *
 * @author Artem Iliukhin
 */
public class ExportVariableInObjectModuleCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "export-variable-object-module"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ExportVariableInObjectModuleCheck_Title)
            .description(Messages.ExportVariableInObjectModuleCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(639, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;
        ModuleType type = module.getModuleType();
        if (type != ModuleType.OBJECT_MODULE)
        {
            return;
        }

        for (DeclareStatement ds : module.allDeclareStatements())
        {
            for (ExplicitVariable expliciteVar : ds.getVariables())
            {
                if (expliciteVar.isExport())
                {
                    resultAceptor.addIssue(Messages.ExportVariableInObjectModuleCheck_Issue, expliciteVar,
                        BslPackage.Literals.EXPLICIT_VARIABLE__EXPORT);
                }
            }
        }
    }
}
