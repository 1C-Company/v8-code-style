/**
 * Copyright (C) 2022, 1C
 */
package com.e1c.v8codestyle.bsl.qfix;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.services.BslGrammarAccess;
import com._1c.g5.v8.dt.bsl.ui.quickfix.BslQuickFixUtil;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixFacade;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

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
        configurer.description(Messages.UndefinedVariableFix_title);
    }

    @Override
    protected void fixIssue(XtextResource state, IXtextBslModuleFixFacade model) throws BadLocationException
    {
        EObject element = model.getElement();
        boolean isRussian = model.isRussian();
        BslGrammarAccess bslGrammar = model.getBslGrammar();

        String declarationKeyWord = !isRussian ? bslGrammar.getDeclareStatementAccess().getVarKeyword_0_0().getValue()
            : bslGrammar.getDeclareStatementAccess()
                .getCyrillicCapitalLetterPeCyrillicSmallLetterIeCyrillicSmallLetterErCyrillicSmallLetterIeCyrillicSmallLetterEmKeyword_0_1()
                .getValue();

        //up to Method class
        Method method = EcoreUtil2.getContainerOfType(element, Method.class);

        if (method != null)
        {
            Optional<String> indent = model.getFormatString(method);
            int offset = NodeModelUtils.findActualNodeFor(method.allStatements().get(0)).getOffset(); //method always has statement

            String variableName = model.getIssueData()[1];
            IXtextDocument document = model.getXtextDocument();
            String lineSeparator = model.getLineSeparator();

            String variable = BslQuickFixUtil.createVariable(declarationKeyWord, variableName,
                indent.get() + model.getIndentProvider().getIndent(), lineSeparator);

            //write var to module
            BslQuickFixUtil.writeToDoc(document, offset, variable);
            //linked mode model
            int posDec = offset + declarationKeyWord.length() + 1;
            int posUse = model.getIssue().getOffset() + variable.length();

            ITextViewer viewer = model.getTextViewer();
            if (viewer != null)
            {
                BslQuickFixUtil.createLinkedModeModelForVariable(document, viewer, posDec, posUse,
                    variableName.length());
            }
        }
    }
}
