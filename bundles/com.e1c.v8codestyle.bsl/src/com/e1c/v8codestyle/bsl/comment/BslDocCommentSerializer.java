/*******************************************************************************
 * Copyright (C) 2021, 2024, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.comment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Section;
import com._1c.g5.v8.dt.bsl.documentation.comment.IBslCommentToken;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/** Bsl Documentation comment model serializer. Allows to serialize comment to the string with new or old format, set
 * up some formatting style.
 *
 * @author Dmitriy Marmyshev
 *
 */
public abstract class BslDocCommentSerializer
{

    /**
     * Creates new Bsl {@link BslDocumentationComment comment} {@link BslDocCommentSerializer} builder.
     *
     * @return the documentation comment serializer builder
     */
    public static final Builder newBuilder()
    {
        return new Builder();
    }

    private static final int DEFAULT_SECTION_SPACE = 2;

    protected static final String ONE_SPACE = " "; //$NON-NLS-1$

    private static final String DEFAULT_INDENT = "    "; //$NON-NLS-1$

    protected final FormatterSettings settings;

    private boolean ignoreLineNumbers;

    private final boolean isRu;

    private String lineSeparator = System.lineSeparator();

    private String lineFormatter;

    private String indent;


    /**
     * Instantiates a new BSL documentation comment serializer with default section space = 2 and ignoring line numbers
     * in the model.
     * @param isRussian true, if need to use Russian keywords.
     */
    protected BslDocCommentSerializer(FormatterSettings settings, boolean isRussian)
    {
        this.settings = settings;
        this.isRu = isRussian;
        this.ignoreLineNumbers = true;
    }


    /**
     * Checks if serializer ignores line numbers in {@link BslDocumentationComment model} objects.
     *
     * @return true, if serializer is ignoring line numbers
     */
    public boolean isIgnoreLineNumbers()
    {
        return ignoreLineNumbers;
    }

    /**
     * Checks if need to use Russian keywords.
     *
     * @return true, if need to use Russian keywords.
     */
    public boolean isRussian()
    {
        return isRu;
    }

    /**
     * Checks if string presentation must be in old format or in new format
     *
     * @return true, if <code>true</code> if string presentation must be in old format, <code>false</code> in new format
     */
    public boolean isOldFormat()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the line separator.
     *
     * @return the line separator, cannot return {@code null}.
     */
    public String getLineSeparator()
    {
        return lineSeparator;
    }

    /**
     * Serializes comment to the string with default line formatter (as line separator) and default indent symbols.
     *
     * @param comment the documentation comment model
     * @return string presentation of the documentation comment, never <code>null</code>, if description is empty
     * line with {@link IBslCommentToken#LINE_STARTER} will be returned
     */
    public String serialize(BslDocumentationComment comment)
    {
        return serialize(comment, getLineSeparator(), DEFAULT_INDENT);
    }

    protected int serialize(int currentLine, IDescriptionPart part, int alignmentSpaces, int starNumber,
        StringBuilder builder)
    {
        if (part instanceof BslDocumentationComment)
        {

        }
        return currentLine;
    }

    protected int serialize(int currentLine, Collection<IDescriptionPart> parts, int alignmentSpaces, int starNumber,
        StringBuilder builder)
    {
        for (IDescriptionPart part : parts)
        {
            currentLine = serialize(currentLine, part, alignmentSpaces, starNumber, builder);
        }

        return currentLine;
    }

    /**
     * Serializes comment to the string.
     *
     * @param comment the documentation comment model
     * @param lineFormatter symbols added before {@link IBslCommentToken#LINE_STARTER} for each line, cannot be <code>null</code>
     * @param indent symbols for indent extra fields, cannot be <code>null</code>
     * @return string presentation of the documentation comment, never <code>null</code>, if description is empty line with {@link IBslCommentToken#LINE_STARTER} will be returned
     */
    public String serialize(BslDocumentationComment comment, String lineFormatter, String indent)
    {
        lineFormatter = lineFormatter.replace(getLineSeparator(), ""); //$NON-NLS-1$
        StringBuilder builder = new StringBuilder();
        builder.append(IBslCommentToken.LINE_STARTER).append(ONE_SPACE.repeat(settings.getLineStartSpace()));

        int currentLine = 0;
        currentLine = serializeDeprecated(currentLine, comment, lineFormatter, builder);

        currentLine = serializeDescription(currentLine, comment.getDescription(), lineFormatter, indent, 0, builder);
        if (comment.getParametersSection() != null
            && (!comment.getParametersSection().getDescription().getParts().isEmpty()
                || !comment.getParametersSection().getParameterDefinitions().isEmpty()))
        {
            ParametersSection section = comment.getParametersSection();
            currentLine = serialize(currentLine, section, lineFormatter, indent, builder);
        }
        if (comment.getCallOptionsSection() != null
            && !comment.getCallOptionsSection().getDescription().getParts().isEmpty())
        {
            Section section = comment.getCallOptionsSection();
            currentLine = serializeCallOptionsSection(currentLine, section, lineFormatter, indent, builder);
        }
        if (comment.getReturnSection() != null && (!comment.getReturnSection().getDescription().getParts().isEmpty()
            || !comment.getReturnSection().getReturnTypes().isEmpty()))
        {
            ReturnSection section = comment.getReturnSection();
            currentLine = serialize(currentLine, section, lineFormatter, indent, builder);
        }
        if (comment.getExampleSection() != null && !comment.getExampleSection().getDescription().getParts().isEmpty())
        {
            Section section = comment.getExampleSection();
            currentLine = serializeExampleSection(currentLine, section, lineFormatter, indent, builder);
        }
        return builder.toString();
    }

    protected int serializeDeprecated(int currentLine, BslDocumentationComment comment, String lineFormatter,
        StringBuilder builder)
    {
        if (comment.isDeprecated())
        {
            builder.append(isRussian() ? IBslCommentToken.DEPRECATED_RU : IBslCommentToken.DEPRECATED);
            if (!isIgnoreLineNumbers() || !comment.getDescription().getParts().isEmpty())
            {
                if (settings.getDedepricatedSpace() > 0)
                {
                    int lineNumber = currentLine + settings.getDedepricatedSpace();
                    addEmptyLines(currentLine, lineNumber, lineFormatter, 0, builder);
                    currentLine = lineNumber;
                }
                else
                {
                    builder.append(ONE_SPACE);
                }
            }
        }
        return currentLine;
    }

    protected int serializeExampleSection(int currentLine, Section section, String lineFormatter, String indent,
        StringBuilder builder)
    {
        int lineNumber = isIgnoreLineNumbers() ? currentLine + settings.getSectionSpace() : section.getLineNumber();
        addEmptyLines(currentLine, lineNumber, lineFormatter, 0, builder);
        currentLine = lineNumber;
        builder.append(isRussian() ? IBslCommentToken.EXAMPLE_RU : IBslCommentToken.EXAMPLE);
        if (!isIgnoreLineNumbers() || !section.getDescription().getParts().isEmpty())
        {
            builder.append(ONE_SPACE.repeat(settings.getSpaceBeforeDescription()));
        }
        return serializeDescription(currentLine, section.getDescription(), lineFormatter, indent, 0, builder);
    }

    protected abstract int serialize(int currentLine, ReturnSection section, String lineFormatter, String indent,
        StringBuilder builder);

    protected int serialize(int currentLine, ParametersSection section, String lineFormatter, String indent,
        StringBuilder builder)
    {
        int lineNumber = isIgnoreLineNumbers() ? currentLine + settings.getSectionSpace() : section.getLineNumber();
        addEmptyLines(currentLine, lineNumber, lineFormatter, 0, builder);
        currentLine = lineNumber;
        builder.append(isRussian() ? IBslCommentToken.PARAMETERS_RU : IBslCommentToken.PARAMETERS);
        if (!isIgnoreLineNumbers() || !section.getDescription().getParts().isEmpty())
        {
            builder.append(ONE_SPACE.repeat(settings.getSpaceBeforeDescription()));
        }
        currentLine = serializeDescription(currentLine, section.getDescription(), lineFormatter, indent, 0, builder);
        int alignmentSpaces = -indent.length(); // shift first parameters fields to minus alignment length
        currentLine = serializeFieldDefinitions(currentLine, section.getParameterDefinitions(), lineFormatter, indent,
            alignmentSpaces, 0, builder);
        return currentLine;
    }

    protected int serializeCallOptionsSection(int currentLine, Section section, String lineFormatter, String indent,
        StringBuilder builder)
    {
        int lineNumber = isIgnoreLineNumbers() ? currentLine + settings.getSectionSpace() : section.getLineNumber();
        addEmptyLines(currentLine, lineNumber, lineFormatter, 0, builder);
        currentLine = lineNumber;
        builder.append(isRussian() ? IBslCommentToken.CALL_OPTIONS_RU : IBslCommentToken.CALL_OPTIONS);
        if (!isIgnoreLineNumbers() || !section.getDescription().getParts().isEmpty())
        {
            builder.append(ONE_SPACE.repeat(settings.getSpaceBeforeDescription()));
        }
        currentLine = serializeDescription(currentLine, section.getDescription(), lineFormatter, indent, 0, builder);
        return currentLine;
    }

    /**
     * Adds empty line comment in Bsl documentation comment serialization
     * @param currentLine actual line in Bsl documentation comment for starting write of new content
     * @param nextPartLine number of line for writing next comment information
     * @param lineFormatter symbols added before {@link IBslCommentToken#LINE_STARTER} for each line, cannot be <code>null</code>
     * @param alignmentSpaces number of spaces for alignment emty lines in whole comment content
     * @param builder{@link StringBuilder} for append string content of the empty lines, cannot be <code>null</code>
     */
    protected void addEmptyLines(int currentLine, int nextPartLine, String lineFormatter, int alignmentSpaces,
        StringBuilder builder)
    {
        if (nextPartLine > currentLine)
        {
            int count = nextPartLine - currentLine - 1;
            for (int i = 0; i < count; ++i)
            {
                builder.append(getLineSeparator())
                    .append(lineFormatter)
                    .append(IBslCommentToken.LINE_STARTER)
                    .append(ONE_SPACE.repeat(settings.getLineStartSpace()));
            }
            builder.append(getLineSeparator())
                .append(lineFormatter)
                .append(IBslCommentToken.LINE_STARTER)
                .append(ONE_SPACE.repeat(settings.getLineStartSpace()))
                .append(ONE_SPACE.repeat(alignmentSpaces));
        }
    }

    protected int serializeDescription(int currentLine, Description description, String lineFormatter, String indent,
        int alignmentSpaces, StringBuilder builder)
    {
        return serializeDescription(currentLine, description.getParts(), lineFormatter, indent, alignmentSpaces,
            builder);
    }

    protected int serializeDescription(int currentLine, List<IDescriptionPart> parts, String lineFormatter,
        String indent, int alignmentSpaces, StringBuilder builder)
    {
        boolean isPrevTextPart = false;
        boolean firstPart = true;
        for (IDescriptionPart part : parts)
        {
            int lineNumber = isIgnoreLineNumbers() ? currentLine : part.getLineNumber();
            if (isPrevTextPart && isIgnoreLineNumbers())
            {
                lineNumber = currentLine + 1;
            }

            addEmptyLines(currentLine, lineNumber, lineFormatter, alignmentSpaces, builder);
            currentLine = lineNumber;

            isPrevTextPart = false;
            if (part instanceof TextPart)
            {
                String textPart = ((TextPart)part).getText();
                builder.append(firstPart ? textPart.stripLeading() : textPart);
                isPrevTextPart = true;
            }
            else if (part instanceof LinkPart)
            {
                currentLine = serialize(currentLine, (LinkPart)part, lineFormatter, indent, builder);
            }
            else if (part instanceof TypeSection)
            {
                currentLine =
                    serialize(currentLine, (TypeSection)part, lineFormatter, indent, alignmentSpaces, 0, builder);
            }
            firstPart = false;
        }
        return currentLine;
    }

    protected abstract int serializeFieldDefinitions(int currentLine, List<FieldDefinition> fieldExtension,
        String lineFormatter, String indent, int alignmentSpaces, int starNumber, StringBuilder builder);

    protected abstract int serialize(int currentLine, TypeSection typeSection, String lineFormatter, String indent,
        int alignmentSpaces, int starNumber, StringBuilder builder);

    protected int serialize(int currentLine, LinkPart linkPart, String lineFormatter, String indent,
        StringBuilder builder)
    {
        if (settings.isLinkInBrackets())
        {
            builder.append(IBslCommentToken.LINK_SECTION_LEFT_BRACKET);
        }

        if (isRussian())
        {
            builder.append(IBslCommentToken.LINK_RU);
        }
        else
        {
            builder.append(IBslCommentToken.LINK);
        }
        builder.append(' ');
        builder.append(linkPart.getLinkText());
        if (settings.isLinkInBrackets())
        {
            builder.append(IBslCommentToken.LINK_SECTION_RIGHT_BRACKET);
        }
        return currentLine;
    }

    public static class CurrentFormatDocCommentSerializer
        extends BslDocCommentSerializer
    {
        private CurrentFormatDocCommentSerializer(FormatterSettings settings, boolean isRussian)
        {
            super(settings, isRussian);
        }

        @Override
        public boolean isOldFormat()
        {
            return true;
        }

        @Override
        protected int serializeFieldDefinitions(int currentLine, List<FieldDefinition> fieldExtension,
            String lineFormatter, String indent, int alignmentSpaces, int starNumber, StringBuilder builder)
        {
            for (int i = 0; i < fieldExtension.size(); ++i)
            {
                FieldDefinition field = fieldExtension.get(i);
                if (field.getName() == null || field.getName().isEmpty())
                {
                    continue;
                }
                builder.append(getLineSeparator())
                    .append(lineFormatter)
                    .append(IBslCommentToken.LINE_STARTER)
                    .append(ONE_SPACE.repeat(settings.getLineStartSpace()));
                ++currentLine;
                //add space for alignment start and end of type section
                if (settings.isAlignFields())
                {
                    builder.append(ONE_SPACE.repeat(Math.max(0, alignmentSpaces)));
                }
                builder.append(Strings.repeat(IBslCommentToken.STAR, starNumber)).append(' ');
                builder.append(field.getName()).append(' ');
                if (!field.getTypeSections().isEmpty())
                {
                    //only one type section allowed in old format
                    TypeSection typeSection = field.getTypeSections().get(0);
                    builder.append(IBslCommentToken.TYPE_SECTION_DASH).append(' ');
                    currentLine = serialize(currentLine, typeSection, lineFormatter, indent,
                        indent.length() + alignmentSpaces, starNumber + 1, builder);
                    if (!field.getDescription().getParts().isEmpty())
                    {
                        //add new line for the end of type section
                        builder.append(getLineSeparator())
                            .append(lineFormatter)
                            .append(IBslCommentToken.LINE_STARTER)
                            .append(ONE_SPACE.repeat(settings.getLineStartSpace()));
                        ++currentLine;
                        //add space for alignment start and end of type section
                        builder.append(ONE_SPACE.repeat(indent.length() + alignmentSpaces));
                        for (IDescriptionPart part : field.getDescription().getParts())
                        {
                            if (part instanceof TextPart)
                            {
                                builder.append(((TextPart)part).getText());
                            }
                            else if (part instanceof LinkPart)
                            {
                                currentLine = serialize(currentLine, ((LinkPart)part), lineFormatter, indent, builder);
                            }
                        }
                    }
                }
                else
                {
                    currentLine = serializeDescription(currentLine, field.getDescription(), lineFormatter, indent,
                        indent.length() + alignmentSpaces, builder);
                }
            }
            return currentLine;
        }

        @Override
        protected int serialize(int currentLine, TypeSection typeSection, String lineFormatter, String indent,
            int alignmentSpaces, int starNumber, StringBuilder builder)
        {
            int lineNumber = isIgnoreLineNumbers() ? currentLine : typeSection.getLineNumber();
            addEmptyLines(currentLine, lineNumber, lineFormatter, alignmentSpaces, builder);
            currentLine = lineNumber;
            if (!typeSection.getTypeDefinitions().isEmpty())
            {
                //count number of symbols for previous line
                int numberCharsForPreviousLine = 0;
                for (int i = builder.length() - 1; i >= 0; --i)
                {
                    if (builder.charAt(i) == '\n')
                    {
                        numberCharsForPreviousLine = builder.length() - i - 1;
                        numberCharsForPreviousLine -= IBslCommentToken.LINE_STARTER.length() + 1;
                        break;
                    }
                }
                List<FieldDefinition> fieldExtension = null;
                Set<String> addTypeNames = Sets.newHashSet();
                for (int i = 0; i < typeSection.getTypeDefinitions().size(); ++i)
                {
                    TypeDefinition definition = typeSection.getTypeDefinitions().get(i);
                    String typeName = definition.getTypeName();
                    if (typeName != null && !typeName.isEmpty() && addTypeNames.add(typeName))
                    {
                        if (i != 0)
                        {
                            builder.append(IBslCommentToken.TYPE_SECTION_TYPE_DELIMETER).append(' ');
                        }
                        builder.append(typeName);
                    }
                    if (i == typeSection.getTypeDefinitions().size() - 1)
                    {
                        if (!definition.getContainTypes().isEmpty())
                        {
                            builder.append(' ')
                                .append(isRussian() ? IBslCommentToken.CONTAINS_RU : IBslCommentToken.CONTAINS)
                                .append(' ');
                            Set<String> addContainsTypeNames = Sets.newHashSet();
                            for (int j = 0; j < definition.getContainTypes().size(); ++j)
                            {
                                TypeDefinition containsDefinition = definition.getContainTypes().get(j);
                                typeName = containsDefinition.getTypeName();
                                if (typeName != null && !typeName.isEmpty() && addContainsTypeNames.add(typeName))
                                {
                                    if (j != 0)
                                    {
                                        builder.append(IBslCommentToken.TYPE_SECTION_TYPE_DELIMETER).append(' ');
                                    }
                                    builder.append(typeName);
                                }
                                if (j == definition.getContainTypes().size() - 1)
                                {
                                    fieldExtension = containsDefinition.getFieldDefinitionExtension();
                                }
                            }
                        }
                        else
                        {
                            if (fieldExtension == null || fieldExtension.isEmpty())
                            {
                                fieldExtension = definition.getFieldDefinitionExtension();
                            }
                        }
                    }
                }
                if (fieldExtension != null && !fieldExtension.isEmpty())
                {
                    List<IDescriptionPart> parts = typeSection.getDescription().getParts();
                    if (!parts.isEmpty())
                    {
                        builder.append(' ')
                            .append(IBslCommentToken.TYPE_SECTION_DASH)
                            .append(ONE_SPACE.repeat(settings.getSpaceBeforeDescription()));
                        boolean firstPart = true;
                        for (IDescriptionPart part : parts)
                        {
                            if (part instanceof TextPart)
                            {
                                String partText = ((TextPart)part).getText();
                                builder.append(firstPart ? partText.stripLeading() : partText);
                            }
                            else if (part instanceof LinkPart)
                            {
                                currentLine = serialize(currentLine, ((LinkPart)part), lineFormatter, indent, builder);
                            }
                            firstPart = false;
                        }
                    }
                    builder.append(IBslCommentToken.TYPE_SECTION_EXTENSION_COLON);
                    currentLine = serializeFieldDefinitions(currentLine, fieldExtension, lineFormatter, indent,
                        numberCharsForPreviousLine, starNumber, builder);
                }
                else
                {
                    builder.append(' ')
                        .append(IBslCommentToken.TYPE_SECTION_DASH)
                        .append(ONE_SPACE.repeat(settings.getSpaceBeforeDescription()));
                    currentLine = serializeDescription(currentLine, typeSection.getDescription(), lineFormatter, indent,
                        alignmentSpaces, builder);
                }
            }
            return currentLine;
        }

        @Override
        protected int serialize(int currentLine, ReturnSection section, String lineFormatter, String indent,
            StringBuilder builder)
        {
            int lineNumber = isIgnoreLineNumbers() ? currentLine + settings.getSectionSpace() : section.getLineNumber();
            addEmptyLines(currentLine, lineNumber, lineFormatter, 0, builder);
            currentLine = lineNumber;
            builder.append(isRussian() ? IBslCommentToken.RETURNS_RU : IBslCommentToken.RETURNS);
            if (!section.getReturnTypes().isEmpty())
            {
                //write only description on the same line that header keyword
                List<IDescriptionPart> parts = section.getDescription().getParts();
                List<IDescriptionPart> partsOnStartLine = new ArrayList<>(parts.size());
                List<IDescriptionPart> partsOnAnotherLine = new ArrayList<>(parts.size());
                for (int i = 0; i < parts.size(); ++i)
                {
                    IDescriptionPart part = parts.get(i);
                    if (!isIgnoreLineNumbers() && part.getLineNumber() == section.getLineNumber())
                    {
                        partsOnStartLine.add(part);
                    }
                    else
                    {
                        partsOnAnotherLine.add(part);
                    }
                }
                if (!isIgnoreLineNumbers() || !partsOnStartLine.isEmpty())
                {
                    builder.append(' ');
                }
                currentLine = serializeDescription(currentLine, partsOnStartLine, lineFormatter, indent, 0, builder);
                //only one type section allowed in old format
                TypeSection typeSection = section.getReturnTypes().get(0);
                if (isIgnoreLineNumbers())
                {
                    addEmptyLines(currentLine, currentLine + 1, lineFormatter, 1, builder); //add new line for type section from return section
                    currentLine++;
                }
                currentLine = serialize(currentLine, typeSection, lineFormatter, indent, 0, 1, builder);
                if (!partsOnAnotherLine.isEmpty())
                {
                    addEmptyLines(currentLine, currentLine + 1, lineFormatter, 0, builder); //add new line for description from return section
                    currentLine =
                        serializeDescription(currentLine, partsOnAnotherLine, lineFormatter, indent, 0, builder);
                }
            }
            else
            {
                if (!isIgnoreLineNumbers() || !section.getDescription().getParts().isEmpty())
                {
                    builder.append(' ');
                }
                currentLine =
                    serializeDescription(currentLine, section.getDescription(), lineFormatter, indent, 0, builder);
            }
            return currentLine;
        }
    }

    public static class NewFormatDocCommentSerializer
        extends BslDocCommentSerializer
    {

        private NewFormatDocCommentSerializer(FormatterSettings settings, boolean isRussian)
        {
            super(settings, isRussian);
        }

        @Override
        public boolean isOldFormat()
        {
            return false;
        }

        @Override
        protected int serializeFieldDefinitions(int currentLine, List<FieldDefinition> fieldExtension,
            String lineFormatter, String indent, int alignmentSpaces, int starNumber, StringBuilder builder)
        {
            for (int i = 0; i < fieldExtension.size(); ++i)
            {
                FieldDefinition field = fieldExtension.get(i);
                if (field.getName() == null || field.getName().isEmpty())
                {
                    continue;
                }
                builder.append(getLineSeparator())
                    .append(lineFormatter)
                    .append(IBslCommentToken.LINE_STARTER)
                    .append(ONE_SPACE.repeat(settings.getLineStartSpace()));
                ++currentLine;
                //add space for alignment start and end of type section
                if (settings.isAlignFields())
                {
                    builder.append(ONE_SPACE.repeat(Math.max(0, alignmentSpaces)));
                }
                if (alignmentSpaces >= 0 && settings.isAlignFields())
                {
                    builder.append(indent);
                }
                builder.append(IBslCommentToken.STAR).append(' ');
                builder.append(field.getName()).append(' ');
                currentLine = serializeDescription(currentLine, field.getDescription(), lineFormatter, indent,
                    2 * indent.length() + alignmentSpaces, builder);
                for (TypeSection typeSection : field.getTypeSections())
                {
                    currentLine = serialize(currentLine, typeSection, lineFormatter, indent,
                        2 * indent.length() + alignmentSpaces, starNumber + 1, builder);
                }
            }
            return currentLine;
        }

        @Override
        protected int serialize(int currentLine, TypeSection typeSection, String lineFormatter, String indent,
            int alignmentSpaces, int starNumber, StringBuilder builder)
        {
            int lineNumber = isIgnoreLineNumbers() ? currentLine : typeSection.getLineNumber();
            addEmptyLines(currentLine, lineNumber, lineFormatter, alignmentSpaces, builder);
            currentLine = lineNumber;
            if (!typeSection.getTypeDefinitions().isEmpty())
            {
                //count number of symbols for previous line
                int numberCharsForPreviousLine = 0;
                for (int i = builder.length() - 1; i >= 0; --i)
                {
                    if (builder.charAt(i) == '\n')
                    {
                        numberCharsForPreviousLine = builder.length() - i - 1;
                        numberCharsForPreviousLine -= IBslCommentToken.LINE_STARTER.length() + 1;
                        break;
                    }
                }
                List<FieldDefinition> fieldExtension = null;
                builder.append(IBslCommentToken.TYPE_SECTION_LEFT_BRACKET);
                Set<String> addTypeNames = Sets.newHashSet();
                for (int i = 0; i < typeSection.getTypeDefinitions().size(); ++i)
                {
                    TypeDefinition definition = typeSection.getTypeDefinitions().get(i);
                    String typeName = definition.getTypeName();
                    if (typeName != null && !typeName.isEmpty() && addTypeNames.add(typeName))
                    {
                        if (i != 0)
                        {
                            builder.append(IBslCommentToken.TYPE_SECTION_TYPE_DELIMETER).append(' ');
                        }
                        builder.append(typeName);
                    }
                    if (i == typeSection.getTypeDefinitions().size() - 1)
                    {
                        if (!definition.getContainTypes().isEmpty())
                        {
                            builder.append(' ')
                                .append(isRussian() ? IBslCommentToken.CONTAINS_RU : IBslCommentToken.CONTAINS)
                                .append(' ');
                            Set<String> addContainsTypeNames = Sets.newHashSet();
                            for (int j = 0; j < definition.getContainTypes().size(); ++j)
                            {
                                TypeDefinition containsDefinition = definition.getContainTypes().get(j);
                                typeName = containsDefinition.getTypeName();
                                if (typeName != null && !typeName.isEmpty() && addContainsTypeNames.add(typeName))
                                {
                                    if (j != 0)
                                    {
                                        builder.append(IBslCommentToken.TYPE_SECTION_TYPE_DELIMETER).append(' ');
                                    }
                                    builder.append(typeName);
                                }
                                if (j == definition.getContainTypes().size() - 1)
                                {
                                    fieldExtension = containsDefinition.getFieldDefinitionExtension();
                                }
                            }
                        }
                        else
                        {
                            if (fieldExtension == null || fieldExtension.isEmpty())
                            {
                                fieldExtension = definition.getFieldDefinitionExtension();
                            }
                        }
                    }
                }
                if (fieldExtension != null && !fieldExtension.isEmpty())
                {
                    builder.append(IBslCommentToken.TYPE_SECTION_EXTENSION_COLON);
                    currentLine = serializeFieldDefinitions(currentLine, fieldExtension, lineFormatter, indent,
                        numberCharsForPreviousLine, starNumber, builder);
                    //add new line for the end of type section
                    builder.append(getLineSeparator())
                        .append(lineFormatter)
                        .append(IBslCommentToken.LINE_STARTER)
                        .append(ONE_SPACE.repeat(settings.getLineStartSpace()));
                    ++currentLine;
                    //add space for alignment start and end of type section
                    builder.append(ONE_SPACE.repeat(numberCharsForPreviousLine));
                    builder.append(IBslCommentToken.TYPE_SECTION_RIGHT_BRACKET).append(' ');
                }
                else
                {
                    builder.append(IBslCommentToken.TYPE_SECTION_RIGHT_BRACKET).append(' ');
                }
            }
            currentLine = serializeDescription(currentLine, typeSection.getDescription(), lineFormatter, indent,
                alignmentSpaces, builder);
            return currentLine;
        }

        @Override
        protected int serialize(int currentLine, ReturnSection section, String lineFormatter, String indent,
            StringBuilder builder)
        {
            int lineNumber = isIgnoreLineNumbers() ? currentLine + settings.getSectionSpace() : section.getLineNumber();
            addEmptyLines(currentLine, lineNumber, lineFormatter, 0, builder);
            currentLine = lineNumber;
            builder.append(isRussian() ? IBslCommentToken.RETURNS_RU : IBslCommentToken.RETURNS);
            if (!isIgnoreLineNumbers() || !section.getDescription().getParts().isEmpty())
            {
                builder.append(ONE_SPACE.repeat(settings.getSpaceBeforeDescription()));
            }
            else if (isIgnoreLineNumbers() && section.getDescription().getParts().isEmpty())
            {
                lineNumber++;
                addEmptyLines(currentLine, lineNumber, lineFormatter, 0, builder);
                currentLine = lineNumber;
            }
            currentLine =
                serializeDescription(currentLine, section.getDescription(), lineFormatter, indent, 0, builder);
            for (TypeSection typeSection : section.getReturnTypes())
            {
                currentLine = serialize(currentLine, typeSection, lineFormatter, indent, 0, 1, builder);
            }
            return currentLine;
        }

    }

    public static class FormatterSettings
    {
        private String indent = DEFAULT_INDENT;

        private int lineStartSpace = 1;

        private int sectionSpace = DEFAULT_SECTION_SPACE;

        private int afterSpace = 0;

        private int depricatedSpace;

        private boolean alignFields;

        private boolean alignTypes;

        private boolean typeFromNewLine;

        private boolean linkInBrackets;

        private int parameterSpace = 1;

        private int fieldSpace = 1;

        private int spaceBeforeDescription = 1;

        public String getIndent()
        {
            return indent;
        }

        public int getLineStartSpace()
        {
            return lineStartSpace;
        }

        public int getSpaceBeforeDescription()
        {
            return spaceBeforeDescription;
        }

        public int getDedepricatedSpace()
        {
            return depricatedSpace;
        }

        /**
         * Gets the line numbers of space between sections. Default is 2.
         *
         * @return the section space
         */
        public int getSectionSpace()
        {
            return sectionSpace;
        }

        /**
         * Gets the line numbers of space after all sections. Default is 0.
         *
         * @return the section space
         */
        public int getAfterSpace()
        {
            return afterSpace;
        }

        /**
        * Checks if need to align fields of type section to type name position.
        *
        * @return true, if need to align fields, otherwise start field from begin of line
        */
        public boolean isAlignFields()
        {
            return alignFields;
        }

        /**
         * Checks if it needs to serialize link in brackets.
         *
         * @return true, if it needs to serialize link in brackets.
         */
        public boolean isLinkInBrackets()
        {
            return linkInBrackets;
        }

    }

    /**
     * The configuration builder of {@link BslDocCommentSerializer serializer}
     */
    public static final class Builder
    {
        private boolean isRussian;

        private boolean oldFormat;

        private String lineSeparator = System.lineSeparator();

        private int lineStartSpace = 1;

        private int sectionSpace = DEFAULT_SECTION_SPACE;

        private int afterSpace = 0;

        private boolean ignoreLineNumbers;

        private int depricatedSpace;

        private boolean alignFields;

        private boolean alignTypes;

        private boolean typeFromNewLine;

        private boolean linkInBrackets;

        private int parameterSpace = 1;

        private int fieldSpace = 1;

        private int spaceBeforeDescription = 1;

        /**
         * Builds new {@link BslDocCommentSerializer serializer} with build configuration.
         *
         * @return the bsl doc comment serializer
         */
        public BslDocCommentSerializer build()
        {
            FormatterSettings settings = new FormatterSettings();
            settings.lineStartSpace = lineStartSpace;
            settings.spaceBeforeDescription = spaceBeforeDescription;
            settings.depricatedSpace = depricatedSpace;
            settings.parameterSpace = parameterSpace;
            settings.fieldSpace = fieldSpace;
            settings.alignTypes = alignTypes;
            settings.typeFromNewLine = typeFromNewLine;
            settings.sectionSpace = sectionSpace;
            settings.afterSpace = afterSpace;
            settings.alignFields = alignFields;
            settings.linkInBrackets = linkInBrackets;

            BslDocCommentSerializer serializer;
            if (oldFormat)
            {
                serializer = new CurrentFormatDocCommentSerializer(settings, isRussian);
            }
            else
            {
                serializer = new NewFormatDocCommentSerializer(settings, isRussian);
            }

            serializer.ignoreLineNumbers = ignoreLineNumbers;
            serializer.lineSeparator = lineSeparator;

            return serializer;
        }

        /**
         * Sets the line numbers of space between sections. Default is 2.
         *
         * @param sectionSpace the section space, must be more then 0.
         * @return the builder
         */
        public Builder sectionSpace(int sectionSpace)
        {
            Assert.isLegal(sectionSpace > 0);
            this.sectionSpace = sectionSpace;
            return this;
        }

        /**
         * Sets the line numbers of space after all sections. Default is 0.
         *
         * @param afterSpace the after space, must be 0 or more.
         * @return the builder
         */
        public Builder afterSpace(int afterSpace)
        {
            Assert.isLegal(afterSpace >= 0);
            this.afterSpace = afterSpace;
            return this;
        }

        /**
         * Set Ignore line numbers of the model and make defaul line fomatting.
         *
         * @return the builder
         */
        public Builder ignoreLineNumbers()
        {
            this.ignoreLineNumbers = true;
            return this;
        }

        /**
         * Sets the string presentation must be in old format
         *
         * @return the builder
         */
        public Builder oldFormat()
        {
            this.oldFormat = true;
            return this;
        }

        /**
         * Sets the string presentation must be in new format.
         *
         * @return the builder
         */
        public Builder newFormat()
        {
            this.oldFormat = false;
            this.alignFields = true;
            return this;
        }

        /**
         * Align fields of type definition field extention to the parent element indentaion.
         *
         * @return the builder
         */
        public Builder alignFields()
        {
            this.alignFields = true;
            return this;
        }

        /**
         * Sets that it need to align fieldsof type section to type name position.
         *
         * @param alignFields true, if need to align fields, otherwise start field from begin of line
         * @return the builder
         */
        public Builder alignFields(boolean alignFields)
        {
            this.alignFields = alignFields;
            return this;
        }

        /**
         * Sets the link need to serialize in brackets.
         *
         * @return the builder
         */
        public Builder linkInBrackets()
        {
            this.linkInBrackets = true;
            return this;
        }

        /**
         * Sets the line separator.
         *
         * @param lineSeparator the new line separator, cannot be {@code null}.
         * @return the builder
         */
        public Builder lineSeparator(String lineSeparator)
        {
            this.lineSeparator = lineSeparator;
            return this;
        }

        /**
         * Sets the script variant of documentation comment model presentation.
         *
         * @param isRussian {@code true} if it need Russian, otherwise is English
         * @return the builder
         */
        public Builder setScriptVariant(boolean isRussian)
        {
            this.isRussian = isRussian;
            return this;
        }

        /**
         * Sets the script variant of documentation comment model presentation.
         *
         * @param variant the script variant, cannot be {@code null}.
         * @return the builder
         */
        public Builder setScriptVariant(ScriptVariant variant)
        {
            return setScriptVariant(ScriptVariant.RUSSIAN.equals(variant));
        }

        /**
         * Sets the string presentation must be in old format or in new format.
         *
         * @param oldFormat <code>true</code> if string presentation must be in old format, <code>false</code> in new format
         */
        public Builder setOldFormat(boolean oldFormat)
        {
            return oldFormat ? oldFormat() : newFormat();
        }
    }

}
