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
package com.e1c.v8codestyle.autosort;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com._1c.g5.v8.dt.core.platform.IDtProject;

/**
 * The service allows to sort metadata object of the project.
 * Service register listener to {@link IBmModel} of each project and runs
 *
 * @author Dmitriy Marmyshev
 */
public interface ISortService
{

    /** The name of the service */
    String SERVICE_NAME = "SortService"; //$NON-NLS-1$

    /**
     * Sort all metadata object in the project.
     *
     * @param dtProject the DT project to sort, cannot be {@code null}.
     * @param monitor the status progress monitor, cannot be {@code null}.
     * @return the status of the sort operation, cannot be {@code null}.
     */
    IStatus sortAllMetadata(IDtProject dtProject, IProgressMonitor monitor);

    /**
     * Sort objects of the project.
     *
     * @param dtProject the DT project to sort, cannot be {@code null}.
     * @param items the sort items of object and it's collections that need to sort, cannot be {@code null}.
     * @param monitor the status progress monitor, cannot be {@code null}.
     * @return the status of the sort operation, cannot return {@code null}.
     */
    IStatus sortObject(IDtProject dtProject, Collection<SortItem> items, IProgressMonitor monitor);

    /**
     * Start a-synchronize job to sort all metadata objects of the project.
     *
     * @param project the project to start sort job, cannot be {@code null}.
     */
    void startSortAllMetadata(IProject project);

    /**
     * Start a-synchronize job to sort objects of the project.
     *
     * @param project the project to start sort job, cannot be {@code null}.
     * @param items the sort items of object and it's collections that need to sort, cannot be {@code null}.
     */
    void startSortObject(IProject project, Collection<SortItem> items);
}
