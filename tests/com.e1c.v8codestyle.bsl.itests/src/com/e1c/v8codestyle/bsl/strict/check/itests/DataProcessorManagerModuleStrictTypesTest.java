/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.strict.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.DataProcessor;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.bsl.strict.check.DynamicFeatureAccessMethodNotFoundCheck;

/**
 * Tests of strict types system in BSL module for Data Processor modules.
 *
 * @author Dzyuba_M
 */
public class DataProcessorManagerModuleStrictTypesTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "DataProcessorModule";

    private static final String FQN = "DataProcessor.DataProcessor1";

    private IDtProject dtProject;

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
    }

    @Override
    protected boolean enableCleanUp()
    {
        return false;
    }

    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test of {@link DynamicFeatureAccessMethodNotFoundCheck} when in diefferent environments - allowed for check by user and not allowed.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDynamicFeatureAccessMethodNotFoundCheckInDiffEnvs() throws Exception
    {
        List<Marker> markers = getMarters("dynamic-access-method-not-found", getModule());
        assertEquals(0, markers.size());
    }

    private IDtProject getProject()
    {
        return dtProject;
    }

    private List<Marker> getMarters(String checkId, Module module)
    {
        String id = module.eResource().getURI().toPlatformString(true);
        List<Marker> markers =
            new ArrayList<>(Arrays.asList(markerManager.getMarkers(getProject().getWorkspaceProject(), id)));

        markers.removeIf(m -> !checkId.equals(getCheckIdFromMarker(m, getProject())));
        return markers;
    }

    private Module getModule() throws Exception
    {
        testingWorkspace.waitForBuildCompletion();
        waitForDD(getProject());

        IBmObject mdObject = getTopObjectByFqn(FQN, getProject());
        assertTrue(mdObject instanceof DataProcessor);
        Module module = ((DataProcessor)mdObject).getManagerModule();
        assertNotNull(module);
        return module;
    }

}
