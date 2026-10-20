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
import java.util.OptionalInt;
import java.util.function.Function;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Section;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
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
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * The check for valid indentation in method doc comment
 * 
 * @author Artem Samohvalov
 */
public class IndentsInCommentSectionCheck
    extends DocumentationCommentBasicDelegateCheck
{
    private final BslMultiLineCommentDocumentationProvider commentProvider;

    @Inject
    public IndentsInCommentSectionCheck(BslMultiLineCommentDocumentationProvider commentProvider,
        IResourceLookup resourceLookup, INamingService namingService, IBmModelManager bmModelManager,
        IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);

        this.commentProvider = commentProvider;
    }

    @Override
    public String getCheckId()
    {
        return "indents-in-comment-section"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.IndentsInCommentSectionCheck_Title)
            .description(Messages.IndentsInCommentSectionCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(453, getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(BslDocumentationComment.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAcceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        BslDocumentationComment documentationComment = (BslDocumentationComment)object;

        Method method = documentationComment.getMethod();
        if (method == null)
        {
            return;
        }

        List<String> commentTextLines = commentProvider.getCommentLines(method);

        if (commentTextLines.isEmpty())
        {
            return;
        }

        ParametersSection parametersSection = documentationComment.getParametersSection();
        ReturnSection returnSection = documentationComment.getReturnSection();
        Section callOptionsSection = documentationComment.getCallOptionsSection();
        Section exampleSection = documentationComment.getExampleSection();

        checkParametersSection(parametersSection, commentTextLines, resultAcceptor);
        checkReturnsSection(returnSection, commentTextLines, resultAcceptor);
        checkSection(callOptionsSection, commentTextLines, resultAcceptor);
        checkSection(exampleSection, commentTextLines, resultAcceptor);
    }

    private void checkParametersSection(ParametersSection parametersSection, List<String> commentTextLines,
        DocumentationCommentResultAcceptor resultAcceptor)
    {
        if (parametersSection == null)
        {
            return;
        }

        OptionalInt parentIndent = getIndentByLineNumber(commentTextLines, parametersSection.getLineNumber());
        if (parentIndent.isEmpty())
        {
            return;
        }

        checkIndentedItems(parentIndent.getAsInt(), parametersSection.getParameterDefinitions(),
            field -> getDescriptionParts(((FieldDefinition)field).getTypeSections()), commentTextLines, resultAcceptor);
    }

    private void checkReturnsSection(ReturnSection returnsSection, List<String> commentTextLines,
        DocumentationCommentResultAcceptor resultAcceptor)
    {
        if (returnsSection == null)
        {
            return;
        }

        OptionalInt parentIndent = getIndentByLineNumber(commentTextLines, returnsSection.getLineNumber());
        if (parentIndent.isEmpty())
        {
            return;
        }

        checkIndentedItems(parentIndent.getAsInt(), returnsSection.getReturnTypes(),
            type -> ((TypeSection)type).getDescription().getParts(), commentTextLines, resultAcceptor);
    }

    private void checkIndentedItems(int parentIndent, List<? extends IDescriptionPart> items,
        Function<IDescriptionPart, List<IDescriptionPart>> descriptionParts, List<String> commentTextLines,
        DocumentationCommentResultAcceptor resultAcceptor)
    {
        int targetIndent = -1;

        for (IDescriptionPart item : items)
        {
            int lineNumber = item.getLineNumber();
            int offset = item.getOffset();

            OptionalInt itemIndent = getIndentByLineNumber(commentTextLines, lineNumber);
            if (itemIndent.isEmpty())
            {
                continue;
            }

            int indent = itemIndent.getAsInt();

            if (indent <= parentIndent || (targetIndent != -1 && indent != targetIndent))
            {
                boolean needMore = targetIndent == -1 || targetIndent > indent;
                addIssue(resultAcceptor, lineNumber, offset, needMore);
            }
            else
            {
                targetIndent = indent;
            }

            checkDescriptionsParts(descriptionParts.apply(item), commentTextLines, resultAcceptor, lineNumber, indent,
                true);
        }
    }

    /**
     * Check section. Check whether there is an indentation on the 2nd and subsequent lines.
     * For example and call options section
     *
     * @param section the section
     * @param commentTextLines the comment text lines
     * @param resultAcceptor the result acceptor
     */
    private void checkSection(Section section, List<String> commentTextLines,
        DocumentationCommentResultAcceptor resultAcceptor)
    {
        if (section == null)
        {
            return;
        }

        OptionalInt sectionIndent = getIndentByLineNumber(commentTextLines, section.getLineNumber());
        if (sectionIndent.isEmpty())
        {
            return;
        }

        checkDescriptionsParts(section.getDescription().getParts(), commentTextLines, resultAcceptor,
            section.getLineNumber(), sectionIndent.getAsInt(), false);
    }

    private OptionalInt getIndentByLineNumber(List<String> lines, int lineNumber)
    {
        if (lineNumber < 0 || lines.size() <= lineNumber)
        {
            return OptionalInt.empty();
        }

        return OptionalInt.of(DocumentationCommentIndentsHelper.getIndentWidth((lines.get(lineNumber))));
    }

    private void addIssue(DocumentationCommentResultAcceptor resultAcceptor, int lineNumber, int offset,
        boolean needMoreTabs)
    {
        String message = needMoreTabs ? Messages.IndentsInCommentSectionCheck_IssueFewTabs
            : Messages.IndentsInCommentSectionCheck_IssueManyTabs;

        resultAcceptor.addIssue(message, lineNumber, DocumentationCommentIndentsHelper.COMMENT_PREFIX_LENGTH,
            Math.max(offset - DocumentationCommentIndentsHelper.COMMENT_PREFIX_LENGTH, 1));
    }

    private List<IDescriptionPart> getDescriptionParts(List<TypeSection> typeSection)
    {
        //@formatter:off
        return typeSection.stream()
            .flatMap(type -> type.getDescription().getParts().stream())
            .toList();
        //@formatter:on
    }

    /**
     * Check descriptions parts.
     *
     * @param descriptionParts the description parts
     * @param commentTextLines the comment text lines
     * @param resultAcceptor the result acceptor
     * @param parentLineNumber the parent line number
     * @param parentIndent the parent indent
     * @param oneLevel indents on description parts can be only on one level
     */
    private void checkDescriptionsParts(List<IDescriptionPart> descriptionParts, List<String> commentTextLines,
        DocumentationCommentResultAcceptor resultAcceptor, int parentLineNumber, int parentIndent, boolean oneLevel)
    {
        int targetDescriptionPartIndent = -1;

        for (IDescriptionPart part : descriptionParts)
        {
            if (part.getLineNumber() == parentLineNumber)
            {
                continue;
            }

            OptionalInt partIndent = getIndentByLineNumber(commentTextLines, part.getLineNumber());
            if (partIndent.isEmpty())
            {
                continue;
            }

            int descriptionPartIndent = partIndent.getAsInt();
            boolean invalidIndent = descriptionPartIndent <= parentIndent;

            if (!invalidIndent && targetDescriptionPartIndent != -1)
            {
                invalidIndent = oneLevel ? descriptionPartIndent != targetDescriptionPartIndent
                    : descriptionPartIndent < targetDescriptionPartIndent;
            }

            if (invalidIndent)
            {
                addIssue(resultAcceptor, part.getLineNumber(), part.getOffset(),
                    targetDescriptionPartIndent == -1 || targetDescriptionPartIndent > descriptionPartIndent);
                continue;
            }

            if (oneLevel || targetDescriptionPartIndent == -1)
            {
                targetDescriptionPartIndent = descriptionPartIndent;
            }
        }
    }
}
