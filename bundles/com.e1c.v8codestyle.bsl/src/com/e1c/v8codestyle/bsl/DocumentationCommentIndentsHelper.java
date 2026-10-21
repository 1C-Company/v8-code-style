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
package com.e1c.v8codestyle.bsl;

import java.util.ArrayList;
import java.util.List;

/**
 * The documentation comment indents helper.
 *
 * @author Artem Samohvalov
 */
public final class DocumentationCommentIndentsHelper
{
    public static final String COMMENT_PREFIX = "//"; //$NON-NLS-1$
    public static final int COMMENT_PREFIX_LENGTH = COMMENT_PREFIX.length();

    private static final int TABULATION_SPACE_COUNT = 4;
    private static final String TABULATION_SPACE = " ".repeat(TABULATION_SPACE_COUNT); //$NON-NLS-1$

    /**
     * Checks if is mixed tabs and spaces.
     *
     * @param line the line, cannot be {@code null}
     * @return true, if is mixed tabs and spaces
     */
    public static boolean isMixedTabsAndSpaces(String line)
    {
        if (line.length() <= COMMENT_PREFIX_LENGTH)
        {
            return false;
        }

        char first = line.charAt(COMMENT_PREFIX_LENGTH);

        if (first != ' ' && first != '\t')
        {
            return false;
        }

        for (int i = COMMENT_PREFIX_LENGTH + 1; i < line.length(); ++i)
        {
            char current = line.charAt(i);

            if (current != ' ' && current != '\t')
            {
                break;
            }

            if (current != first)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the indent length.
     *
     * @param line the line, cannot be {@code null}
     * @return the indent length
     */
    public static int getIndentLength(String line)
    {
        int i = COMMENT_PREFIX_LENGTH;

        while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t'))
        {
            ++i;
        }

        return i - COMMENT_PREFIX_LENGTH;
    }

    /**
     * Gets the normalized indent.
     *
     * @param line the line, cannot be {@code null}
     * @return the normalized indent, never be {@code null}
     */
    public static String getNormalizedIndent(String line)
    {
        int length = getIndentLength(line);
        StringBuilder indent = new StringBuilder();

        for (int i = COMMENT_PREFIX_LENGTH; i < COMMENT_PREFIX_LENGTH + length; ++i)
        {
            // it's recommended to use only spaces
            indent.append(line.charAt(i) == '\t' ? TABULATION_SPACE : ' ');
        }

        return indent.toString();
    }

    /**
     * Find mixed lines.
     *
     * @param lines the lines
     * @param onlyFirst find only first mixed line
     * @return the list of line numbers 
     */
    public static List<Integer> findMixedLines(List<String> lines, boolean onlyFirst)
    {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < lines.size(); ++i)
        {
            if (isMixedTabsAndSpaces(lines.get(i)))
            {
                result.add(i);

                if (onlyFirst)
                    break;
            }
        }

        return result;
    }

    /**
     * Gets the indent width.
     *
     * @param line the line
     * @return the indent width
     */
    public static int getIndentWidth(String line)
    {
        if (line.length() < COMMENT_PREFIX_LENGTH)
            return 0;

        int indent = 0;

        for (int i = COMMENT_PREFIX_LENGTH; i < line.length(); ++i)
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

    private DocumentationCommentIndentsHelper()
    {
    }
}
