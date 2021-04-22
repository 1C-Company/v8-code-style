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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com._1c.g5.v8.dt.core.operations.ProjectPipelineJob;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IWorkspaceOrchestrator;

/**
 * The Eclipse platform job to sort the project.
 * This job allows to append the {@link Queue} of small {@link SortItem sort tasks} while it is running.
 * Callers should call {@link #schedule()} each time after append the job to make sure that this job will
 * complete the sort tasks.
 *
 * @author Dmitriy Marmyshev
 *
 * @see ISortService
 * @see SortItem
 */
public class SortJob
    extends Job
{

    private final BlockingQueue<SortItem> queue = new LinkedBlockingQueue<>();

    private final IDtProject dtProject;

    private final IWorkspaceOrchestrator workspaceOrchestrator;

    private final ISortService sortService;

    /**
     * Instantiates a new sort job.
     *
     * @param dtProject the DT project, cannot be {@link null}.
     * @param sortService the sort service, cannot be {@link null}.
     * @param workspaceOrchestrator the workspace orchestrator, cannot be {@link null}.
     */
    public SortJob(IDtProject dtProject, ISortService sortService, IWorkspaceOrchestrator workspaceOrchestrator)
    {
        super(MessageFormat.format(Messages.SortJob_Sort_metadata_objects__0, dtProject.getName()));
        this.dtProject = dtProject;
        this.sortService = sortService;
        this.workspaceOrchestrator = workspaceOrchestrator;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        if (dtProject.getWorkspaceProject() == null)
            return Status.CANCEL_STATUS;

        Object handler = workspaceOrchestrator.beginBackgroundOperation("Sort-MD-objects", //$NON-NLS-1$
            Arrays.asList(dtProject), ProjectPipelineJob.BUILD);

        try
        {
            while (!queue.isEmpty() && !monitor.isCanceled())
            {
                execute(monitor);
            }
            if (monitor.isCanceled())
                queue.clear();
        }
        finally
        {
            if (monitor.isCanceled())
            {
                workspaceOrchestrator.cancelOperation(handler);
                return Status.CANCEL_STATUS;
            }
            else
            {
                workspaceOrchestrator.endOperation(handler);
            }
        }

        return Status.OK_STATUS;
    }

    /**
     * Gets the queue of sort items.
     *
     * @return the queue, cannot be {@link null}.
     */
    public Queue<SortItem> getQueue()
    {
        return queue;
    }

    private void execute(IProgressMonitor monitor)
    {
        List<SortItem> items = new ArrayList<>();
        SortItem item = null;
        while ((item = queue.poll()) != null && !monitor.isCanceled())
        {
            items.add(item);
        }

        if (!monitor.isCanceled() && !items.isEmpty())
        {
            sortService.sortObject(dtProject, items, monitor);
        }
    }

}
