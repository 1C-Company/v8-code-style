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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.common.StringUtils;

/**
 * The provider of project option.
 *
 * @author Dmitriy Marmyshev
 */
public interface IProjectOptionProvider
{

    /**
     * Gets the id of option.
     *
     * @return the id, cannot return {@code null} or string with spaces or special symbols.
     */
    String getId();

    /**
     * Gets the presentation (short title) of the available option.
     *
     * @return the presentation
     */
    String getPresentation();

    /**
     * Gets the long description or explanation of the option for the project.
     *
     * @return the description
     */
    default String getDescription()
    {
        return StringUtils.EMPTY;
    }

    /**
     * Gets the order of position in list of all options.
     *
     * @return the order
     */
    int getOrder();

    /**
     * Gets the option enabled for the project or not.
     *
     * @param project the project, cannot be {@code null}.
     * @return true if the option is enabled for the project.
     */
    boolean getOption(IProject project);

    /**
     * Gets the default value of enable for new project.
     *
     * @return the default value.
     */
    boolean getDefault();

    /**
     * Save function option for the project.
     *
     * @param project the project, cannot be {@code null}.
     * @param value the value
     * @param monitor the monitor, cannot be {@code null}.
     */
    void saveOption(IProject project, boolean value, IProgressMonitor monitor);
}
