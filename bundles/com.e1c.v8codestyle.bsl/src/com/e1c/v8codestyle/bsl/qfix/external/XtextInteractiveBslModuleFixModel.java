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

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.validation.Issue;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.services.BslGrammarAccess;
import com._1c.g5.v8.dt.bsl.ui.BslGeneratorMultiLangProposals;
import com._1c.g5.v8.dt.bsl.ui.quickfix.BslQuickFixUtil;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.lcore.ui.texteditor.IndentTextEditorProvider;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.google.common.base.Preconditions;

/**
 * The xtext module quick fix model
 *
 * @author Vadim Geraskin
 */
public class XtextInteractiveBslModuleFixModel
    extends XtextBslModuleFixModel
    implements IXtextInteractiveBslModuleFixModel
{
    private final IndentTextEditorProvider indentProvider;
    private final BslGeneratorMultiLangProposals bslGenProp;
    private final IModificationContext modificationContext;
    private final IXtextDocument document;
    private final LinkedModeModel linkedModeModel;

    /**
    /**
     * Creates module quick fix model
     *
     * @param moduleSupp supplier for {@link Module}, cannot be {@code null}
     * @param v8projectManager V8 project manager, cannot be {@code null}
     * @param bslGrammar BSL grammar, cannot be {@code null}
     * @param resourceLookup resource lookup service, cannot be {@code null}
     * @param indentProvider indent provider, cannot be {@code null}
     * @param bslGenProp Multi-lang BSL proposals generator, cannot be {@code null}
     * @param issue issue, cannot be {@code null}
     * @param dtProject {@code IDtProject}, cannot be {@code null}
     * @param editorOpener {@code IURIEditorOpener}, cannot be {@code null}
     */
    public XtextInteractiveBslModuleFixModel(Supplier<Module> moduleSupp, IV8ProjectManager v8projectManager,
        BslGrammarAccess bslGrammar, IResourceLookup resourceLookup, IndentTextEditorProvider indentProvider,
        BslGeneratorMultiLangProposals bslGenProp, Issue issue, IDtProject dtProject, IURIEditorOpener editorOpener)
    {
        super(moduleSupp, v8projectManager, bslGrammar, resourceLookup, issue, dtProject);
        this.indentProvider = Preconditions.checkNotNull(indentProvider);
        this.bslGenProp = Preconditions.checkNotNull(bslGenProp);
        this.modificationContext = new BslIssueModificationContext(issue, Preconditions.checkNotNull(editorOpener));
        this.document = modificationContext.getXtextDocument(issue.getUriToProblem());

        linkedModeModel = new LinkedModeModel();
        boolean isRussian = getScriptVariant(v8project) == ScriptVariant.RUSSIAN;
        bslGenProp.setRussianLang(isRussian);
    }

    @Override
    public IXtextDocument getDocument()
    {
        return document;
    }

    @Override
    public IndentTextEditorProvider getIndentProvider()
    {
        return indentProvider;
    }

    @Override
    public BslGeneratorMultiLangProposals getBslGeneratorMultiLangProposals()
    {
        return bslGenProp;
    }

    @Override
    public IModificationContext getModificationContext()
    {
        return modificationContext;
    }

    @Override
    public IRegion getLineInformationOfOffset(int offset) throws BadLocationException
    {
        return document.getLineInformationOfOffset(offset);
    }

    @Override
    public String getLineSeparator()
    {
        return PreferenceUtils.getLineSeparator(getResourceLookup().getProject(document.getResourceURI()));
    }

    @Override
    public Optional<String> getFormatString(EObject eObject) throws BadLocationException
    {
        INode node = NodeModelUtils.findActualNodeFor(eObject);
        if (node == null)
        {
            return Optional.empty();
        }
        IRegion lineInformation = getLineInformationOfOffset(node.getOffset());
        String indent = computeFormatLine(document, lineInformation).toString();
        return Optional.ofNullable(indent);
    }

    @Override
    public LinkedModeModel getLinkedModeModel()
    {
        return linkedModeModel;
    }

    @Override
    public void enterUiMode()
    {
        LinkedModeUI ui = new LinkedModeUI(linkedModeModel, BslQuickFixUtil.getTextViewer(modificationContext));
        ui.enter();
    }

    @Override
    public void selectAndRevealForLinkedModeModel(int posStart, int length)
    {
        selectAndRevealForLinkedModeModel(BslQuickFixUtil.getTextViewer(modificationContext), posStart, length);
    }

    private static StringBuilder computeFormatLine(IDocument doc, IRegion lineInformation) throws BadLocationException
    {
        String lineContent = doc.get(lineInformation.getOffset(), lineInformation.getLength());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lineContent.length(); ++i)
        {
            if (Character.isWhitespace(lineContent.charAt(i)))
            {
                builder.append(lineContent.charAt(i));
            }
            else
            {
                break;
            }
        }
        return builder;
    }

    private static void selectAndRevealForLinkedModeModel(ITextViewer viewer, int posStart, int length)
    {
        viewer.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                viewer.getSelectionProvider().removeSelectionChangedListener(this);
                viewer.revealRange(posStart, length);
                viewer.setSelectedRange(posStart, length);
            }
        });
    }
}
