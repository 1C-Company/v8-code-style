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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.Before;
import org.junit.Ignore;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.lifecycle.ProjectStopType;
import com._1c.g5.v8.dt.core.lifecycle.ServiceLifecycleJob;
import com._1c.g5.v8.dt.core.lifecycle.WorkspaceProjectStopRequest;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.testing.TestingProjectLifecycleSupport;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.ICheck;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Abstract test base which loads a project and updates its single module from a {@code test.bsl} resource if necessary.
 * Default project is {@code CommonModule} that allows to load rest resource to common module.
 *<br><br>
 * Clients may override the name of the project and path to the module, module id, and the way how to get the module
 * (for example manager module, object module, form module and etc).
 *
 * @author Dmitriy Marmyshev
 */
@Ignore
public class AbstractSingleModuleTestBase
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "CommonModule";

    private static final String FQN = "CommonModule.CommonModule";

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

    protected static final String FOLDER_RESOURCE = "/resources/";

    private IDtProject dtProject;

    private Class<? extends ICheck> checkClass;

    private ICheck check;

    /**
     * Instantiates a new abstract single module test by the class of the check.
     *
     * @param checkClass the check class, cannot be {@code null}.
     */
    protected AbstractSingleModuleTestBase(Class<? extends ICheck> checkClass)
    {
        super();
        this.checkClass = checkClass;
    }

    @Override
    protected boolean enableCleanUp()
    {
        return false;
    }

    /**
     * Gets the DT project instance.
     *
     * @return the project, cannot return {@code null}.
     */
    protected IDtProject getProject()
    {
        return dtProject;
    }

    @Before
    public void setUp() throws CoreException
    {

        IProject project = testingWorkspace.getProject(getTestConfigurationName());

        if (!project.exists() || !project.isAccessible())
        {
            testingWorkspace.cleanUpWorkspace();
            dtProject = openProjectAndWaitForValidationFinish(getTestConfigurationName());
        }
        dtProject = dtProjectManager.getDtProject(project);
        setCheckEnable(true);
    }

    /**
     * Sets the check enable or disable.
     *
     * @param enable the new check enable
     */
    protected void setCheckEnable(boolean enable)
    {
        IProject project = getProject().getWorkspaceProject();
        CheckUid cuid = new CheckUid(getCheckId(), BslPlugin.PLUGIN_ID);
        ICheckSettings settings = checkRepository.getSettings(cuid, project);
        if (settings.isEnabled() != enable)
        {
            settings.setEnabled(enable);
            checkRepository.applyChanges(Collections.singleton(settings), project);
            waitForDD(getProject());
        }
    }

    /**
     * Gets the test configuration name, the project name.
     *
     * @return the test configuration name, cannot return {@code null}.
     */
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Gets the module instance from the project.
     * <br><br>
     * Clients should override this method if they need to get module form different path
     * then {@code /ProjectName/src/CommonModules/CommonModule/Module.bsl}
     *
     * @return the module, cannot return {@code null}.
     */
    protected Module getModule()
    {
        IBmObject mdObject = getTopObjectByFqn(FQN, getProject());
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        return module;
    }

    /**
     * Update project module file from bundle resource path, wait for validation and get the module.
     *
     * @param pathToResource the full path to resource in this bundle, cannot be {@code null}.
     * @return the module after validation, cannot return {@code null}.
     * @throws Exception the exception
     */
    protected Module updateAndGetModule(String pathToResource) throws Exception
    {
        updateModule(pathToResource);

        return getModule();
    }

    /**
     * Update project module file form bundle resource path and wait until validation finished.
     *
     * @param pathToResource the path to resource
     * @throws Exception the exception
     */
    protected void updateModule(String pathToResource) throws Exception
    {
        IProject project = getProject().getWorkspaceProject();
        IFile file = project.getFile(getModuleFileName());
        try (InputStream in = getClass().getResourceAsStream(pathToResource))
        {
            if (file.exists())
            {
                file.setContents(in, true, true, new NullProgressMonitor());
            }
            else
            {
                file.create(in, true, new NullProgressMonitor());
            }
        }
        testingWorkspace.buildWorkspace();
        // As well as AUTO_BUILD-family job is being scheduled synchronously
        // So all we need is to wait for auto-build job is being finished
        // And also a little protection from direct file changes (without Eclipse
        // resource subsystem)
        project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        try
        {
            Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
            Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, null);
        }
        catch (OperationCanceledException | InterruptedException e)
        {
            throw new IllegalStateException("Cannot update file:" + file.toString(), e); //$NON-NLS-1$
        }
        waitForDD(getProject());
    }

    /**
     * Gets the module first marker for this check.
     *
     * @return the module first marker, may return {@code null} if there is no markers with the check ID.
     */
    protected Marker getModuleFirstMarker()
    {
        return getFirstMarker(getCheckId(), getModuleId(), getProject());
    }

    /**
     * Gets the module project relative file path.
     *
     * @return the module file name, cannot return {@code null}.
     */
    protected String getModuleFileName()
    {
        return COMMON_MODULE_FILE_NAME;
    }

    /**
     * Gets the module id it is full project resource path.
     *
     * @return the module id, cannot return {@code null}.
     */
    protected String getModuleId()
    {
        return Path.ROOT.append(getTestConfigurationName()).append(getModuleFileName()).toString();
    }

    /**
     * Gets the module markers for this check.
     *
     * @return the module markers, cannot return {@code null}.
     */
    protected List<Marker> getModuleMarkers()
    {
        List<Marker> markers = List.of(markerManager.getMarkers(getProject().getWorkspaceProject(), getModuleId()));

        String chekcId = getCheckId();
        assertNotNull(chekcId);

        CheckUid id = new CheckUid(chekcId, BslPlugin.PLUGIN_ID);

        return markers.stream()
            .filter(
                m -> id.equals(checkRepository.getUidForShortUid(m.getCheckId(), getProject().getWorkspaceProject())))
            .collect(Collectors.toList());
    }

    /**
     * Gets the check id gets from the check instance.
     *
     * @return the check id, cannot return {@code null}.
     */
    protected String getCheckId()
    {
        return getCheckInstance().getCheckId();
    }

    /**
     * Gets the check instance created in BSL bundle.
     *
     * @return the check instance, cannot return {@code null}.
     */
    protected ICheck getCheckInstance()
    {
        if (check == null)
        {
            check = BslPlugin.getDefault().getInjector().getInstance(checkClass);
        }
        return check;
    }

    /**
     * Delete project in the workspace.
     * @param project deleted project, cannot be <code>null</code>
     * @throws CoreException if workspace clean up error occurred
     */
    protected void cleanUpProject(IProject project) throws CoreException
    {
        // First of all we need to stop all DT projects to perform the most effective
        // cleanup, without stray dependent projects restarts and so on
        Collection<WorkspaceProjectStopRequest> stopRequests = new ArrayList<>();

        IDtProject dtProject = dtProjectManager.getDtProject(project);
        if (dtProject != null)
        {
            stopRequests.add(new WorkspaceProjectStopRequest(project, ProjectStopType.DELETE));
        }

        // Stopping projects in the right order
        orchestrator.stopWorkspaceProjects(stopRequests, new NullProgressMonitor());

        // Delete projects physically

        try
        {
            project.delete(false, true, null);
            TestingProjectLifecycleSupport.waitForProjectStop(project);
        }
        catch (Throwable e)
        {
            e.printStackTrace(System.err);
            //nop
        }

        // Checking if there are critical failures during the shutdown of the project
        try
        {
            // Force closing services that failed during the normal close
            if (dtProject != null && orchestrator.isStarted(dtProject))
            {
                orchestrator.stopProject(dtProject, ProjectStopType.DELETE);
                TestingProjectLifecycleSupport.waitForProjectStop(project);
            }
        }
        catch (Throwable e)
        {
            //nop
        }
        try
        {
            testingWorkspace.getWorkspaceRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
        }
        catch (Throwable e)
        {
            e.printStackTrace(System.err);
        }

        try
        {
            Job.getJobManager().join(ServiceLifecycleJob.FAMILY, new NullProgressMonitor());
            Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
            Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, null);
        }
        catch (OperationCanceledException | InterruptedException e)
        {
            // Nothing
        }
    }
}
