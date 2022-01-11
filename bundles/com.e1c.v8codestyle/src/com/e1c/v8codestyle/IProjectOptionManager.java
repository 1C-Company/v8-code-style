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
package com.e1c.v8codestyle;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The manager of project functional options.
 *
 * @author Dmitriy Marmyshev
 */
public interface IProjectOptionManager
{

    /**
     * Gets the available options.
     *
     * @return the available options, cannot return {@code null}.
     */
    List<ProjectOption> getAvailableOptions();

    /**
     * Gets the functional option definition.
     *
     * @param optionId the option id, cannot be {@code null}.
     * @return the option definition, can return {@code null} if option is not found.
     */
    ProjectOption getOption(String optionId);

    /**
     * Gets the option for the project.
     *
     * @param project the project, cannot return {@code null}.
     * @param optionId the option id, cannot return {@code null}.
     * @return the option enable
     */
    boolean isOptionEnabled(IProject project, String optionId);

    /**
     * Gets the option for the project.
     *
     * @param project the project, cannot return {@code null}.
     * @param option the option, cannot return {@code null}.
     * @return the option enable
     */
    boolean isOptionEnabled(IProject project, ProjectOption option);

    /**
     * Save option enable for the project.
     *
     * @param project the project, cannot return {@code null}.
     * @param option the option, cannot return {@code null}.
     * @param enable the value of enable or disable the option
     * @param monitor the monitor, cannot return {@code null}.
     */
    void save(IProject project, ProjectOption option, boolean enable, IProgressMonitor monitor);

}
