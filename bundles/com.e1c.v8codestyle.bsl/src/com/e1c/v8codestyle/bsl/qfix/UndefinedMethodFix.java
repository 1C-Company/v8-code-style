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
import org.eclipse.xtext.resource.XtextResource;

import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextInteractiveBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.MultiVariantXtextBslModuleFix;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Quick fix for check com.e1c.v8codestyle.bsl.check:undefined-method
 *
 * @author Vadim Geraskin
 */
@QuickFix(checkId = "undefined-method", supplierId = "com.e1c.v8codestyle.bsl")
public class UndefinedMethodFix
    extends MultiVariantXtextBslModuleFix
{
    @Override
    protected void buildVariants()
    {
        // 1-st variant of issue qf: create function
        VariantBuilder.create(this)
            .description(Messages.UndefinedMethodFix_func_title, Messages.UndefinedMethodFix_func_desc)
            .interactive(true)
            .change(
                (context, session, state, model) -> createFunction(state, (IXtextInteractiveBslModuleFixModel)model))
            .build();

        // 2-nd variant of issue qf: create procedure
        VariantBuilder.create(this)
            .description(Messages.UndefinedMethodFix_proc_title, Messages.UndefinedMethodFix_proc_desc)
            .interactive(true)
            .change(
                (context, session, state, model) -> createProcedure(state, (IXtextInteractiveBslModuleFixModel)model))
            .build();
    }

    private void createFunction(XtextResource state, IXtextInteractiveBslModuleFixModel model)
    {
        try
        {
            QuickFixMethodsHelper.createMethod(state, model, true);
        }
        catch (BadLocationException e)
        {
            BslPlugin.createErrorStatus("Error occured when creating function", e); //$NON-NLS-1$
        }
    }

    private void createProcedure(XtextResource state, IXtextInteractiveBslModuleFixModel model)
    {
        try
        {
            QuickFixMethodsHelper.createMethod(state, model, false);
        }
        catch (BadLocationException e)
        {
            BslPlugin.createErrorStatus("Error occured when creating procedure", e); //$NON-NLS-1$
        }
    }
}
