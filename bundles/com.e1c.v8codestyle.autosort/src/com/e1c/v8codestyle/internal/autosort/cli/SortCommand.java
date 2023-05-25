/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.internal.autosort.cli;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import com._1c.g5.v8.dt.core.operations.ProjectPipelineJob;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com.e1c.g5.v8.dt.cli.api.Argument;
import com.e1c.g5.v8.dt.cli.api.CliCommand;
import com.e1c.g5.v8.dt.cli.api.components.BaseCliCommand;
import com.e1c.v8codestyle.autosort.AutoSortPreferences;
import com.e1c.v8codestyle.autosort.ISortService;
import com.e1c.v8codestyle.internal.autosort.AutoSortPlugin;
import com.google.inject.Inject;

/**
 * The CLI command allows to import and to sort project in workspace or to sort existing project in workspace.
 * When it runs on each project it turns on default sort settings if not set up.
 *
 * @author Dmitriy Marmyshev
 */
public class SortCommand
    extends BaseCliCommand
{

    private final ISortService sortService;

    @Inject
    public SortCommand(ISortService sortService)
    {
        this.sortService = sortService;
    }

    /**
     * Import projects into workspace and sort projects.
     *
     * @param projectPaths the project paths where to find all projects recursively
     * @return the status of command
     */
    @CliCommand(command = "sort-project", value = "SortCommand_Description")
    public IStatus importAndSortProjects(@Argument(value = "--projects", elementType = Path.class,
        descriptor = "SortCommand_Project_paths_Description") Path[] projectPaths)
    {
        if (projectPaths == null || projectPaths.length == 0)
        {
            return Status.OK_STATUS;
        }

        Path[] paths = new Path[projectPaths.length];
        for (int i = 0; i < projectPaths.length; i++)
        {
            Path path = projectPaths[i];
            paths[i] = path.toAbsolutePath();
        }

        Collection<File> projectFilePaths = findProjectsRecursively(paths);

        List<IDtProject> projects = new ArrayList<>();
        for (File prjectFile : projectFilePaths)
        {
            IDtProject dtProject = startDtProject(prjectFile.getParentFile().toPath());
            if (dtProject != null && dtProject.getWorkspaceProject() != null)
            {
                projects.add(dtProject);
            }
            else
            {
                logError(
                    MessageFormat.format(Messages.SortCommand_Project__0__did_not_import_into_workspace, prjectFile));
            }
        }
        if (projects.isEmpty())
        {
            // here we tried to import any project but not found - so command should be unsuccessful
            return Status.CANCEL_STATUS;
        }

        sortProjects(projects);

        return Status.OK_STATUS;
    }

    /**
     * Sort projects that are existing in workspace.
     *
     * @param projectNames the project names
     * @return the status of command
     */
    @CliCommand(command = "sort-project", value = "SortCommand_Description")
    public IStatus sortExistingProjects(@Argument(value = "--project-names", elementType = IProject.class,
        descriptor = "SortCommand_Project_names_Description") IProject[] projectNames)
    {
        if (projectNames == null || projectNames.length == 0)
        {
            return Status.OK_STATUS;
        }
        List<IDtProject> projects = new ArrayList<>();

        for (int i = 0; i < projectNames.length; i++)
        {
            IProject project = projectNames[i];
            if (!project.isAccessible())
            {
                String error =
                    MessageFormat.format(Messages.SortCommand_Project__0__not_found_in_workspace, project.getName());
                logError(error);
                return AutoSortPlugin.createErrorStatus(error, null);
            }
            waitUntilStarted(project, DT_PROJECT_STARTUP_DURATION);
            IDtProject dtProject = getContext().getDtProjectManager().getDtProject(project);
            if (dtProject != null && dtProject.getWorkspaceProject() != null)
            {
                projects.add(dtProject);
            }
            else
            {
                logError(
                    MessageFormat.format(Messages.SortCommand_Project__0__not_found_in_workspace, project.getName()));
            }
        }

        sortProjects(projects);

        return Status.OK_STATUS;
    }

    private void sortProjects(List<IDtProject> projects)
    {
        String info = MessageFormat.format(Messages.SortCommand_Sort_projects__0,
            projects.stream().map(IDtProject::getName).collect(Collectors.joining(", "))); //$NON-NLS-1$
        getContext().getLogger().info(info);

        for (IDtProject project : projects)
        {
            if (!AutoSortPreferences.isAutoSortEnabled(project.getWorkspaceProject()))
            {
                AutoSortPreferences.setupProjectDefault(project.getWorkspaceProject());
            }
            exclusiveOperation("Sort-MD-objects", project, ProjectPipelineJob.AFTER_BUILD_DD, () -> { //$NON-NLS-1$
                sortService.sortAllMetadata(project, new NullProgressMonitor());
                return null;
            });

            info = MessageFormat.format(Messages.SortCommand_Sort_project__0__finished, project.getName());
            getContext().getLogger().info(info);
        }

        for (IDtProject project : projects)
        {
            // wait here to build project after sorting, to avoid dropping/stopping project while saving
            exclusiveOperation("After-Sort-MD-objects", project, ProjectPipelineJob.AFTER_BUILD_DD, () -> null); //$NON-NLS-1$
        }
    }
}
