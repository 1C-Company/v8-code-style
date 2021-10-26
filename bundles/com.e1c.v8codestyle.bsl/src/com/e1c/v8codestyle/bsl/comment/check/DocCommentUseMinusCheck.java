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
package com.e1c.v8codestyle.bsl.comment.check;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * In Description of Documentation comment model should use only hyphen-minus symbol instead of usual hyphen
 * or different dashes. This check analyze wrong "minus" only in first text part of description which goes after field
 * declaration to catch possible wrong parsing of the documentation comment model.
 *
 * @author Dmitriy Marmyshev
 */
public class DocCommentUseMinusCheck
    extends DocumentationCommentBasicDelegateCheck
{

    private static final String CHECK_ID = "doc-comment-use-minus"; //$NON-NLS-1$

    /** The pattern to find wrong hyphen symbols: [–—‒―⸺⸻‑‐]+
     * Unicode symbols are forbidden:
     * 0x2013 - middle dash –
     * 0x2014 - long dash —
     * 0x2012 - digital dash ‒
     * 0x2015 - horizontal line ―
     * 0x2E3A - double dash ⸺
     * 0x2E3B - triple dash ⸻
     * 0x2010 - hyphen ‐
     * 0x2011 - solid-hyphen ‑
     *
     * Acceptable only hyphen-minus: 0x002D -
     */
    private static final Pattern WRONG_HYPHEN = Pattern.compile("[–—‒―⸺⸻‑‐]+"); //$NON-NLS-1$

    private static final int SHOW_PREV_SYMBOLS = 7;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DocCommentUseMinusCheck_title)
            .description(Messages.DocCommentUseMinusCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .delegate(Description.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (object.getParent() instanceof BslDocumentationComment)
        {
            // Should not restrict any "wrong-minus" symbols in description of root
            return;
        }

        Description descr = (Description)object;

        for (IDescriptionPart part : descr.getParts())
        {
            if (part instanceof TextPart)
            {
                TextPart textPart = (TextPart)part;
                String text = textPart.getText();
                Matcher matcher = WRONG_HYPHEN.matcher(text);
                int previous = 0;
                while (matcher.find())
                {
                    int start = matcher.start();
                    int end = matcher.end();

                    int offset = textPart.getOffset() + start;
                    int lenth = end - start;
                    if ((start - previous) > SHOW_PREV_SYMBOLS)
                    {
                        start = start - SHOW_PREV_SYMBOLS;
                    }
                    else
                    {
                        start = previous;
                    }
                    String symbols = text.substring(start, end);

                    previous = end;

                    String message = MessageFormat.format(
                        Messages.DocCommentUseMinusCheck_Only_hyphen_minus_symbol_is_allowed_in_doc_comment_but_found_0,
                        symbols);

                    resultAceptor.addIssue(message, textPart.getLineNumber(), offset, lenth);
                }
            }
            else
            {
                // analyze only first text part
                break;
            }
        }
    }

}
