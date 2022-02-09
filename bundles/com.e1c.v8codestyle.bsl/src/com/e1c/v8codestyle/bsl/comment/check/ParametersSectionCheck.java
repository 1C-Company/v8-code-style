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
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 *  Validates that each of parameters specified in documenting comments.
 *
 * @author Maxim Degtyarev
 * @author Dmitriy Marmyshev
 *
 */
public class ParametersSectionCheck
    extends DocumentationCommentBasicDelegateCheck
{

    private static final String CHECK_ID = "doc-comment-parameter-section"; //$NON-NLS-1$

    private static final String PARAMETER_CHECK_ONLY_EXPORT = "checkOnlyExport"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ParametersSectionCheck_title)
            .description(Messages.ParametersSectionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(ParametersSection.class);
        builder.parameter(PARAMETER_CHECK_ONLY_EXPORT, Boolean.class, Boolean.FALSE.toString(),
            Messages.ParametersSectionCheck_Check_only_export_method_parameter_section);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        ParametersSection parameterSection = (ParametersSection)object;

        if (root.getMethod().getFormalParams().isEmpty())
        {
            resultAceptor.addIssue(Messages.ParametersSectionCheck_Remove_useless_parameter_section,
                parameterSection.getHeaderKeywordLength());
            return;
        }

        if (!root.getMethod().isExport() && parameters.getBoolean(PARAMETER_CHECK_ONLY_EXPORT))
        {
            return;
        }

        Set<String> parameterNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        root.getMethod().getFormalParams().forEach(p -> parameterNames.add(p.getName()));

        for (FieldDefinition parameterDefinition : parameterSection.getParameterDefinitions())
        {
            parameterNames.remove(parameterDefinition.getName());
        }
        if (!parameterNames.isEmpty())
        {
            String message = MessageFormat.format(Messages.ParametersSectionCheck_Parameter_definition_missed_for__N,
                String.join(", ", parameterNames)); //$NON-NLS-1$
            resultAceptor.addIssue(message, parameterSection.getHeaderKeywordLength());
        }
    }

}
