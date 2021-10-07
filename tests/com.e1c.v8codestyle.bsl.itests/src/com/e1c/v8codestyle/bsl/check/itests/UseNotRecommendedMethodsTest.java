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
 *     Sergey Kozynskiy - issue #100
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.bsl.check.UseNotRecommendedMethods;

/**
 * The test for class {@link UseNotRecommendedMethods}.
 *
 * @author Sergey Kozynskiy
 *
 */
public class UseNotRecommendedMethodsTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "use-not-recommended-methods"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "UseNotRecommendedMethods";

    /**
     * Test use not recommended methods.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUseNotRecommendedMethods() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn("CommonModule.TestModule", dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        String id = module.eResource().getURI().toPlatformString(true);
        Marker[] markers = markerManager.getMarkers(dtProject.getWorkspaceProject(), id);
        assertNotNull(markers);

        String checkUid = getCheckIdFromMarker(markers[0], dtProject);
        assertNotNull(checkUid);
        assertTrue(CHECK_ID.equals(checkUid));

        checkUid = getCheckIdFromMarker(markers[1], dtProject);
        assertNotNull(checkUid);
        assertTrue(CHECK_ID.equals(checkUid));
    }
}
