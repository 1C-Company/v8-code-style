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
import java.util.function.Function;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Section;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
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
    private static final String COMMENT_PREFIX = "//"; //$NON-NLS-1$
    private static final int TABULATION_SPACE_COUNT = 4;

    @Inject
    public IndentsInCommentSectionCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);
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

        List<String> commentTextLines = DocumentationCommentLines.get(documentationComment.getMethod());

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

        int parentIndent = getIndentByLineNumber(commentTextLines, parametersSection.getLineNumber());

        checkIndentedItems(parentIndent, parametersSection.getParameterDefinitions(),
            field -> getDescriptionParts(((FieldDefinition)field).getTypeSections()), commentTextLines, resultAcceptor);
    }

    private void checkReturnsSection(ReturnSection returnsSection, List<String> commentTextLines,
        DocumentationCommentResultAcceptor resultAcceptor)
    {
        if (returnsSection == null)
        {
            return;
        }

        int parentIndent = getIndentByLineNumber(commentTextLines, returnsSection.getLineNumber());

        checkIndentedItems(parentIndent, returnsSection.getReturnTypes(),
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
            int itemIndent = getIndentByLineNumber(commentTextLines, lineNumber);

            if (itemIndent <= parentIndent || (targetIndent != -1 && itemIndent != targetIndent))
            {
                boolean needMore = targetIndent == -1 || targetIndent > itemIndent;
                addIssue(resultAcceptor, lineNumber, offset, needMore);
            }
            else
            {
                targetIndent = itemIndent;
            }

            checkDescriptionsParts(descriptionParts.apply(item), commentTextLines, resultAcceptor, lineNumber,
                itemIndent, true);
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

        int sectionIndent = getIndentByLineNumber(commentTextLines, section.getLineNumber());
        int sectionLineNumber = section.getLineNumber();

        checkDescriptionsParts(section.getDescription().getParts(), commentTextLines, resultAcceptor, sectionLineNumber,
            sectionIndent, false);
    }

    private int getIndentByLineNumber(List<String> lines, int lineNumber)
    {
        if (lines.size() <= lineNumber)
        {
            return -1;
        }

        String line = lines.get(lineNumber);
        return getIndentCount(line);
    }

    private void addIssue(DocumentationCommentResultAcceptor resultAcceptor, int lineNumber, int offset,
        boolean needMoreTabs)
    {
        String message = needMoreTabs ? Messages.IndentsInCommentSectionCheck_IssueFewTabs
            : Messages.IndentsInCommentSectionCheck_IssueManyTabs;

        resultAcceptor.addIssue(message, lineNumber, COMMENT_PREFIX.length(),
            Math.max(offset - COMMENT_PREFIX.length(), 1));
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
     * @param oneLevel indents on description pars can be only on one level
     */
    private void checkDescriptionsParts(List<IDescriptionPart> descriptionParts, List<String> commentTextLines,
        DocumentationCommentResultAcceptor resultAcceptor, int parentLineNumber, int parentIndent, boolean oneLevel)
    {
        int targetDescriptionPartIndent = -1;
        for (IDescriptionPart part : descriptionParts)
        {
            if (part.getLineNumber() != parentLineNumber)
            {
                int descriptionPartIndent = getIndentByLineNumber(commentTextLines, part.getLineNumber());
                boolean invalidIndent = descriptionPartIndent <= parentIndent;

                if (!invalidIndent && targetDescriptionPartIndent != -1)
                {
                    if (oneLevel)
                    {
                        invalidIndent = descriptionPartIndent != targetDescriptionPartIndent;
                    }
                    else
                    {
                        invalidIndent = descriptionPartIndent < targetDescriptionPartIndent;
                    }
                }
                if (invalidIndent)
                {
                    addIssue(resultAcceptor, part.getLineNumber(), part.getOffset(),
                        targetDescriptionPartIndent == -1 ? true : targetDescriptionPartIndent > descriptionPartIndent);
                    continue;
                }

                if (oneLevel)
                    targetDescriptionPartIndent = descriptionPartIndent;

                else if (targetDescriptionPartIndent == -1)
                    targetDescriptionPartIndent = descriptionPartIndent;
            }
        }
    }

    /**
     * get indent count
     * 
     * @param line
     * @return count of spaces, tab = space * TABULATION_SPACE_COUNT
     */
    private int getIndentCount(String line)
    {
        int indent = 0;

        for (int i = 0; i < line.length(); ++i)
        {
            char c = line.charAt(i);

            if (c == ' ')
            {
                ++indent;
            }
            else if (c == '\t')
            {
                indent += TABULATION_SPACE_COUNT;
            }
            else
            {
                break;
            }
        }

        return indent;
    }
}
