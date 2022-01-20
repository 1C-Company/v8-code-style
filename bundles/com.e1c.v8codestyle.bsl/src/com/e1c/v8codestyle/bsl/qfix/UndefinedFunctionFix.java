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
package com.e1c.v8codestyle.bsl.qfix;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.resource.XtextResource;

import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextInteractiveBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.SingleVariantXtextBslModuleFix;

/**
 * Quick fix for check com.e1c.v8codestyle.bsl.check:undefined-function
 *
 * @author Vadim Geraskin
 */
@QuickFix(checkId = "undefined-function", supplierId = "com.e1c.v8codestyle.bsl")
public class UndefinedFunctionFix
    extends SingleVariantXtextBslModuleFix
{
    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true).description(Messages.UndefinedMethodFix_func_title);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        QuickFixMethodsHelper.createMethod(state, (IXtextInteractiveBslModuleFixModel)model, true);
        return null;
    }
}
