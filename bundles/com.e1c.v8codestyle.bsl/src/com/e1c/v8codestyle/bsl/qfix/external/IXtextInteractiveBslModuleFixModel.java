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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;

import com._1c.g5.v8.dt.bsl.ui.BslGeneratorMultiLangProposals;
import com._1c.g5.v8.dt.lcore.ui.texteditor.IndentTextEditorProvider;

/**
 * Contract for xtext BSL interactive quick fix model
 *
 * @author Vadim Geraskin
 */
public interface IXtextInteractiveBslModuleFixModel
    extends IXtextBslModuleFixModel
{
    /**
     * Provides {@link IndentTextEditorProvider}
     *
     * @return {@link IndentTextEditorProvider}, never {@code null}
     */
    IndentTextEditorProvider getIndentProvider();

    /**
     * Multilanguage proposals for quick-fix and content-assist for Bsl
     *
     * @return reference to {@link BslGeneratorMultiLangProposals}, never {@code null}
     */
    BslGeneratorMultiLangProposals getBslGeneratorMultiLangProposals();

    /**
     * Provides modification context
     *
     * @return {@link IModificationContext}, never {@code null}
     */
    IModificationContext getModificationContext();

    /**
     * Provides the format string for the given {@code EObject} which is the semantic object whose node should
     * be provided {@code NodeModelUtils.findActualNodeFor()}
     *
     * @param eObject the object, cannot be {@code null}
     * @return format string, never {@code null}
     * @throws BadLocationException
     */
    Optional<String> getFormatString(EObject eObject) throws BadLocationException;

    /**
     * Provides {@code LinkedModeModel}
     *
     * @return {@code LinkedModeModel}, never {@code null}
     */
    LinkedModeModel getLinkedModeModel();

    /**
     * Returns a description of the line at the given offset.The description contains the offset and the length of the
     * line excluding the line's delimiter.
     *
     * @param offset the offset whose line should be described
     * @return a region describing the line, never {@code null}
     * @throws BadLocationException
     */
    IRegion getLineInformationOfOffset(int offset) throws BadLocationException;

    /**
     * Connects the ui mode for linked mode and starts UI on the first position
     * Should be called write after installation of linkedModeModel: {@code linkedModeModel.forceInstall()}
     */
    void enterUIMode();

    /**
     * Sets the selected range and reveals it
     *
     * @param posStart the start position
     * @param length the length of selection
     */
    void selectAndRevealForLinkedModeModel(int posStart, int length);
}
