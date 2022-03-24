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
import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.validation.Issue;

import com._1c.g5.v8.dt.bsl.services.BslGrammarAccess;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;

/**
 * Contract for xtext BSL quick fix model
 *
 * @author Vadim Geraskin
 */
public interface IXtextBslModuleFixModel
{
    /**
     * Provides {@link IDocument}
     *
     * @return {@link IDocument}, never {@code null}
     */
    IDocument getDocument();

    /**
     * Provides module {@link IResourceLookup}
     *
     * @return {link IResourceLookup}, never {@code null}
     */
    IResourceLookup getResourceLookup();

    /**
     * Provides the element which is used to create a quick fix
     *
     * @return the element, can be {@code null}
     */
    EObject getElement();

    /**
     * xtext validation issue reference
     *
     * @return {@link Issue}, never {@code null}
     */
    Issue getIssue();

    /**
     * The associated user data. May be {@code null} or empty but may not contain {@code null} entries.
     *
     * @return user data, can be {@code null}
     */
    String[] getIssueData();

    /**
     * Provides {@link BslGrammarAccess}
     *
     * @return {@link BslGrammarAccess}, never {@code null}
     */
    BslGrammarAccess getBslGrammar();

    /**
     * Returns {@code ScriptVariant} for current project
     *
     * @return {@code ScriptVariant}, never {@code null}
     */
    ScriptVariant getScriptVariant();

    /**
     * Gets line separator preference set for current project. If the preference is not set for the project then
     * the canonical lookup order is used (instance, configuration, default).
     *
     * @return the line separator preference. Never {@code null}.
     */
    String getLineSeparator();
}
