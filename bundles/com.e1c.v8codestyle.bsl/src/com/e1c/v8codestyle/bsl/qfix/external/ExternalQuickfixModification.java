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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.handly.buffer.BufferChange;
import org.eclipse.handly.snapshot.NonExpiringSnapshot;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModification;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.Issue;

import com._1c.g5.ides.ui.texteditor.xtext.embedded.EmbeddedEditorBuffer;
import com.google.common.base.Function;

/**
 * Implementation of {@link IModification} for creating and applying quickfix text changes.
 *
 * @author Dzyuba_M - initial implementation
 * @author Vadim Geraskin - move to a separate class
 * @param <E> type of model object is contained in {@link Issue}
 */
public class ExternalQuickfixModification<E extends EObject>
    implements IModification
{
    private final Issue issue;
    private final Function<E, TextEdit> function;
    private final Class<E> clazz;

    /**
     * Constructor
     * @param issue processing validation {@link Issue}, can't be <code>null</code>
     * @param clazz {@link Class} of the Built-in object corresponding to validation {@link Issue}, can't be <code>null</code>
     * @param function special {@link Function} that contains logic about creating {@link TextEdit} changes corresponding to quickfix, can't be <code>null</code>
     */
    public ExternalQuickfixModification(Issue issue, Class<E> clazz, Function<E, TextEdit> function)
    {
        this.issue = issue;
        this.function = function;
        this.clazz = clazz;
    }

    @Override
    public void apply(IModificationContext context) throws Exception
    {
        IXtextDocument document = getActualDocument(context);
        TextEdit change = createChanges(document, issue, clazz, function);
        if (change != null)
        {
            EmbeddedEditorBuffer buffer = new EmbeddedEditorBuffer(document);
            try
            {
                NonExpiringSnapshot snapshot = new NonExpiringSnapshot(buffer);
                BufferChange bufferChange = new BufferChange(change);
                bufferChange.setBase(snapshot);
                buffer.applyChange(bufferChange, null);
            }
            finally
            {
                buffer.release();
            }
        }
    }

    /**
     * Creates quickfix changes
     * @param document actual {@link IXtextDocument}, creating text changes will be applied to it, can't be <code>null</code>
     * @param issue processing validation {@link Issue}, can't be <code>null</code>
     * @param clazz {@link Class} of the Built-in object corresponding to validation {@link Issue}, can't be <code>null</code>
     * @param function special {@link Function} that contains logic about creating {@link TextEdit} changes corresponding to quickfix, can't be <code>null</code>
     * @return Created {@link TextEdit} quickfix changes, can be <code>null</code> in some cases:
     * <ul>
     * <li>object contained in {@link Issue} isn't instanceof <code>clazz</code></li>
     * <li><code>function</code> return <code>null</code>
     * </ul>
     */
    private TextEdit createChanges(IXtextDocument document, final Issue issue, final Class<E> clazz,
        final Function<E, TextEdit> function)
    {
        return document.readOnly(new IUnitOfWork<TextEdit, XtextResource>()
        {
            @Override
            public TextEdit exec(XtextResource state) throws Exception
            {
                EObject element = state.getEObject(issue.getUriToProblem().fragment());
                if (clazz.isInstance(element))
                    return function.apply(clazz.cast(element));
                else
                    return null;
            }
        });
    }

    /**
     * Gets actual {@link IXtextDocument} from {@link IModificationContext}
     * @param context actual {@link IModificationContext} of quickfix, can't be <code>null</code>
     * @return {@link IXtextDocument} from {@link IModificationContext} or <code>null</code> it there is no
     * implementation of {@link IXtextDocument} for this {@link IModificationContext}
     */
    private IXtextDocument getActualDocument(IModificationContext context)
    {
        return context.getXtextDocument();
    }
}
