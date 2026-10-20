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

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.model.Method;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.bsl.DocumentationCommentIndentsHelper;
import com.google.inject.Inject;

/**
 * The fix for {@link MixedTabsAndSpacesInCommentCheck}
 * 
 * @author Artem Samohvalov
 */
@QuickFix(checkId = "mixed-tabs-and-spaces-in-comment", supplierId = "com.e1c.v8codestyle.bsl")
public class MixedTabsAndSpacesInCommentCheckFix
    extends SingleVariantXtextBslModuleFix
{
    private final BslMultiLineCommentDocumentationProvider commentProvider;

    @Inject
    public MixedTabsAndSpacesInCommentCheckFix(BslMultiLineCommentDocumentationProvider commentProvider)
    {
        this.commentProvider = commentProvider;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.MixedTabsAndSpacesInCommentCheckFix_Description)
            .details(Messages.MixedTabsAndSpacesInCommentCheckFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        if (!(model.getElement() instanceof Method method))
            return null;

        IDocument document = model.getDocument();
        List<String> lines = commentProvider.getCommentLines(method);

        INode methodNode = NodeModelUtils.findActualNodeFor(method);
        if (methodNode == null)
            return null;

        int commentStartLine = document.getLineOfOffset(methodNode.getOffset());
        while (commentStartLine > 0 && isCommentLine(document, commentStartLine - 1))
        {
            --commentStartLine;
        }

        List<Integer> mixedLines = DocumentationCommentIndentsHelper.findMixedLines(lines, false);
        MultiTextEdit textEditResult = new MultiTextEdit();

        for (int lineNumber : mixedLines)
        {
            IRegion lineInfo = document.getLineInformation(commentStartLine + lineNumber);
            int offset = lineInfo.getOffset();

            String newIndent = DocumentationCommentIndentsHelper.getNormalizedIndent(lines.get(lineNumber));
            int oldIndentLength = DocumentationCommentIndentsHelper.getIndentLength(lines.get(lineNumber));

            ReplaceEdit replaceEdit = new ReplaceEdit(
                offset + DocumentationCommentIndentsHelper.COMMENT_PREFIX_LENGTH, oldIndentLength, newIndent);
            textEditResult.addChild(replaceEdit);
        }

        return textEditResult.hasChildren() ? textEditResult : null;
    }

    private boolean isCommentLine(IDocument document, int lineNumber) throws BadLocationException
    {
        IRegion lineInfo = document.getLineInformation(lineNumber);
        String line = document.get(lineInfo.getOffset(), lineInfo.getLength());
        return line.startsWith(DocumentationCommentIndentsHelper.COMMENT_PREFIX);
    }
}
