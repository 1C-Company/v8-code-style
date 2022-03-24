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

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.ui.contentassist.BslProposalProvider;
import com._1c.g5.v8.dt.bsl.ui.quickfix.BslQuickFixUtil;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextInteractiveBslModuleFixModel;
import com.google.common.base.Strings;

/**
 * Helper methods for xtext-based quick fixes
 *
 * @author Vadim Geraskin
 */
public final class QuickFixMethodsHelper
{
    /**
     * Creates method and writes it to module
     *
     * @param model the xtext BSL quick fix model, cannot be {@code null}
     * @param isFunc indicates whether the method is function or procedure
     * @throws BadLocationException
     */
    static void createMethod(IXtextInteractiveBslModuleFixModel model, boolean isFunc) throws BadLocationException
    {
        EObject element = model.getElement();

        String methodName = getMethodName(((StaticFeatureAccess)element).getName());
        String directiveName = ""; //$NON-NLS-1$

        //up to Method class
        Method method = EcoreUtil2.getContainerOfType(element, Method.class);

        if (method != null)
        {
            String methodKeywordType = getTypeMethodName(model, isFunc);
            String methodEndKeywordType = getTypeEndMethodName(model, isFunc);

            //calculation offset
            int totalEndOffset = NodeModelUtils.getNode(method).getTotalEndOffset();
            Optional<String> indent = model.getFormatString(method);

            if (indent.isPresent())
            {
                String func = createMethod(model, indent.get(), directiveName, methodName, methodKeywordType,
                    methodEndKeywordType);

                // Write method to module
                IXtextDocument document = (IXtextDocument)model.getDocument();
                BslQuickFixUtil.writeToDoc(document, totalEndOffset, func);
                flushMethod(model, indent.get(), methodKeywordType, totalEndOffset, directiveName);
            }
        }
    }

    /**
     * Provides offset for the given {@Method}
     *
     * @param method the method, cannot be {@code null}
     * @return offset
     */
    static int getMethodOffset(Method method)
    {
        //method always has statement
        return NodeModelUtils.findActualNodeFor(method.allStatements().get(0)).getOffset();
    }

    /**
     * Gets 'Procedure' or 'Function'
     *
     * @param model {@link IXtextBslModuleFixModel}, cannot be {@code null}
     * @param isFunc defines what will be return
     * @return 'Procedure' if <code>isFunc == false, else return 'Function'</code>
     */
    static String getTypeMethodName(IXtextBslModuleFixModel model, boolean isFunc)
    {
        boolean isRussion = model.getScriptVariant() == ScriptVariant.RUSSIAN;
        return BslProposalProvider.getTypeMethodName(model.getBslGrammar(), isFunc, isRussion);
    }

    /**
     * Gets 'EndProcedure' or 'EndFunction'
     *
     * @param model {@link IXtextBslModuleFixModel}, cannot be {@code null}
     * @param isFunc defines what will be return
     * @return 'EndProcedure' if <code>isFunc == false, else return 'EndFunction'</code>
     */
    static String getTypeEndMethodName(IXtextBslModuleFixModel model, boolean isFunc)
    {
        boolean isRussion = model.getScriptVariant() == ScriptVariant.RUSSIAN;
        return BslProposalProvider.getTypeEndMethodName(model.getBslGrammar(), isFunc, isRussion);
    }

    private static String getMethodName(String name)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(name);
        builder.append('(');
        builder.append(')');
        return builder.toString();
    }

    private static String createMethod(IXtextInteractiveBslModuleFixModel model, String indent, String directive,
        String methodName, String methodKeyword, String methodEndKeyword)
    {
        String lineSeparator = model.getLineSeparator();
        String commentContent = Strings.repeat(lineSeparator, 2);
        String todoComment = indent + model.getIndentProvider().getIndent()
            + model.getBslGeneratorMultiLangProposals().getImplementationPropStr();

        StringBuilder builder = new StringBuilder();
        //add comment above function
        builder.append(commentContent);
        //create function body
        if (!Strings.isNullOrEmpty(directive))
        {
            builder.append(indent).append('&').append(directive).append(lineSeparator);
        }
        builder.append(indent).append(methodKeyword).append(' ');
        builder.append(methodName);
        builder.append(lineSeparator).append(todoComment).append(lineSeparator);
        builder.append(indent).append(methodEndKeyword);
        return builder.toString();
    }

    private static void flushMethod(IXtextInteractiveBslModuleFixModel model, String indent, String methodKeywordType,
        int totalEndOffset, String directiveName)
    {
        IXtextDocument document = (IXtextDocument)model.getDocument();
        String lineSeparator = model.getLineSeparator();
        String commentContent = Strings.repeat(lineSeparator, 2);
        //creating LinkedModeModel
        LinkedPosition[] groupParams = calculateParamsGroupForMethod(commentContent, indent, lineSeparator,
            methodKeywordType, document, model.getIssueData(), totalEndOffset);
        int posDec = totalEndOffset + indent.length() + methodKeywordType.length() + 1 + commentContent.length();
        if (!Strings.isNullOrEmpty(directiveName))
        {
            posDec += indent.length() + 1 + directiveName.length() + lineSeparator.length();
        }
        int posUse = model.getIssue().getOffset();

        int nameLen = ((StaticFeatureAccess)model.getElement()).getName().length();
        createLinkedModeModel(model, posDec, posUse, nameLen, groupParams);
    }

    private static LinkedPosition[] calculateParamsGroupForMethod(String commentContent, String indent,
        String lineSeparator, String functionKeywordType, IXtextDocument doc, String[] data, int offset)
    {
        if (data != null && data.length >= 2)
        {
            int numParams = Integer.parseInt(data[2]);
            LinkedPosition[] groupParams = new LinkedPosition[numParams];
            int startLen = data[1].length() + 2 + offset + commentContent.length() + indent.length()
                + functionKeywordType.length();
            if (!Strings.isNullOrEmpty(data[0]))
            {
                startLen += indent.length() + data[0].length() + lineSeparator.length() + 1;
            }
            for (int i = 3; i < data.length; ++i)
            {
                groupParams[i - 3] = new LinkedPosition(doc, startLen, data[i].length());
                startLen += data[i].length() + 2;
            }
            return groupParams;
        }
        else
        {
            return new LinkedPosition[0];
        }
    }

    private static void createLinkedModeModel(IXtextInteractiveBslModuleFixModel model, int posDec, int posUse,
        int length, LinkedPosition[] params)
    {
        IXtextDocument document = (IXtextDocument)model.getDocument();
        try
        {
            // create groups - this step is independent of the linked mode
            LinkedPositionGroup group = new LinkedPositionGroup();
            group.addPosition(new LinkedPosition(document, posDec, length));
            group.addPosition(new LinkedPosition(document, posUse, length));
            /* set up linked mode */
            LinkedModeModel linkedModeModel = model.getLinkedModeModel();
            linkedModeModel.addGroup(group);
            for (int i = 0; i < params.length; ++i)
            {
                LinkedPositionGroup groupParam = new LinkedPositionGroup();
                groupParam.addPosition(params[i]);
                linkedModeModel.addGroup(groupParam);
            }
            LinkedModeModel.closeAllModels(document);
            linkedModeModel.forceInstall();
            model.enterUiMode();
            model.selectAndRevealForLinkedModeModel(posDec, length);
        }
        catch (Exception e)
        {
            model.selectAndRevealForLinkedModeModel(posUse, length);
        }
    }

    private QuickFixMethodsHelper()
    {
        // Not to be instantiated
    }
}
