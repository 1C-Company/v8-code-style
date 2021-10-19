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
package com.e1c.v8codestyle.internal.bsl.ui.views;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;

import com._1c.g5.v8.dt.bsl.comment.DocumentationCommentProperties;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Section;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.LinkContainsTypeDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.ui.editor.BslXtextDocument;
import com._1c.g5.v8.dt.common.Pair;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;

/**
 * The listener interface for receiving {@link ISelection} events.
 * The class that is interested in processing a  {@link ISelection}
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * SelectionService <code>addPostSelectionListener</code> method. When
 * the ISelection event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Dmitriy Marmyshev
 */
public class BslDocCommentSelectionListener
    implements ISelectionListener
{

    private final TreeViewer viewer;

    private final IResourceLookup resourceLookup;

    private final IBslPreferences bslPreferences;

    private final BslMultiLineCommentDocumentationProvider commentProvider;

    private final EObjectAtOffsetHelper bslObjectAtOffsetHelper;

    /**
     * Instantiates a new bsl doc comment selection listener.
     *
     * @param viewer the viewer, cannot be {@code null}.
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the bsl preferences, cannot be {@code null}.
     */
    public BslDocCommentSelectionListener(TreeViewer viewer, IResourceLookup resourceLookup,
        IBslPreferences bslPreferences)
    {
        this.viewer = viewer;
        this.resourceLookup = resourceLookup;
        this.bslPreferences = bslPreferences;

        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        this.commentProvider = rsp.get(BslMultiLineCommentDocumentationProvider.class);
        this.bslObjectAtOffsetHelper = rsp.get(EObjectAtOffsetHelper.class);
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {

        BslDocumentationComment doc = null;
        if (selection instanceof IStructuredSelection
            && ((IStructuredSelection)selection).getFirstElement() instanceof Method)
        {
            doc = getDocComment((Method)((IStructuredSelection)selection).getFirstElement());
            viewer.setInput(doc);
        }
        else if (selection instanceof ITextSelection || selection instanceof IMarkSelection)
        {
            int offset = 0;

            if (selection instanceof ITextSelection)
            {
                ITextSelection ts = (ITextSelection)selection;
                offset = ts.getOffset();
            }
            else if (selection instanceof IMarkSelection)
            {
                IMarkSelection ts = (IMarkSelection)selection;
                offset = ts.getOffset();
            }

            Pair<BslDocumentationComment, Object> result = getSelectedDocModel(part, offset);
            if (result != null)
            {
                doc = result.first;
            }
            viewer.setInput(doc);
            if (result != null && result.second != null)
            {
                viewer.setSelection(new StructuredSelection(result.second));
            }
            else
            {
                viewer.setSelection(new StructuredSelection());
            }
        }

    }

    private Pair<BslDocumentationComment, Object> getSelectedDocModel(IWorkbenchPart part, final int offset)
    {
        XtextEditor target = part.getAdapter(XtextEditor.class);
        if (target == null)
        {
            return null;
        }

        IXtextDocument xtextDoc = target.getDocument();
        CancelableUnitOfWork<Pair<BslDocumentationComment, Object>, XtextResource> unit = new CancelableUnitOfWork<>()
        {
            @Override
            public Pair<BslDocumentationComment, Object> exec(XtextResource res, CancelIndicator monitor)
                throws Exception
            {
                if (monitor.isCanceled())
                {
                    return null;
                }

                if (res.getContents() != null && !res.getContents().isEmpty())
                {
                    EObject obj = res.getContents().get(0);

                    // do only for bsl module
                    if (obj instanceof Module)
                    {
                        EObject subObject = bslObjectAtOffsetHelper.resolveElementAt(res, offset);
                        if (subObject instanceof Method)
                        {
                            if (monitor.isCanceled())
                            {
                                return null;
                            }

                            BslDocumentationComment docComment = getDocComment((Method)subObject);
                            Object selected = getSelected(docComment, subObject, offset);
                            return Pair.newPair(docComment, selected);
                        }
                        else if (subObject != null)
                        {
                            for (EObject e = subObject; e != null; e = e.eContainer())
                            {
                                if (monitor.isCanceled())
                                {
                                    return null;
                                }
                                if (e instanceof Method)
                                {
                                    return Pair.newPair(getDocComment((Method)e), null);
                                }
                            }
                        }
                    }
                }
                return null;
            }

        };

        if (xtextDoc instanceof BslXtextDocument)
        {
            return ((BslXtextDocument)xtextDoc).readOnlyDataModelWithoutSync(unit);
        }
        return xtextDoc.readOnly(unit);
    }

    private BslDocumentationComment getDocComment(Method method)
    {
        IProject project = resourceLookup.getProject(method);
        DocumentationCommentProperties props = bslPreferences.getDocumentCommentProperties(project);

        return BslCommentUtils.parseTemplateComment(method, props.oldCommentFormat(), commentProvider);
    }

    private Object getSelected(BslDocumentationComment docComment, EObject method, int offset)
    {
        if (docComment == null)
        {
            return null;
        }

        List<INode> nodes = commentProvider.getDocumentationNodes(method);
        if (nodes.isEmpty())
        {
            return null;
        }

        INode actual = null;
        for (INode node : nodes)
        {
            if (node.getOffset() > offset)
            {
                break;
            }
            actual = node;
        }

        if (actual != null && actual.getEndOffset() > offset)
        {
            int actualOffset = offset - actual.getOffset();
            int line = nodes.indexOf(actual);

            if (line > -1 && actualOffset > 0)
            {
                return getSelected(docComment, line, actualOffset);
            }
        }

        return null;
    }

    private Object getListSelected(List<?> objects, int line, int offset)
    {
        if (objects == null)
        {
            return null;
        }
        for (Object object : objects)
        {
            Object selected = getSelected(object, line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        return null;
    }

    private Object getSelected(Object object, int line, int offset)
    {

        if (object instanceof TextPart)
        {
            TextPart section = (TextPart)object;
            if (section.getLineNumber() == line && section.getOffset() < offset
                && section.getOffset() + section.getText().length() > offset)
            {
                return section;
            }
        }
        else if (object instanceof LinkPart)
        {
            LinkPart section = (LinkPart)object;
            if (section.getLineNumber() == line && section.getOffset() < offset
                && section.getLinkTextOffset() + section.getLinkText().length() > offset)
            {
                return section;
            }
        }
        else if (object instanceof FieldDefinition)
        {
            FieldDefinition section = (FieldDefinition)object;
            if (section.getLineNumber() == line && section.getNameOffset() < offset
                && section.getNameOffset() + section.getName().length() > offset)
            {
                return section;
            }
            Object selected = getSelected(section.getDescription(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getListSelected(section.getTypeSections(), line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        else if (object instanceof LinkContainsTypeDefinition)
        {
            LinkContainsTypeDefinition section = (LinkContainsTypeDefinition)object;
            Object selected = getSelected(section.getLink(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getListSelected(section.getContainTypes(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getListSelected(section.getFieldDefinitionExtension(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getSelected(section.getLinkToExtensionFields(), line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        else if (object instanceof TypeDefinition)
        {
            TypeDefinition section = (TypeDefinition)object;
            if (section.getLineNumber() == line && section.getNameOffset() < offset
                && section.getNameOffset() + section.getTypeName().length() > offset)
            {
                return section;
            }

            Object selected = getListSelected(section.getContainTypes(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getListSelected(section.getFieldDefinitionExtension(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getSelected(section.getLinkToExtensionFields(), line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        else if (object instanceof BslDocumentationComment)
        {
            BslDocumentationComment root = (BslDocumentationComment)object;

            Object selected = getSelected(root.getDescription(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getSelected(root.getParametersSection(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getSelected(root.getCallOptionsSection(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getSelected(root.getExampleSection(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getSelected(root.getReturnSection(), line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        else if (object instanceof Description)
        {
            return getListSelected(((Description)object).getParts(), line, offset);
        }
        else if (object instanceof ParametersSection)
        {
            ParametersSection section = (ParametersSection)object;
            if (section.getLineNumber() == line && section.getOffset() < offset
                && section.getOffset() + section.getHeaderKeywordLength() > offset)
            {
                return section;
            }
            Object selected = getSelected(section.getDescription(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getListSelected(section.getParameterDefinitions(), line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        else if (object instanceof ReturnSection)
        {
            ReturnSection section = (ReturnSection)object;
            if (section.getLineNumber() == line && section.getOffset() < offset
                && section.getOffset() + section.getHeaderKeywordLength() > offset)
            {
                return section;
            }
            Object selected = getSelected(section.getDescription(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getListSelected(section.getReturnTypes(), line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        else if (object instanceof TypeSection)
        {
            TypeSection section = (TypeSection)object;
            if (section.getLineNumber() == line && section.getOffset() < offset
                && section.getOffset() + section.getHeaderKeywordLength() > offset)
            {
                return section;
            }
            Object selected = getSelected(section.getDescription(), line, offset);
            if (selected != null)
            {
                return selected;
            }
            selected = getListSelected(section.getTypeDefinitions(), line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        else if (object instanceof Section)
        {
            Section section = (Section)object;
            if (section.getLineNumber() == line && section.getOffset() < offset
                && section.getOffset() + section.getHeaderKeywordLength() > offset)
            {
                return section;
            }
            Object selected = getSelected(section.getDescription(), line, offset);
            if (selected != null)
            {
                return selected;
            }
        }
        return null;
    }
}
