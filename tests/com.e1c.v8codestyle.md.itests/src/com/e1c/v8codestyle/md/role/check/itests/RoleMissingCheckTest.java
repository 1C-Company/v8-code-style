/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.md.role.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.role.check.RoleMissingCheck;

/**
 * The test for class {@link RoleMissingCheck}.
 *
 * @author Aleksey Kalugin
 *
 */
public class RoleMissingCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "role-missing"; //$NON-NLS-1$

    private static final String PROJECT_NAME_ROLES_MISSING = "RoleMissingCheck";
    private static final String PROJECT_NAME_ROLES_EXIST = "RoleMissingCheck_RolesExist";

    /**
     * Test configuration doesn't have required roles.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRolesMissing() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME_ROLES_MISSING);
        assertNotNull(dtProject);

        var project = dtProject.getWorkspaceProject();
        assertNotNull(project);

        Long id = getTopObjectIdByFqn("Configuration", dtProject);

        var markerCount = Arrays.stream(markerManager.getMarkers(project, id))
            .filter(marker -> id.equals(marker.getSourceObjectId())
                && CHECK_ID.equals(getCheckIdFromMarker(marker, dtProject)))
            .count();

        assertEquals(3, markerCount);
    }

    /**
     * Test configuration has required roles.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRolesExist() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME_ROLES_EXIST);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Configuration", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }
}
