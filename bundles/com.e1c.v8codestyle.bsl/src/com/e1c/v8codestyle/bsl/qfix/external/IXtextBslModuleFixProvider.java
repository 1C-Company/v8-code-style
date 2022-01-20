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
import org.eclipse.xtext.validation.Issue;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;

/**
 * Contract for {@link XtextBslModuleFixProvider}
 *
 * @author Vadim Geraskin
 */
public interface IXtextBslModuleFixProvider
{
    /**
     * Returns xtext quick fix model
     *
     * @param dtProject the {@IDtProject}, cannot be {@code null}
     * @param issue instance of the {@link Issue}, cannot be {@code null}
     * @param session the {@code IFixSession}, cannot be {@code null}
     * @param targetModuleUri the target module Uri, cannot be {@code null}
     * @param isInteractive {@code true} if quick fix supports interactive (UI) model, {@code false} otherwise
     * @return the quick fix model, never {@code null}
     */
    IXtextBslModuleFixModel getXtextFixModel(IDtProject dtProject, Issue issue, IFixSession session,
        URI targetModuleUri, boolean isInteractive);
}
