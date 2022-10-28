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
package com.e1c.v8codestyle.bsl.comment.check;

import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.bsl.model.Procedure;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check that comment for the export procedure (function) contains Description section
 *
 * @author Olga Bozhko
 */
public class ExportProcedureCommentDescriptionCheck
    extends AbstractDocCommentTypeCheck
{
    private static final String CHECK_ID = "doc-comment-export-method-description-section"; //$NON-NLS-1$
    private static final int STANDARD_NUM = 453;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ExportMethodCommentDescriptionCheck_title)
            .description(Messages.ExportMethodCommentDescriptionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(STANDARD_NUM, getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(BslDocumentationComment.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (!(root.getMethod() instanceof Function || root.getMethod() instanceof Procedure)
            || !root.getMethod().isExport())
        {
            return;
        }

        BslDocumentationComment docComment = (BslDocumentationComment)object;
        BslDocumentationComment.Description description = docComment.getDescription();
        if (description != null && description.getParts().isEmpty())
        {
            resultAceptor.addIssue(MessageFormat.format(
                    Messages.ExportMethodCommentDescriptionCheck_Missing_Description_in_export_procedure_comment,
                root.getMethod().getName()), root.getMethod(), NAMED_ELEMENT__NAME);
        }
    }
}
