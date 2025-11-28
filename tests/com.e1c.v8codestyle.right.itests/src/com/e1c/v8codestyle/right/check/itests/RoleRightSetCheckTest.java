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
 *     Andrey Volkov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.right.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.internal.right.itests.CheckRights;
import com.e1c.v8codestyle.right.check.RoleRightSetCheck;

/**
 * Tests for {@link RoleRightSetCheck} check.
 *
 * @author Andrey Volkov
 */
public class RoleRightSetCheckTest
    extends CheckRights
{
    @Test
    public void testMainProjectRolesCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish("RoleRightSetCheckTest");
        assertNotNull(dtProject);

        checkMarkerCount(dtProject, "Role.Роль1AllowAll.Rights", 0);
        checkMarkerCount(dtProject, "Role.Роль2DisableAll.Rights", 0);
    }

    @Test
    public void testExtensionProjectRolesCorrect() throws Exception
    {
        IDtProject dtProject =
            openProjectAndWaitForValidationFinish("RoleRightSetCheckTest.RoleRightSetCheckExtensionTest");
        assertNotNull(dtProject);

        checkMarkerCount(dtProject, "Role.Расш1_ОсновнаяРоль.Rights", 6);
        checkMarkerCount(dtProject, "Role.Расш1_Роль1AllowAll.Rights", 0);
        checkMarkerCount(dtProject, "Role.Расш1_Роль2DisableAll.Rights", 0);
        checkMarkerCount(dtProject, "Role.Роль1AllowAll.Rights", 0);
        checkMarkerCount(dtProject, "Role.Роль2DisableAll.Rights", 0);
    }

    private void checkMarkerCount(IDtProject dtProject, String roleDescriptionFqn, int markerCount) throws CoreException
    {
        IBmObject bmRoleDescription = getTopObjectByFqn(roleDescriptionFqn, dtProject);
        assertNotNull(bmRoleDescription);

        Object extractedMarkerContainerId = extractMarkerContainerId(bmRoleDescription.bmGetId());
        Marker[] markers = markerManager.getMarkers(dtProject.getWorkspaceProject(), extractedMarkerContainerId);

        assertTrue(markers.length == markerCount);
    }
}
