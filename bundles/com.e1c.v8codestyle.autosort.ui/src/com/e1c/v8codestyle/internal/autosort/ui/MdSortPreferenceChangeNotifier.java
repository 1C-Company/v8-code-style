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
package com.e1c.v8codestyle.internal.autosort.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com._1c.g5.v8.dt.core.lifecycle.ProjectContext;
import com._1c.g5.v8.dt.core.platform.IConfigurationAware;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.lifecycle.LifecycleParticipant;
import com._1c.g5.v8.dt.lifecycle.LifecyclePhase;
import com._1c.g5.v8.dt.lifecycle.LifecycleService;
import com._1c.g5.v8.dt.md.sort.MdSortPreferences;
import com.e1c.v8codestyle.autosort.AutoSortPreferences;
import com.e1c.v8codestyle.autosort.ISortService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The notifier of changes in the metadata objects sort preferences to run metadata objects sorting if necessary.
 *
 * @author Almaz Nasibullin
 */
@Singleton
@LifecycleService(name = MdSortPreferenceChangeNotifier.SERVICE_NAME)
public class MdSortPreferenceChangeNotifier
{
    /**
     * The service name.
     */
    public static final String SERVICE_NAME = "MdSortPreferenceChangeNotifier"; //$NON-NLS-1$

    private final Map<IProject, MdSortPreferenceChangeListener> listeners = new ConcurrentHashMap<>();

    @Inject
    private ISortService sortService;

    @Inject
    private IV8ProjectManager v8ProjectManager;

    @LifecycleParticipant(phase = LifecyclePhase.RESOURCE_LOADING, dependsOn = { IV8ProjectManager.SERVICE_NAME })
    public void init(ProjectContext projectContext)
    {
        IDtProject dtProject = projectContext.getProject();
        if (dtProject.getType().equals(IDtProject.WORKSPACE_PROJECT_TYPE))
        {
            IV8Project v8Project = v8ProjectManager.getProject(dtProject);
            if (v8Project instanceof IConfigurationAware)
            {
                IProject project = dtProject.getWorkspaceProject();
                MdSortPreferenceChangeListener listener = new MdSortPreferenceChangeListener(project);
                MdSortPreferences.getPreferences(project).addPreferenceChangeListener(listener);
                listeners.put(project, listener);
            }
        }
    }

    @LifecycleParticipant(phase = LifecyclePhase.RESOURCE_UNLOADING)
    public void dispose(ProjectContext projectContext)
    {
        IDtProject dtProject = projectContext.getProject();
        if (dtProject.getType().equals(IDtProject.WORKSPACE_PROJECT_TYPE))
        {
            IProject project = dtProject.getWorkspaceProject();
            MdSortPreferenceChangeListener listener = listeners.remove(project);
            if (listener != null)
            {
                MdSortPreferences.getPreferences(project).removePreferenceChangeListener(listener);
            }
        }
    }

    private class MdSortPreferenceChangeListener
        implements IPreferenceChangeListener
    {
        private final IProject project;
        private final AtomicBoolean mdSortPreferenceChanged = new AtomicBoolean(false);

        private MdSortPreferenceChangeListener(IProject project)
        {
            this.project = project;
        }

        @Override
        public void preferenceChange(PreferenceChangeEvent event)
        {
            String key = event.getKey();
            if ((MdSortPreferences.ASCENDING_SORT.equals(key) || MdSortPreferences.NATURAL_SORT_ORDER.equals(key))
                && AutoSortPreferences.isAutoSortEnabled(project))
            {
                boolean askToSortMetadataObjects = mdSortPreferenceChanged.compareAndSet(false, true);
                if (askToSortMetadataObjects)
                {
                    Job job = Job.create(Messages.MdSortPreferenceChangeListener_Job_name, monitor -> {
                        Assert.isTrue(mdSortPreferenceChanged.compareAndSet(true, false));
                        startSortAllMetadataObjectsIfNecessary();
                        return Status.OK_STATUS;
                    });
                    // run job with delay if few settings have changed in one moment
                    job.schedule(500);
                }
            }
        }

        private void startSortAllMetadataObjectsIfNecessary()
        {
            Display.getDefault().syncExec(() -> {
                IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                Shell shell = activeWorkbenchWindow != null ? activeWorkbenchWindow.getShell() : null;
                if (shell != null
                    && MessageDialog.openQuestion(shell, Messages.MdSortPreferenceChangeListener_Sort_question_title,
                        Messages.MdSortPreferenceChangeListener_Sort_question))
                {
                    sortService.startSortAllMetadata(project);
                }
            });
        }
    }
}
