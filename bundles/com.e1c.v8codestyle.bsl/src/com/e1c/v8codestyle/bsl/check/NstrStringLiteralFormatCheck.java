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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FEATURE_ACCESS__NAME;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STRING_LITERAL__LINES;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.core.platform.V8ParametersStringParser;
import com._1c.g5.v8.dt.metadata.mdclass.Language;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks {@link StringLiteral} format of NStr function that: <br>
 * 1. the literal content is not empty,  <br>
 * 2. can be parsed with key-value without errors <br>
 * 3. each language code in the literal is known <br>
 * 4. message for each language code in literal is set and not empty<br>
 * 5. message is not ending on blank symbol<br>
 * 6. and optionally may check that each language of the project has translation <br>
 *
 * @author Dmitriy Marmyshev
 */
public class NstrStringLiteralFormatCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "bsl-nstr-string-literal-format"; //$NON-NLS-1$

    private static final String PARAM_CHECK_EMPTY_INTERFACE = "checkEmptyInterface"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_EMPTY_INTERFACE = Boolean.FALSE.toString();

    private static final String NSTR_NAME = "NStr"; //$NON-NLS-1$

    private static final String NSTR_NAME_RU = "НСтр"; //$NON-NLS-1$

    private IV8ProjectManager v8ProjectManager;

    /**
     * Instantiates a new NSTR string literal format check.
     *
     * @param v8ProjectManager the v 8 project manager service, cannot be {@code null}.
     */
    @Inject
    public NstrStringLiteralFormatCheck(IV8ProjectManager v8ProjectManager)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.NstrStringLiteralFormatCheck_title)
            .description(Messages.NstrStringLiteralFormatCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION)
            .parameter(PARAM_CHECK_EMPTY_INTERFACE, Boolean.class, DEFAULT_CHECK_EMPTY_INTERFACE,
                Messages.NstrStringLiteralFormatCheck_Check_empty_interface_for_each_language);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation inv = (Invocation)object;
        if (inv.getParams().isEmpty() || !(NSTR_NAME_RU.equalsIgnoreCase(inv.getMethodAccess().getName())
            || NSTR_NAME.equalsIgnoreCase(inv.getMethodAccess().getName())))
        {
            return; // Not continue
        }

        Expression first = inv.getParams().get(0);
        if (!(first instanceof StringLiteral))
        {
            resultAceptor.addIssue(
                Messages.NstrStringLiteralFormatCheck_NStr_method_should_accept_string_as_first_param,
                inv.getMethodAccess(), FEATURE_ACCESS__NAME);
            return;
        }

        final String fullString =
            String.join(System.lineSeparator(), BslUtil.getStringLiteralContent((StringLiteral)first, false));

        if (StringUtils.isBlank(fullString))
        {
            resultAceptor.addIssue(Messages.NstrStringLiteralFormatCheck_NStr_message_is_empty, first,
                STRING_LITERAL__LINES);
            return;
        }

        V8ParametersStringParser parser = new V8ParametersStringParser(fullString);
        if (!parser.getStatus().isOK())
        {
            String message = MessageFormat.format(Messages.NstrStringLiteralFormatCheck_NStr_format_is_incorrect__E,
                parser.getStatus().getMessage());
            resultAceptor.addIssue(message, first, STRING_LITERAL__LINES);
            return;
        }

        final Set<String> codes = v8ProjectManager.getProject(inv)
            .getLanguages()
            .stream()
            .map(Language::getLanguageCode)
            .collect(Collectors.toSet());

        final Map<String, String> params = parser.getParameters();
        for (Entry<String, String> entry : params.entrySet())
        {
            if (!codes.contains(entry.getKey()))
            {
                String message = MessageFormat.format(
                    Messages.NstrStringLiteralFormatCheck_NStr_contains_unknown_language_code__S, entry.getKey());
                resultAceptor.addIssue(message, first, STRING_LITERAL__LINES);
            }
            else if (StringUtils.isBlank(entry.getValue()))
            {
                String message = MessageFormat.format(
                    Messages.NstrStringLiteralFormatCheck_NStr_message_for_language_code__S__is_empty, entry.getKey());
                resultAceptor.addIssue(message, first, STRING_LITERAL__LINES);
            }
            else if (entry.getValue().endsWith(" ") || entry.getValue().endsWith(System.lineSeparator())) //$NON-NLS-1$
            {
                String message = MessageFormat.format(
                    Messages.NstrStringLiteralFormatCheck_NStr_message_for_code__S__ends_with_space, entry.getKey());
                resultAceptor.addIssue(message, first, STRING_LITERAL__LINES);
            }
        }

        boolean checkEmptyInterface = parameters.getBoolean(PARAM_CHECK_EMPTY_INTERFACE);
        if (inv.getParams().size() == 1 && checkEmptyInterface)
        {
            for (String code : codes)
            {
                if (!params.containsKey(code))
                {
                    String message = MessageFormat.format(
                        Messages.NstrStringLiteralFormatCheck_NStr_message_for_language_code__S__is_empty, code);
                    resultAceptor.addIssue(message, first, STRING_LITERAL__LINES);
                }
            }
        }
    }

}
