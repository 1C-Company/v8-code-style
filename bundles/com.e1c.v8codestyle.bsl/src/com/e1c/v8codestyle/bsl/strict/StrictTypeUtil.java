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
package com.e1c.v8codestyle.bsl.strict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.IBslCommentToken;
import com._1c.g5.v8.dt.bsl.model.Module;

/**
 * The utility class for strict-types system.
 *
 * @author Dmitriy Marmyshev
 */
public final class StrictTypeUtil
{

    /** The strict-types annotation using in module header to activate checks */
    public static final String STRICT_TYPE_ANNOTATION = "@strict-types"; //$NON-NLS-1$

    public static final String BSL_FILE_EXTENSION = "bsl"; //$NON-NLS-1$

    private static final int COMMENT_LENGTH = IBslCommentToken.LINE_STARTER.length();

    /**
     * Checks for {@code @strict-types} annotation in BSL module file.
     *
     * @param file the file of BSL module, cannot be {@code null}.
     * @return true, if the module has {@code @strict-types} annotation in module header
     * @throws CoreException the core exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean hasStrictTypeAnnotation(IFile file) throws CoreException, IOException
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents(), file.getCharset())))
        {

            String line = reader.readLine();
            while (line != null)
            {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith(IBslCommentToken.LINE_STARTER))
                {
                    return false;
                }
                if (line.length() > 0 && line.substring(COMMENT_LENGTH).trim().startsWith(STRICT_TYPE_ANNOTATION))
                {
                    return true;
                }

                line = reader.readLine();
            }
        }

        return false;
    }

    /**
     * Checks for {@code @strict-types} annotation in module description.
     *
     * @param object the object of BSL module, cannot be {@code null}.
     * @return true, if module has annotation in header
     */
    public static boolean hasStrictTypeAnnotation(EObject object)
    {
        Module module = EcoreUtil2.getContainerOfType(object, Module.class);
        if (module == null)
        {
            return false;
        }

        return hasStrictTypeAnnotation(module);
    }

    /**
     * Checks for {@code @strict-types} annotation in module description.
     *
     * @param module the module, cannot be {@code null}.
     * @return true, if module has annotation in header
     */
    public static boolean hasStrictTypeAnnotation(Module module)
    {
        ICompositeNode node = NodeModelUtils.getNode(module);
        node = node.getRootNode();

        return hasStrictTypeAnnotation(node);
    }

    /**
     * Checks for {@code @strict-types} annotation in module description.
     *
     * @param root the root, cannot be {@code null}.
     * @return true, if module has annotation in header
     */
    public static boolean hasStrictTypeAnnotation(INode root)
    {
        for (ILeafNode node : root.getLeafNodes())
        {
            if (!node.isHidden())
            {
                return false;
            }

            if (BslCommentUtils.isCommentNode(node)
                && node.getText().substring(COMMENT_LENGTH).trim().startsWith(STRICT_TYPE_ANNOTATION))
            {
                return true;
            }

        }
        return false;
    }

    private StrictTypeUtil()
    {
        throw new IllegalAccessError();
    }

}
