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

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.ui.quickfix.BslQuickFixUtil;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextInteractiveBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.SingleVariantXtextBslModuleFix;

/**
 * Quick fix for check com.e1c.v8codestyle.bsl.check:undefined-variable
 *
 * @author Vadim Geraskin
 */
@QuickFix(checkId = "undefined-variable", supplierId = "com.e1c.v8codestyle.bsl")
public class UndefinedVariableFix
    extends SingleVariantXtextBslModuleFix
{
    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.UndefinedVariableFix_title)
            .details(Messages.UndefinedVariableFix_desc);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        IXtextInteractiveBslModuleFixModel interactiveModel = (IXtextInteractiveBslModuleFixModel)model;

        EObject element = model.getElement();
        String declarationKeyWord = getDeclarationKeyword(model);

        //up to Method class
        Method method = EcoreUtil2.getContainerOfType(element, Method.class);

        if (method != null)
        {
            Optional<String> indent = interactiveModel.getFormatString(method);
            int offset = QuickFixMethodsHelper.getMethodOffset(method);

            String variableName = ((StaticFeatureAccess)model.getElement()).getName();
            IXtextDocument document = (IXtextDocument)interactiveModel.getDocument();
            String lineSeparator = model.getLineSeparator();

            String variable = BslQuickFixUtil.createVariable(declarationKeyWord, variableName,
                indent.get() + interactiveModel.getIndentProvider().getIndent(), lineSeparator);

            //write var to module
            BslQuickFixUtil.writeToDoc(document, offset, variable);

            //linked mode model
            int posDec = offset + declarationKeyWord.length() + 1;
            int posUse = model.getIssue().getOffset() + variable.length();
            int length = variableName.length();

            createLinkedModeModel(interactiveModel, posDec, posUse, length);
        }
        return null;
    }

    private static String getDeclarationKeyword(IXtextBslModuleFixModel model)
    {
        boolean isRussion = model.getScriptVariant() == ScriptVariant.RUSSIAN;
        return !isRussion ? model.getBslGrammar().getDeclareStatementAccess().getVarKeyword_0_0().getValue() : model
            .getBslGrammar()
            .getDeclareStatementAccess()
            .getCyrillicCapitalLetterPeCyrillicSmallLetterIeCyrillicSmallLetterErCyrillicSmallLetterIeCyrillicSmallLetterEmKeyword_0_1()
            .getValue();
    }

    private static void createLinkedModeModel(IXtextInteractiveBslModuleFixModel model, int posDec, int posUse,
        int length)
    {
        IXtextDocument document = (IXtextDocument)model.getDocument();
        try
        {
            //create groups - this step is independent of the linked mode
            LinkedPositionGroup group = new LinkedPositionGroup();
            group.addPosition(new LinkedPosition(document, posDec, length));
            group.addPosition(new LinkedPosition(document, posUse, length));
            //set up linked mode
            LinkedModeModel linkedModeModel = model.getLinkedModeModel();
            linkedModeModel.addGroup(group);
            LinkedModeModel.closeAllModels(document);
            linkedModeModel.forceInstall();

            model.enterUIMode();
            model.selectAndRevealForLinkedModeModel(posUse, length);
        }
        catch (Exception e)
        {
            //mark using varible
            model.selectAndRevealForLinkedModeModel(posUse, length);
        }
    }
}
