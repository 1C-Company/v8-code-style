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

import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.comment.DocumentationCommentProperties;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Validator of export function that has return section in documentation comment if function has anything
 * in documentation comment. If function has link to some other function this means documentation inherites and
 * validator checks documentation comment of linked function. Otherwise this function should have return section.
 *
 * @author Dmitriy Marmyshev
 */
public class ExportFunctionReturnSectionCheck
    extends AbstractDocCommentTypeCheck
{
    private static final String CHECK_ID = "doc-comment-export-function-return-section"; //$NON-NLS-1$

    private final IResourceLookup resourceLookup;

    private final IBslPreferences bslPreferences;

    private final IScopeProvider scopeProvider;

    private final BslMultiLineCommentDocumentationProvider commentProvider;

    /**
     * Instantiates a new check of export function return section.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     */
    @Inject
    public ExportFunctionReturnSectionCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences)
    {
        this.resourceLookup = resourceLookup;
        this.bslPreferences = bslPreferences;
        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        this.commentProvider = rsp.get(BslMultiLineCommentDocumentationProvider.class);
        this.scopeProvider = rsp.get(IScopeProvider.class);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ExportFunctionReturnSectionCheck_title)
            .description(Messages.ExportFunctionReturnSectionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .delegate(BslDocumentationComment.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (!(root.getMethod() instanceof Function) || !root.getMethod().isExport())
        {
            return;
        }

        BslDocumentationComment docComment = (BslDocumentationComment)object;
        if (isInheritedFromLink(docComment))
        {
            LinkPart linkPart = getSingleLinkPart(docComment.getDescription());

            IProject project = resourceLookup.getProject(root.getModule());
            DocumentationCommentProperties props = bslPreferences.getDocumentCommentProperties(project);

            docComment = BslCommentUtils.getLinkPartCommentContent(linkPart, scopeProvider, commentProvider,
                props.oldCommentFormat(), root.getMethod());
        }

        if (docComment.getReturnSection() == null || docComment.getReturnSection().getReturnTypes().isEmpty())
        {
            resultAceptor.addIssue(Messages.ExportFunctionReturnSectionCheck_Export_function_return_section_required,
                root.getMethod(), NAMED_ELEMENT__NAME);
        }
    }

    private boolean isInheritedFromLink(BslDocumentationComment docComment)
    {
        return docComment.getReturnSection() == null && docComment.getParametersSection() == null
            && getSingleLinkPart(docComment.getDescription()) != null;
    }
}
