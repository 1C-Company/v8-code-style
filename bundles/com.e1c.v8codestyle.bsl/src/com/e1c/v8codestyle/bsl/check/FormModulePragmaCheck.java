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

import static com._1c.g5.v8.dt.bsl.common.Symbols.COMMAND_MODULE_SYMBOLS;
import static com._1c.g5.v8.dt.bsl.common.Symbols.FORM_MODULE_SYMBOLS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA__VALUE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check that pragmas is not used in other modules than form or command module.
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 */
public class FormModulePragmaCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "form-module-pragma"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormModulePragmaCheck_title)
            .description(Messages.FormModulePragmaCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .extension(new StandardCheckExtension(439, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(PRAGMA);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Pragma pragma = (Pragma)object;
        CaseInsensitiveString symbol = new CaseInsensitiveString(pragma.getSymbol());
        if (FORM_MODULE_SYMBOLS.contains(symbol) || COMMAND_MODULE_SYMBOLS.contains(symbol))
        {
            Module module = EcoreUtil2.getContainerOfType(pragma, Module.class);
            ModuleType type = module.getModuleType();

            if (type != ModuleType.FORM_MODULE && type != ModuleType.COMMAND_MODULE)
            {
                resultAceptor.addIssue(Messages.FormModulePragmaCheck_Form_module_compilation_pragma_used, pragma,
                    PRAGMA__VALUE);
            }
        }
    }

}
