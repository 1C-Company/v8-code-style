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
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.common.StringUtils;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Validates {@link LinkPart} of documentation comment that it referenced to an existing object.
 * This check allows to skip  link with single word in {@link Description}
 * <pre>
 * // Method description here.
 * // And also see something here...
 * </pre>
 * but it validates this
 * <pre>
 * // Method description here.
 * // And also see some.thing here
 * </pre>
 *  as reference to method "thing" of common module "some".
 *
 * @author Dmitriy Marmyshev
 */
public class RefLinkPartCheck
    extends AbstractDocCommentTypeCheck
{

    private static final String CHECK_ID = "doc-comment-ref-link"; //$NON-NLS-1$

    private static final String PARAMETER_ALLOW_SEE_IN_DESCRIPTION = "allowSeeInDescription"; //$NON-NLS-1$

    private final IScopeProvider scopeProvider;

    /**
     * Instantiates a new reference link part check.
     *
     * @param scopeProvider the scope provider service, cannot be {@code null}.
     */
    @Inject
    public RefLinkPartCheck(IScopeProvider scopeProvider)
    {
        this.scopeProvider = scopeProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RefLinkPartCheck_title)
            .description(Messages.RefLinkPartCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(LinkPart.class);
        builder.parameter(PARAMETER_ALLOW_SEE_IN_DESCRIPTION, Boolean.class, Boolean.TRUE.toString(),
            Messages.RefLinkPartCheck_Allow_See_in_description);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        LinkPart linkPart = (LinkPart)object;

        boolean isSingleWordInDescription = parameters.getBoolean(PARAMETER_ALLOW_SEE_IN_DESCRIPTION)
            && object.getParent() instanceof Description && linkPart.getPartsWithOffset().size() == 1;

        if (!isWebLink(linkPart) && !isSingleWordInDescription
            && getLinkPartLastObject(linkPart, scopeProvider, root.getMethod()).isEmpty())
        {
            resultAceptor.addIssue(Messages.RefLinkPartCheck_Link_referenced_to_unexisting_object,
                linkPart.getLineNumber(), linkPart.getLinkTextOffset(), linkPart.getLinkText().length());
        }
    }

    private boolean isWebLink(LinkPart linkPart)
    {
        String text = linkPart.getLinkText();
        if (StringUtils.isEmpty(text))
        {
            // just skip other checks
            return true;
        }
        return text.startsWith("http://") || text.startsWith("https://") || text.startsWith("ftp://"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
