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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Method;

/**
 * 
 * 
 * @author Artem Samohvalov
 */
public class DocumentationCommentLines
{
    private static final String COMMENT_PREFIX = "//"; //$NON-NLS-1$

    /**
     * The method gets doc comment and return it
     * indexes in return list is line numbers of a method
     * 
     * @param method
     * @return all doc comment lines without "//"
     */
    public static List<String> get(Method method)
    {
        INode node = NodeModelUtils.findActualNodeFor(method);
        if (node == null)
        {
            return List.of();
        }

        return get(node.getText());
    }

    /**
     * The method gets doc string comment and return it
     * indexes in return list is line numbers of a method
     * 
     * @param method
     * @return all doc comment lines without "//"
     */
    public static List<String> get(String text)
    {
        List<String> result = new ArrayList<>();

        for (String line : text.split("\\r?\\n")) //$NON-NLS-1$
        {
            if (!line.startsWith(COMMENT_PREFIX))
            {
                continue;
            }

            result.add(line.substring(COMMENT_PREFIX.length()));
        }

        return result;
    }
}
