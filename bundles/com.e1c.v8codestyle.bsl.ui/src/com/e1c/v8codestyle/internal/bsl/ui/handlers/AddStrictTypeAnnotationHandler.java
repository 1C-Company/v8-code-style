/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.internal.bsl.ui.handlers;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com._1c.g5.v8.dt.core.model.EditingMode;
import com._1c.g5.v8.dt.core.model.IModelEditingSupport;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;
import com.google.inject.Inject;

/**
 * The Handler collects selection of objects and start {@link Job} by each project
 * to add {@code @strict-types} annotation.
 * Handler support {@link IProject}, {@link MdObject} and any files and folders.
 *
 * @author Dmitriy Marmyshev
 */
public class AddStrictTypeAnnotationHandler
    extends AbstractHandler
{

    @Inject
    private IResourceLookup resourceLookup;

    @Inject
    private IModelEditingSupport modelEditingSupport;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
        if (!selection.isEmpty())
        {
            processSelection(selection);
        }

        return null;
    }

    private void processSelection(IStructuredSelection selection)
    {
        Collection<IProject> projects = getSelectedProjects(selection);
        if (!projects.isEmpty())
        {
            projects.forEach(this::startProcessProject);
            return;
        }

        Map<IProject, Collection<MdObject>> mdObjects = getSelectedMdObjects(selection);
        if (!mdObjects.isEmpty())
        {
            mdObjects.forEach(this::startProcessMdObjects);
            return;
        }
        Map<IProject, Collection<IResource>> resource = getSelectedResources(selection);
        if (!resource.isEmpty())
        {
            resource.forEach(this::startProcessResources);
        }
    }

    private Collection<IProject> getSelectedProjects(IStructuredSelection selection)
    {
        Collection<IProject> result = new ArrayList<>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();)
        {
            Object object = iterator.next();
            if (object instanceof IProject)
            {
                result.add((IProject)object);
            }
        }

        return result;
    }

    private Map<IProject, Collection<MdObject>> getSelectedMdObjects(IStructuredSelection selection)
    {
        Map<IProject, Collection<MdObject>> result = new HashMap<>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();)
        {
            Object object = iterator.next();
            if (object instanceof MdObject)
            {
                IProject project = resourceLookup.getProject((MdObject)object);
                if (project != null)
                {
                    result.computeIfAbsent(project, p -> new ArrayList<>()).add((MdObject)object);
                }
            }
        }
        return result;
    }

    private Map<IProject, Collection<IResource>> getSelectedResources(IStructuredSelection selection)
    {
        Map<IProject, Collection<IResource>> result = new HashMap<>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();)
        {
            Object object = iterator.next();
            IResource adapted = Adapters.adapt(object, IResource.class, true);
            if (adapted != null && adapted.getProject() != null)
            {
                result.computeIfAbsent(adapted.getProject(), p -> new ArrayList<>()).add(adapted);
            }
        }
        return result;
    }

    private void startProcessProject(IProject project)
    {
        createJobAndSchedule(project, m -> processProject(project, m));
    }

    private void startProcessMdObjects(IProject project, Collection<MdObject> mdObjects)
    {
        createJobAndSchedule(project, m -> processMdObjects(project, mdObjects, m));
    }

    private void startProcessResources(IProject project, Collection<IResource> resource)
    {
        createJobAndSchedule(project, m -> processResources(project, resource, m));
    }

    private void createJobAndSchedule(IProject project, ICoreRunnable runnable)
    {
        String title = MessageFormat.format(Messages.AddStrictTypeAnnotationHandler_Job_title, project.getName());
        Job job = Job.create(title, runnable);
        job.setUser(true);
        job.setRule(project);
        job.schedule();
    }

    private void processProject(IProject project, IProgressMonitor monitor)
    {
        if (!project.isAccessible())
        {
            return;
        }

        monitor.beginTask(Messages.AddStrictTypeAnnotationHandler_Get_prject_module_files, IProgressMonitor.UNKNOWN);

        Collection<IFile> files = new HashSet<>();

        appendFilesHierarchically(project, files, monitor);
        monitor.done();

        if (monitor.isCanceled() || files.isEmpty())
        {
            return;
        }

        updateFiles(files, monitor);
    }

    private void processMdObjects(IProject project, Collection<MdObject> mdObjects, IProgressMonitor monitor)
    {

        if (!project.isAccessible())
        {
            return;
        }

        monitor.beginTask(Messages.AddStrictTypeAnnotationHandler_Get_prject_module_files, IProgressMonitor.UNKNOWN);

        Collection<IFile> files = getFiles(mdObjects, monitor);
        monitor.done();

        if (monitor.isCanceled() || files.isEmpty())
        {
            return;
        }

        updateFiles(files, monitor);

    }

    private void processResources(IProject project, Collection<IResource> resources, IProgressMonitor monitor)
    {

        if (!project.isAccessible())
        {
            return;
        }

        monitor.beginTask(Messages.AddStrictTypeAnnotationHandler_Get_prject_module_files, IProgressMonitor.UNKNOWN);

        Collection<IFile> files = new HashSet<>();

        for (IResource resource : resources)
        {
            if (monitor.isCanceled())
            {
                return;
            }
            appendFilesHierarchically(resource, files, monitor);
        }
        monitor.done();

        if (monitor.isCanceled() || files.isEmpty())
        {
            return;
        }

        updateFiles(files, monitor);

    }

    private void appendFilesHierarchically(IResource resource, Collection<IFile> files, IProgressMonitor monitor)
    {
        try
        {
            resource.accept(r -> {

                if (monitor.isCanceled())
                {
                    return false;
                }

                if (r instanceof IFile)
                {

                    IFile file = (IFile)r;
                    if (needUpdate(file))
                    {
                        files.add(file);
                    }
                    return false;
                }
                return true;
            });
        }
        catch (CoreException e)
        {
            UiPlugin.logError(e);
        }
    }

    private void updateFiles(Collection<IFile> files, IProgressMonitor monitor)
    {
        monitor.beginTask(Messages.AddStrictTypeAnnotationHandler_Update_module_files, files.size());

        for (IFile file : files)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            addAnnotationIfAbsent(file, monitor);

            monitor.worked(1);
        }
        monitor.done();
    }

    private boolean needUpdate(IFile file)
    {
        if (StrictTypeUtil.BSL_FILE_EXTENSION.equals(file.getFileExtension()) && file.isAccessible())
        {
            try
            {
                return !StrictTypeUtil.hasStrictTypeAnnotation(file);
            }
            catch (CoreException | IOException e)
            {
                UiPlugin.logError(e);
            }
        }
        return false;
    }

    private void addAnnotationIfAbsent(IFile file, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }
        try
        {
            if (!StrictTypeUtil.hasStrictTypeAnnotation(file)
                && modelEditingSupport.canEdit(createModuleProxy(file), EditingMode.DIRECT) && !monitor.isCanceled())
            {
                StrictTypeUtil.setStrictTypeAnnotation(file, monitor);
            }
        }
        catch (CoreException | IOException e)
        {
            IStatus status = UiPlugin.createErrorStatus("Can't update bsl file with name: " + file.getName(), e); //$NON-NLS-1$
            UiPlugin.log(status);
        }

    }

    private EObject createModuleProxy(IFile bslFile)
    {
        EObject module = EcoreUtil.create(MODULE);
        URI uri = URI.createPlatformResourceURI(bslFile.getFullPath().toString(), true).appendFragment("/0"); //$NON-NLS-1$
        ((InternalEObject)module).eSetProxyURI(uri);
        return module;
    }

    private Collection<IFile> getFiles(Collection<MdObject> mdObjects, IProgressMonitor monitor)
    {
        Collection<IFile> files = new HashSet<>();

        for (MdObject mdObject : mdObjects)
        {
            if (monitor.isCanceled())
            {
                return files;
            }
            IFile mdFile = resourceLookup.getPlatformResource(mdObject);
            if (mdFile == null)
            {
                continue;
            }
            appendFilesHierarchically(mdFile.getParent(), files, monitor);
        }
        return files;
    }
}
