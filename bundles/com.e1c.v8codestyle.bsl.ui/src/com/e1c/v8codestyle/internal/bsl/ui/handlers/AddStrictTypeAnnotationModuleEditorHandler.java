/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.internal.bsl.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.handly.buffer.BufferChange;
import org.eclipse.handly.snapshot.NonExpiringSnapshot;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;

import com._1c.g5.ides.ui.texteditor.xtext.embedded.EmbeddedEditorBuffer;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.IBslCommentToken;
import com._1c.g5.v8.dt.bsl.ui.menu.BslHandlerUtil;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.core.model.EditingMode;
import com._1c.g5.v8.dt.core.model.IModelEditingSupport;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;
import com.google.inject.Inject;

/**
 * The Handler to update current module opened in BSL editor if {@code @strict-types} annotation is not added yet.
 *
 * @author Dmitriy Marmyshev
 */
public class AddStrictTypeAnnotationModuleEditorHandler
    extends AbstractHandler
{

    @Inject
    private IResourceLookup resourceLookup;

    @Inject
    private IModelEditingSupport modelEditingSupport;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);

        XtextEditor target = BslHandlerUtil.extractXtextEditor(part);
        if (target != null)
        {
            IXtextDocument document = target.getDocument();

            Pair<IProject, Integer> pair =
                document.readOnly(new CancelableUnitOfWork<Pair<IProject, Integer>, XtextResource>()
                {
                    @Override
                    public Pair<IProject, Integer> exec(XtextResource resource, CancelIndicator monitor)
                        throws Exception
                    {
                        return getInsertContextIfPossible(resource,
                            monitor == null ? CancelIndicator.NullImpl : monitor);
                    }

                });
            if (pair != null && pair.getFirst() != null && pair.getSecond() > -1)
            {
                addAnnotation(document, pair.getFirst(), pair.getSecond());
            }

        }
        return null;
    }

    private Pair<IProject, Integer> getInsertContextIfPossible(XtextResource resource, CancelIndicator monitor)
    {
        if (monitor.isCanceled() || resource.getParseResult() == null)
        {
            return null;
        }

        IParseResult parseResult = resource.getParseResult();
        EObject root = parseResult.getRootASTElement();
        if (!modelEditingSupport.canEdit(root, EditingMode.DIRECT))
        {
            return null;
        }

        ICompositeNode node = parseResult.getRootNode();
        if (StrictTypeUtil.hasStrictTypeAnnotation(node))
        {
            return null;
        }

        int offset = getInsertOffset(node);
        IProject project = resourceLookup.getProject(root);
        if (monitor.isCanceled())
        {
            return null;
        }

        return Tuples.create(project, offset);

    }

    private void addAnnotation(IXtextDocument document, IProject project, int insertOffset)
    {
        String preferedLineSeparator = PreferenceUtils.getLineSeparator(project);
        StringBuilder sb = new StringBuilder();

        if (insertOffset > 0)
        {
            sb.append(preferedLineSeparator);
        }

        sb.append(IBslCommentToken.LINE_STARTER);
        sb.append(" "); //$NON-NLS-1$
        sb.append(StrictTypeUtil.STRICT_TYPE_ANNOTATION);
        sb.append(preferedLineSeparator);
        sb.append(preferedLineSeparator);

        TextEdit change = new InsertEdit(insertOffset, sb.toString());

        try (EmbeddedEditorBuffer buffer = new EmbeddedEditorBuffer(document))
        {
            NonExpiringSnapshot snapshot = new NonExpiringSnapshot(buffer);
            BufferChange bufferChange = new BufferChange(change);
            bufferChange.setBase(snapshot);
            buffer.applyChange(bufferChange, new NullProgressMonitor());
        }
        catch (CoreException e)
        {
            UiPlugin.logError(e);
        }
    }

    private static int getInsertOffset(INode root)
    {

        for (ILeafNode node : root.getLeafNodes())
        {
            if (!node.isHidden())
            {
                return 0;
            }

            if (!BslCommentUtils.isCommentNode(node))
            {
                return node.getOffset();
            }

        }
        return 0;
    }

}
