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
package com.e1c.v8codestyle.bsl.ui.qfix;

import java.text.MessageFormat;
import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.common.Pair;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;


/**
 * Quick fix for check com.e1c.v8codestyle.bsl.strict.check:function-return-value-type
 * Replaces Function and EndFunction keywords with Procedure and EndProcedure
 *
 * @author Timur Mukhamedishin
 *
 */
@QuickFix(checkId = "function-return-value-type", supplierId = "com.e1c.v8codestyle.bsl")
public class ConvertFunctionToProcedureFix
    extends SingleVariantXtextBslModuleFix
{
    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description((context, session) -> {
                String name = ""; //$NON-NLS-1$

                IXtextBslModuleFixModel model = context.getModel(session, false);
                EObject element = model.getElement();

                if (element instanceof Function)
                {
                    name = ((Function)element).getName();
                }

                String description = MessageFormat.format(Messages.ConvertFunctionToProcedureFix_description, name);
                String details = MessageFormat.format(Messages.ConvertFunctionToProcedureFix_details, name);

                return Pair.newPair(description, details);
            });
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model)
    {
        EObject element = model.getElement();

        if (!(element instanceof Function))
        {
            return null;
        }

        boolean isRussian = model.getScriptVariant() == ScriptVariant.RUSSIAN;

        Keyword functionKeyword = getFunctionKeyword(model, isRussian);
        Keyword endFunctionKeyword = getEndFunctionKeyword(model, isRussian);
        Keyword procedureKeyword = getProcedureKeyword(model, isRussian);
        Keyword endProcedureKeyword = getEndProcedureKeyword(model, isRussian);

        INode node = NodeModelUtils.findActualNodeFor(element);

        LinkedList<ILeafNode> allLeafNodes = new LinkedList<>();
        node.getLeafNodes().forEach(allLeafNodes::add);

        MultiTextEdit result = new MultiTextEdit();

        for (int i = 0; i < allLeafNodes.size(); ++i)
        {
            ILeafNode firstNode = allLeafNodes.pollFirst();

            if (firstNode.getGrammarElement().equals(functionKeyword))
            {
                result.addChild(new ReplaceEdit(firstNode.getOffset(), firstNode.getLength(),
                    procedureKeyword.getValue()));
                break;
            }
        }

        for (int i = 0; i < allLeafNodes.size(); ++i)
        {
            ILeafNode lastNode = allLeafNodes.pollLast();

            if (lastNode.getGrammarElement().equals(endFunctionKeyword))
            {
                result.addChild(new ReplaceEdit(lastNode.getOffset(), lastNode.getLength(),
                    endProcedureKeyword.getValue()));
                break;
            }
        }

        if (result.getChildrenSize() == 2)
        {
            return result;
        }

        return null;
    }

    //CHECKSTYLE.OFF: LineLength
    private Keyword getFunctionKeyword(IXtextBslModuleFixModel model, boolean isRussian)
    {
        return !isRussian ? model.getBslGrammar().getFunctionAccess().getFunctionKeyword_2_0()
            : model.getBslGrammar()
                .getFunctionAccess()
                .getCyrillicCapitalLetterEfCyrillicSmallLetterUCyrillicSmallLetterEnCyrillicSmallLetterKaCyrillicSmallLetterTseCyrillicSmallLetterICyrillicSmallLetterYaKeyword_2_1();
    }

    private Keyword getEndFunctionKeyword(IXtextBslModuleFixModel model, boolean isRussian)
    {
        return !isRussian ? model.getBslGrammar().getFunctionAccess().getEndFunctionKeyword_9_0() : model
            .getBslGrammar()
            .getFunctionAccess()
            .getCyrillicCapitalLetterKaCyrillicSmallLetterOCyrillicSmallLetterEnCyrillicSmallLetterIeCyrillicSmallLetterTseCyrillicCapitalLetterEfCyrillicSmallLetterUCyrillicSmallLetterEnCyrillicSmallLetterKaCyrillicSmallLetterTseCyrillicSmallLetterICyrillicSmallLetterIKeyword_9_1();
    }

    private Keyword getProcedureKeyword(IXtextBslModuleFixModel model, boolean isRussian)
    {
        return !isRussian ? model.getBslGrammar().getProcedureAccess().getProcedureKeyword_2_0() : model
            .getBslGrammar()
            .getProcedureAccess()
            .getCyrillicCapitalLetterPeCyrillicSmallLetterErCyrillicSmallLetterOCyrillicSmallLetterTseCyrillicSmallLetterIeCyrillicSmallLetterDeCyrillicSmallLetterUCyrillicSmallLetterErCyrillicSmallLetterAKeyword_2_1();
    }

    private Keyword getEndProcedureKeyword(IXtextBslModuleFixModel model, boolean isRussian)
    {
        return !isRussian ? model.getBslGrammar().getProcedureAccess().getEndProcedureKeyword_9_0() : model
            .getBslGrammar()
            .getProcedureAccess()
            .getCyrillicCapitalLetterKaCyrillicSmallLetterOCyrillicSmallLetterEnCyrillicSmallLetterIeCyrillicSmallLetterTseCyrillicCapitalLetterPeCyrillicSmallLetterErCyrillicSmallLetterOCyrillicSmallLetterTseCyrillicSmallLetterIeCyrillicSmallLetterDeCyrillicSmallLetterUCyrillicSmallLetterErCyrillicSmallLetterYeruKeyword_9_1();
    }
    //CHECKSTYLE.ON: LineLength
}
