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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.common.StringUtils;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 *  Validates method description in documentation comment that is multi-line and may contains parameter definition like:
 * <pre>
 * // ParameterName - ...
 * </pre>
 * where description line begins with one of names of method parameters.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class MultilineDescriptionParameterSuggestionCheck
    extends DocumentationCommentBasicDelegateCheck
{

    private static final String CHECK_ID = "doc-comment-parameter-in-description-suggestion"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MultilineDescriptionParameterSuggestionCheck_title)
            .description(Messages.MultilineDescriptionParameterSuggestionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.TRIVIAL)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(Description.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (root.getMethod().getFormalParams().isEmpty() || !(object.getParent() instanceof BslDocumentationComment)
            || root.getParametersSection() != null)
        {
            return;
        }

        Description description = (Description)object;

        if (description.getParts().isEmpty())
        {
            return;
        }

        int startLine = description.getParts().get(0).getLineNumber();

        //@formatter:off
        TextPart lastPart = description.getParts()
            .stream()
            .filter(TextPart.class::isInstance)
            .map(TextPart.class::cast)
            .reduce((first, second) -> second)
            .orElse(null);
        //@formatter:on

        if (lastPart == null || startLine == lastPart.getLineNumber())
        {
            return;
        }

        List<String> paramNames = root.getMethod()
            .getFormalParams()
            .stream()
            .filter(p -> StringUtils.isValidName(p.getName()))
            .map(FormalParam::getName)
            .collect(Collectors.toList());

        /** The Pattern of parameter search.
         * RegEx: ^\/*\s*(Param1|Param2)\s*-
         **/
        Pattern paramPattern = Pattern.compile("^\\s*(" + String.join("|", paramNames) + ")\\s*-", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        int prevLine = 0;
        for (IDescriptionPart part : description.getParts())
        {
            if (part instanceof TextPart && part.getLineNumber() > startLine && part.getLineNumber() > prevLine)
            {
                String text = ((TextPart)part).getText();
                Matcher matcher = paramPattern.matcher(text);
                if (matcher.find())
                {
                    resultAceptor.addIssue(
                        Messages.MultilineDescriptionParameterSuggestionCheck_Probably_method_parameter_is_defined,
                        part.getLineNumber(), part.getOffset(), matcher.end());
                }
            }
            prevLine = part.getLineNumber();
        }
    }

}
