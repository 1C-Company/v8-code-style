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

import java.util.function.Supplier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.validation.Issue;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.services.BslGrammarAccess;
import com._1c.g5.v8.dt.bsl.ui.BslGeneratorMultiLangProposals;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.lcore.ui.texteditor.IndentTextEditorProvider;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Xtext BSl module quick fix provider
 *
 * @author Vadim Geraskin
 */
@Singleton
public class XtextBslModuleFixProvider
    implements IXtextBslModuleFixProvider
{
    private final IV8ProjectManager v8projectManager;
    private final BslGrammarAccess bslGrammar;
    private final IResourceLookup resourceLookup;
    private final IndentTextEditorProvider indentProvider;
    private final BslGeneratorMultiLangProposals bslGenProp;
    private final IURIEditorOpener editorOpener;

    @Inject
    /**
     * @param v8projectManager V8 project manager, cannot be {@code null}
     * @param bslGrammar BSl grammar, cannot be {@code null}
     * @param resourceLookup resource lookup, cannot be {@code null}
     * @param indentProvider indent provider, cannot be {@code null}
     * @param bslGenProp BSL multi-language proposals generator, cannot be {@code null}
     * @param editorOpener editor opener, cannot be {@code null}
     */
    public XtextBslModuleFixProvider(IV8ProjectManager v8projectManager, BslGrammarAccess bslGrammar,
        IResourceLookup resourceLookup, IndentTextEditorProvider indentProvider,
        BslGeneratorMultiLangProposals bslGenProp, IURIEditorOpener editorOpener)
    {
        this.v8projectManager = Preconditions.checkNotNull(v8projectManager);
        this.bslGrammar = Preconditions.checkNotNull(bslGrammar);
        this.resourceLookup = Preconditions.checkNotNull(resourceLookup);
        this.indentProvider = Preconditions.checkNotNull(indentProvider);
        this.bslGenProp = Preconditions.checkNotNull(bslGenProp);
        this.editorOpener = Preconditions.checkNotNull(editorOpener);
    }

    @Override
    public IXtextBslModuleFixModel getXtextFixModel(IDtProject dtProject, Issue issue, IFixSession session,
        URI targetModuleUri, boolean isInteractive)
    {
        Supplier<Module> moduleSupp = () -> session.getModule(targetModuleUri);
        if (isInteractive)
        {
            return new XtextInteractiveBslModuleFixModel(moduleSupp, v8projectManager, bslGrammar, resourceLookup,
                indentProvider, bslGenProp, issue, dtProject, editorOpener);
        }
        return new XtextBslModuleFixModel(moduleSupp, v8projectManager, bslGrammar, resourceLookup, issue, dtProject);
    }
}
