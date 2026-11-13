/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.DocumentationCommentIndentsHelper;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * The check finds mixed tabs and spaces in doc comment section
 * 
 * @author Artem Samohvalov
 */
public class MixedTabsAndSpacesInCommentCheck
    extends DocumentationCommentBasicDelegateCheck
{
    private final BslMultiLineCommentDocumentationProvider commentProvider;

    @Inject
    public MixedTabsAndSpacesInCommentCheck(BslMultiLineCommentDocumentationProvider commentProvider,
        IResourceLookup resourceLookup, INamingService namingService, IBmModelManager bmModelManager,
        IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);

        this.commentProvider = commentProvider;
    }

    @Override
    public String getCheckId()
    {
        return "mixed-tabs-and-spaces-in-comment"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MixedTabsAndSpacesInComment_Title)
            .description(Messages.MixedTabsAndSpacesInComment_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(BslDocumentationComment.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart descriptionPart, BslDocumentationComment rootNode,
        DocumentationCommentResultAcceptor resultAcceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor progressMonitor)
    {
        BslDocumentationComment documentationComment = (BslDocumentationComment)descriptionPart;

        Method method = documentationComment.getMethod();
        if (method == null)
        {
            return;
        }

        List<String> lines = commentProvider.getCommentLines(method);
        List<Integer> mixedLines = DocumentationCommentIndentsHelper.findMixedLines(lines, true);
        if (mixedLines.isEmpty())
        {
            return;
        }

        int lineNumber = mixedLines.get(0);
        int indentLength = DocumentationCommentIndentsHelper.getIndentLength(lines.get(lineNumber));
        resultAcceptor.addIssue(Messages.MixedTabsAndSpacesInComment_Issue, lineNumber,
            DocumentationCommentIndentsHelper.COMMENT_PREFIX_LENGTH, indentLength);
    }
}
