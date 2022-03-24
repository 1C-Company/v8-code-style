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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.IBslCommentToken;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.common.StringUtils;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.common.io.CharStreams;

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

    /** The preference root qualifier. */
    public static final String PREF_QUALIFIER = BslPlugin.PLUGIN_ID;

    /** The key for preferences store the state of the creating module with {@code //@strict-types} annotation */
    public static final String PREF_KEY_CREATE_STRICT_TYPES = "addModuleStrictTypesAnnotation"; //$NON-NLS-1$

    /** The default value of creating module with strict types. */
    public static final boolean PREF_DEFAULT_CREATE_STRICT_TYPES = true;

    private static final int COMMENT_LENGTH = IBslCommentToken.LINE_STARTER.length();

    /**
     * Can add module strict-types annotation for project.
     *
     * @param project the project, cannot be {@code null}.
     * @return true, if can create strict-types module for the project
     */
    public static boolean canAddModuleStrictTypesAnnotation(IProject project)
    {
        ProjectScope scope = new ProjectScope(project);
        IScopeContext[] contexts =
            new IScopeContext[] { scope, InstanceScope.INSTANCE, ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(PREF_QUALIFIER, PREF_KEY_CREATE_STRICT_TYPES, PREF_DEFAULT_CREATE_STRICT_TYPES, contexts);
    }

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
        return getStrictTypeAnnotationNode(root) != null;
    }

    /**
     *
     * Gets the {@link ILeafNode} of the  {@code @strict-types} annotation in module description.
     *
     * @param root the root, cannot be {@code null}.
     * @return leaf node, if module has annotation in header, or {@code null} if not found
     */
    public static ILeafNode getStrictTypeAnnotationNode(INode root)
    {
        for (ILeafNode node : root.getLeafNodes())
        {
            if (!node.isHidden())
            {
                return null;
            }

            if (BslCommentUtils.isCommentNode(node)
                && node.getText().substring(COMMENT_LENGTH).trim().startsWith(STRICT_TYPE_ANNOTATION))
            {
                return node;
            }

        }
        return null;
    }

    /**
     * Sets the {@code @strict-types} annotation in the file. If file is not exits this method will create empty file
     * with annotation.
     *
     * @param bslFile the BSL file, cannot be {@code null}.
     * @param monitor the monitor
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CoreException the core exception
     */
    public static void setStrictTypeAnnotation(IFile bslFile, IProgressMonitor monitor)
        throws IOException, CoreException
    {
        String currentCode = StringUtils.EMPTY;
        if (bslFile.exists())
        {
            try (InputStream in = bslFile.getContents();
                Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);)
            {
                currentCode = CharStreams.toString(reader);
            }
        }

        String preferedLineSeparator = PreferenceUtils.getLineSeparator(bslFile.getProject());
        StringBuilder sb = new StringBuilder();

        int insertOffset = getInsertOffset(currentCode, preferedLineSeparator);
        if (insertOffset > 0)
        {
            sb.append(currentCode.substring(0, insertOffset));
            sb.append(preferedLineSeparator);
        }

        sb.append(IBslCommentToken.LINE_STARTER);
        sb.append(" "); //$NON-NLS-1$
        sb.append(StrictTypeUtil.STRICT_TYPE_ANNOTATION);
        sb.append(preferedLineSeparator);
        sb.append(preferedLineSeparator);
        sb.append(currentCode.substring(insertOffset));

        if (monitor.isCanceled())
        {
            return;
        }

        try (InputStream in = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));)
        {
            if (bslFile.exists())
            {
                bslFile.setContents(in, true, true, monitor);
            }
            else
            {
                bslFile.create(in, true, monitor);
            }
        }
    }

    private static int getInsertOffset(String currentCode, String preferedLineSeparator)
    {
        int separator = preferedLineSeparator.length();

        int offset = 0;
        for (Iterator<String> iterator = currentCode.lines().iterator(); iterator.hasNext();)
        {
            if (offset > 0)
            {
                offset = offset + separator;
            }

            String line = iterator.next();
            if (StringUtils.isBlank(line))
            {
                return offset;
            }
            else if (!line.stripLeading().startsWith(IBslCommentToken.LINE_STARTER))
            {
                return 0;
            }

            offset = offset + line.length();

        }
        return 0;
    }

    private StrictTypeUtil()
    {
        throw new IllegalAccessError();
    }


}
