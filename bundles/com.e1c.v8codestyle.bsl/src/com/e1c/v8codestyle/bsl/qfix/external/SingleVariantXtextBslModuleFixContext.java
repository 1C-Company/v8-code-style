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

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.validation.Issue.IssueImpl;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.components.BasicFixContext;

/**
 * Single-variant xtext module quick fix context
 *
 * @author Vadim Geraskin
 */
public class SingleVariantXtextBslModuleFixContext
    extends BasicFixContext
{
    private final URI targetModuleUri;
    private final IssueImpl issue;
    private final IXtextBslModuleFixProvider provider;

    /**
     * Creates quick fix context
     *
     * @param targetModuleUri the module {@code URI}, cannot be {@code null}
     * @param issue the {@code IssueImpl} instance, cannot be {@code null}
     * @param provider the {@code IXtextBslModuleFixProvider} instance, cannot be {@code null}
     * @param dtProject the DT project, cannot be {@code null}
     */
    public SingleVariantXtextBslModuleFixContext(URI targetModuleUri, IssueImpl issue,
        IXtextBslModuleFixProvider provider, IDtProject dtProject)
    {
        super(dtProject);
        this.targetModuleUri = targetModuleUri;
        this.issue = issue;
        this.provider = provider;
    }

    /**
     * Provides the target module URI
     *
     * @return the targetModuleUri, never {@code null}
     */
    public URI getTargetModuleUri()
    {
        return targetModuleUri;
    }

    /**
     * Provides the {@code IssueImpl} for quick fix
     *
     * @return xtext issue, never {@code null}
     */
    public IssueImpl getIssue()
    {
        return issue;
    }

    /**
     * Provides the model {@code IXtextBslModuleFixModel} for quick fix
     *
     * @param session {@link IFixSession}, cannot be {@code null}
     * @param isInteractive {@code true} if quick fix supports inbteractive (UI) model, {@code false} otherwise
     * @return model {@code IXtextBslModuleFixModel}, never {@code null}
     */
    public IXtextBslModuleFixModel getModel(IFixSession session, boolean isInteractive)
    {
        return provider.getXtextFixModel(getDtProject(), issue, session, targetModuleUri, isInteractive);
    }
}
