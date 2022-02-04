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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.model.Procedure;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Validates any description in documentation comment that is multi-line and may contains File definition like:
 * <pre>
 * // * FieldName - ...
 * </pre>
 * where description line begins with one or more stars and then a name of field.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class MultilineDescriptionFieldSuggestionCheck
    extends DocumentationCommentBasicDelegateCheck
{

    private static final String CHECK_ID = "doc-comment-field-in-description-suggestion"; //$NON-NLS-1$

    /** The Pattern of field search.
     * RegEx: ^\s*\*+\s*\w+\s*-
     **/
    private static final Pattern PATTERN_FIELD =
        Pattern.compile("^\\s*\\*+\\s*\\w+\\s*-", Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MultilineDescriptionFieldSuggestionCheck_title)
            .description(Messages.MultilineDescriptionFieldSuggestionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.TRIVIAL)
            .issueType(IssueType.CODE_STYLE)
            .delegate(Description.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        Description description = (Description)object;

        if (description.getParts().isEmpty() || (root.getMethod().getFormalParams().isEmpty()
            && (root.getMethod() instanceof Procedure || object.getParent() instanceof BslDocumentationComment)))
        {
            // do not check if empty, or Procedure has no params, or it is function description with no params
            return;
        }

        int startLine = description.getParts().get(0).getLineNumber();

        TextPart lastPart = description.getParts()
            .stream()
            .filter(TextPart.class::isInstance)
            .map(TextPart.class::cast)
            .reduce((first, second) -> second)
            .orElse(null);

        if (lastPart == null || startLine == lastPart.getLineNumber())
        {
            return;
        }

        int prevLine = 0;
        for (IDescriptionPart part : description.getParts())
        {
            if (part instanceof TextPart && part.getLineNumber() > startLine && part.getLineNumber() > prevLine)
            {
                String text = ((TextPart)part).getText();
                Matcher matcher = PATTERN_FIELD.matcher(text);
                if (matcher.find())
                {
                    resultAceptor.addIssue(
                        Messages.MultilineDescriptionFieldSuggestionCheck_Probably_Field_is_defined_in_description,
                        part.getLineNumber(), part.getOffset(), matcher.end());
                }
            }
            prevLine = part.getLineNumber();
        }
    }

}
