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
package com.e1c.v8codestyle.ql.check;

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.STRING_LITERAL_EXPRESSION__CONTENT;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.ql.model.StringLiteralExpression;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.ql.CorePlugin;

/**
 * The check QL string literal contains only camel-case words and not contains any special symbols mixes with letters.
 *
 * @author Dmitriy Marmyshev
 */
public class CamelCaseStringLiteral
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-camel-case-string-literal"; //$NON-NLS-1$

    private static final String PARAMETER_SKIP_CONTENT_PATTERN = "skipContentPattern"; //$NON-NLS-1$

    private static final Pattern WORD_PATTERN = Pattern.compile("\\p{L}+", Pattern.UNICODE_CHARACTER_CLASS); //$NON-NLS-1$

    private static final Pattern NON_WORD_PATTERN = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS); //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CamelCaseStringLiteral_title)
            .description(Messages.CamelCaseStringLiteral_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(762, getCheckId(), CorePlugin.PLUGIN_ID))
            .delegate(StringLiteralExpression.class);
        builder.parameter(PARAMETER_SKIP_CONTENT_PATTERN, String.class, StringUtils.EMPTY,
            Messages.CamelCaseStringLiteral_Regular_expression_to_skip_literal_content);
    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor resultAceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {
        StringLiteralExpression literal = (StringLiteralExpression)object;
        String content = literal.getContent();
        if (content == null)
        {
            return;
        }

        if (content.length() > 2)
        {
            content = content.substring(1, content.length() - 1);
        }

        String skipContentPattern = parameters.getString(PARAMETER_SKIP_CONTENT_PATTERN);
        if (skipContentPattern != null && content.matches(skipContentPattern))
        {
            return;
        }

        Matcher matcher = NON_WORD_PATTERN.matcher(content);
        if (matcher.find() && WORD_PATTERN.matcher(content).find())
        {
            String message =
                MessageFormat.format(Messages.CamelCaseStringLiteral_String_literal_contains_non_CamelCase_symbols__0,
                    content.substring(matcher.start(), matcher.end()));
            resultAceptor.addIssue(message, literal, STRING_LITERAL_EXPRESSION__CONTENT);
        }
    }

}
