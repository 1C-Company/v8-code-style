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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Before;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.ICheck;
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
public class AbstractSingleModuleTestBase
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "CommonModule";

    private static final String FQN = "CommonModule.CommonModule";

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

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
    public void setUp() throws CoreException {

        IProject project = testingWorkspace.getProject(PROJECT_NAME);

        if (!project.exists() || project.isAccessible())
        {
            testingWorkspace.cleanUpWorkspace();
            dtProject = openProjectAndWaitForValidationFinish(getTestConfigurationName());
        }
        dtProject = dtProjectManager.getDtProject(project);
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
        IBmObject mdObject = getTopObjectByFqn(FQN, dtProject);
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
     * @throws CoreException the core exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected Module updateAndGetModule(String pathToResource) throws CoreException, IOException
    {
        updateModule(pathToResource);

        return getModule();
    }

    /**
     * Update project module file form bundle resource path and wait until validation finished.
     *
     * @param pathToResource the path to resource
     * @throws CoreException the core exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void updateModule(String pathToResource) throws CoreException, IOException
    {
        try (InputStream in = getClass().getResourceAsStream(pathToResource))
        {
            IFile file = getProject().getWorkspaceProject().getFile(getModuleFileName());
            file.setContents(in, true, true, new NullProgressMonitor());
        }
        testingWorkspace.waitForBuildCompletion();
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

        return markers.stream()
            .filter(m -> chekcId.equals(getCheckIdFromMarker(m, getProject())))
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
}
