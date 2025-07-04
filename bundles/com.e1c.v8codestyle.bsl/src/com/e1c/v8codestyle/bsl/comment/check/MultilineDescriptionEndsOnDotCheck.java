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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Check multi-line description of the documentation comment that last line ends on dot.
 *
 * @author Dmitriy Marmyshev
 */
public class MultilineDescriptionEndsOnDotCheck
    extends DocumentationCommentBasicDelegateCheck
{

    private static final String CHECK_ID = "doc-comment-description-ends-on-dot"; //$NON-NLS-1$

    /**
     * Constructs an instance
     *
     * @param resourceLookup service for look up workspace resources, see {@link IResourceLookup}, cannot be <code>null</code>
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     */
    @Inject
    public MultilineDescriptionEndsOnDotCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MultilineDescriptionEndsOnDotCheck_title)
            .description(Messages.MultilineDescriptionEndsOnDotCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.TRIVIAL)
            .issueType(IssueType.CODE_STYLE)
            .disable()
            .delegate(Description.class);

    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        Description description = (Description)object;

        if (description == null || description.getParts().isEmpty())
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

        if (lastPart == null || startLine == lastPart.getLineNumber() || isParentExampleSection(root, lastPart))
        {
            return;
        }

        String lastLine = lastPart.getText();

        if (lastLine != null)
        {
            lastLine = lastLine.trim();
        }
        if (lastLine != null && lastLine.endsWith(".")) //$NON-NLS-1$
        {
            return;
        }

        resultAceptor.addIssue(Messages.MultilineDescriptionEndsOnDotCheck_Method_comment_doesnt_ends_on_dot,
            lastPart.getLineNumber(), lastPart.getOffset(), lastPart.getText().length());

    }

    private boolean isParentExampleSection(BslDocumentationComment root, TextPart lastPart)
    {
        return root.getExampleSection() != null
            && root.getExampleSection().getDescription().getParts().contains(lastPart);
    }
}
