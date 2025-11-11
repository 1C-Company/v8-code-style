/*******************************************************************************
 * Copyright (C) 2024, 1C-Soft LLC and others.
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

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.handly.buffer.BufferChange;
import org.eclipse.handly.snapshot.NonExpiringSnapshot;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.Triple;
import org.eclipse.xtext.util.Tuples;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;

import com._1c.g5.ides.ui.texteditor.xtext.embedded.EmbeddedEditorBuffer;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.ui.contentassist.BslProposalProvider;
import com._1c.g5.v8.dt.bsl.ui.menu.BslHandlerUtil;
import com._1c.g5.v8.dt.bsl.util.BslUtil;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.model.EditingMode;
import com._1c.g5.v8.dt.core.model.IModelEditingSupport;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.lcore.nodemodel.util.CustomNodeModelUtils;
import com._1c.g5.v8.dt.lcore.ui.texteditor.IndentTextEditorProvider;
import com.e1c.v8codestyle.bsl.comment.BslDocCommentSerializer;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;
import com.google.inject.Inject;

/**
 * The Handler to format current method's documentation comment in opened in BSL editor.
 *
 * @author Dmitriy Marmyshev
 */
public class FormatDocCommentModuleEditorHandler
    extends AbstractHandler
{

    @Inject
    private BslMultiLineCommentDocumentationProvider commentProvider;

    @Inject
    private BslProposalProvider proposalProvider;

    @Inject
    private IBslPreferences bslPreferences;

    @Inject
    private IV8ProjectManager v8projectManager;

    @Inject
    private IndentTextEditorProvider indentProvider;

    @Inject
    private IModelEditingSupport modelEditingSupport;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);

        XtextEditor target = BslHandlerUtil.extractXtextEditor(part);
        if (target != null)
        {
            final ITextViewer viewer = BslHandlerUtil.getTextViewer(target);
            IXtextDocument document = target.getDocument();

            Triple<Integer, Integer, String> content =
                document.readOnly(new CancelableUnitOfWork<Triple<Integer, Integer, String>, XtextResource>()
                {
                    @Override
                    public Triple<Integer, Integer, String> exec(XtextResource resource, CancelIndicator monitor)
                        throws Exception
                    {
                        if (resource.getContents() != null && !resource.getContents().isEmpty())
                        {
                            EObject obj = resource.getContents().get(0);
                            if (obj instanceof Module)
                                return getFormatedDocComment((Module)obj, viewer);
                        }
                        return null;
                    }

                });

            if (content != null && content.getFirst() > -1 && content.getSecond() > -1 && content.getThird() != null)
            {
                replaceDocComment(document, content.getThird(), content.getFirst(), content.getSecond());
            }

        }
        return null;
    }

    private void replaceDocComment(IXtextDocument document, String comment, int insertOffset, int length)
    {
        TextEdit change = new ReplaceEdit(insertOffset, length, comment);

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

    private Triple<Integer, Integer, String> getFormatedDocComment(Module module, ITextViewer viewer)
    {
        if (!modelEditingSupport.canEdit(module, EditingMode.DIRECT))
        {
            return null;
        }

        Method method = getNearestMethod(module, (viewer.getSelectedRange()).x);
        if (method != null)
        {
            List<INode> lines = commentProvider.getDocumentationNodes(method);
            if (lines.isEmpty())
            {
                return null;
            }
            int methodOffset = lines.get(0).getOffset();
            int length = lines.get(lines.size() - 1).getEndOffset() - methodOffset;

            String lineFormatter = proposalProvider.getLineFormatter(module, viewer.getDocument(), methodOffset);
            IV8Project project = v8projectManager.getProject(module);
            boolean oldFormat = (project != null && project.getProject() != null)
                ? this.bslPreferences.getDocumentCommentProperties(project.getProject()).oldCommentFormat() : true;

            BslDocumentationComment docComment =
                BslCommentUtils.parseTemplateComment(method, oldFormat, commentProvider);
            if (isEmpty(docComment.getDescription()))
            {
                docComment.getDescription().getParts().clear();
            }

            boolean isRussian = BslUtil.isRussian(method, v8projectManager);
            String indent = indentProvider.getIndent();

            String lineSeparator = resolveLineSeparator(project);
            String comment = BslDocCommentSerializer.newBuilder()
                .setOldFormat(oldFormat)
                .setScriptVariant(isRussian)
                .lineSeparator(lineSeparator)
                .ignoreLineNumbers()
                .build()
                .serialize(docComment, lineFormatter, indent)
                .concat(lineSeparator);

            return Tuples.create(methodOffset, length, comment);
        }
        return null;
    }

    private boolean isEmpty(Description description)
    {
        for (IDescriptionPart part : description.getParts())
        {
            if (part instanceof TextPart)
            {
                String text = ((TextPart)part).getText();
                if (StringUtils.isNotBlank(text))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    private Method getNearestMethod(Module module, int offset)
    {
        ICompositeNode iCompositeNode = NodeModelUtils.findActualNodeFor(module);
        ILeafNode node = CustomNodeModelUtils.findLeafNodeAtOffset(iCompositeNode, offset);
        EObject actualObject = NodeModelUtils.findActualSemanticObjectFor(node);
        if (actualObject instanceof Method)
            return (Method)actualObject;
        return EcoreUtil2.getContainerOfType(actualObject, Method.class);
    }

    private static String resolveLineSeparator(IV8Project v8project)
    {
        if (v8project == null)
            return System.lineSeparator();
        return PreferenceUtils.getLineSeparator(v8project.getProject());
    }

}
