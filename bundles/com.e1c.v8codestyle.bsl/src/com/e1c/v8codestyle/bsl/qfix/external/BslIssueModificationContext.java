/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.qfix.external;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IssueModificationContext;
import org.eclipse.xtext.validation.Issue;

import com._1c.g5.v8.dt.lcore.ui.editor.ViewerAwareIssueModificationContext;
import com.google.common.base.Preconditions;

/**
 * Extension of {@link IssueModificationContext} to be used by quick fixes and suppressions
 *
 * @author Vadim Geraskin
 */
public class BslIssueModificationContext
    extends ViewerAwareIssueModificationContext
{
    private final AtomicReference<ITextViewer> viewerRef = new AtomicReference<>();
    private final Issue issue;
    private final IURIEditorOpener editorOpener;

    private IXtextDocument document;

    /**
     * Instantiates modification context
     *
     * @param issue the issue, cannot be {@code null}
     * @param editorOpener the editor opener, cannot be {@code null}
     */
    public BslIssueModificationContext(Issue issue, IURIEditorOpener editorOpener)
    {
        this.issue = Preconditions.checkNotNull(issue);
        this.editorOpener = Preconditions.checkNotNull(editorOpener);
    }

    @Override
    public IXtextDocument getXtextDocument()
    {
        return getXtextDocument(issue.getUriToProblem());
    }

    @Override
    public IXtextDocument getXtextDocument(URI uri)
    {
        if (document != null)
        {
            return document;
        }

        document = getXtextDocument(uri, editorOpener, editor -> viewerRef.set(extractLocalTextViewer(editor)));
        setTextViewer(viewerRef.get());

        return document;
    }

    /*
     * Extract text viewer from editor. Can return {@code null}
     */
    private static ITextViewer extractLocalTextViewer(IEditorPart editor)
    {
        ITextOperationTarget target = editor.getAdapter(ITextOperationTarget.class);
        if (target instanceof ITextViewer)
        {
            return (ITextViewer)target;
        }
        return null;
    }

    private static IXtextDocument getXtextDocument(URI uri, IURIEditorOpener editorOpener,
        Consumer<IEditorPart> editorConsumer)
    {
        for (int trial = 0; trial < 2; trial++)
        {
            IEditorPart editor = editorOpener.open(uri, false);
            if (editor == null)
            {
                return null;
            }
            if (editorConsumer != null)
            {
                editorConsumer.accept(editor);
            }
            if (editor instanceof XtextEditor)
            {
                XtextEditor xtextEditor = (XtextEditor)editor;
                return xtextEditor.getDocument();
            }
        }
        return null;
    }
}
