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
package com.e1c.v8codestyle.bsl.ui.qfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.common.Symbols;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextInteractiveBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 *  Create new method
 *
 *  @author Artem Samohvalov
 */
@QuickFix(checkId = "notify-description-to-server-procedure", supplierId = "com.e1c.v8codestyle.bsl")
public class NotifyDescriptionToServerProcedureFix
    extends SingleVariantXtextBslModuleFix
{
    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.NotifyDescriptionToServerProcedureFix_Description)
            .details(Messages.NotifyDescriptionToServerProcedureFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject element = model.getElement();
        String stringValue = NodeModelUtils.getNode(element).getText();
        String methodName = QuickFixMethodsHelper.getMethodName(stringValue.replace("\"", "").trim()); //$NON-NLS-1$//$NON-NLS-2$

        QuickFixMethodsHelper.createMethod((IXtextInteractiveBslModuleFixModel)model, methodName, false, true,
            getAtClientKeyword(model, element));

        return null;
    }

    private String getAtClientKeyword(IXtextBslModuleFixModel model, EObject element)
    {
        Module module = EcoreUtil2.getContainerOfType(element, Module.class);
        ModuleType type = module.getModuleType();

        if (type == ModuleType.FORM_MODULE || type == ModuleType.COMMAND_MODULE)
        {
            return model.getScriptVariant() == ScriptVariant.RUSSIAN ? Symbols.AT_CLIENT_RUS : Symbols.AT_CLIENT_INTNL;
        }
        return null;
    }
}
