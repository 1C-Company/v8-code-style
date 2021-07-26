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
 *     Aleksandr Kapralov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.right.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.rights.model.ObjectRight;
import com._1c.g5.v8.dt.rights.model.ObjectRights;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.right.check.StartAutomationRight;

/**
 * Tests for {@link StartAutomationRight} check.
 *
 * @author Aleksandr Kapralov
 */
public class StartAutomationRightTest
    extends CheckTestRights
{

    private static final String PROJECT_NAME = "StandartRoles";

    private static final String CONFIGURATION_FQN = "Configuration";

    private static final String CHECK_ID = "start-automation-right"; //$NON-NLS-1$

    private static final RightName[] STANDART_ROLES = new RightName[] { RightName.AUTOMATION };

    @Test
    public void testStandartRoleCorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        updateRole(dtProject, "Role.StandartRole.Rights", CONFIGURATION_FQN, STANDART_ROLES, "StartAutomation");

        IBmObject top = getTopObjectByFqn("Role.StartAutomation.Rights", dtProject);
        assertTrue(top instanceof RoleDescription);

        RoleDescription description = (RoleDescription)top;
        EObject configuration = getTopObjectByFqn(CONFIGURATION_FQN, dtProject);
        ObjectRights objectRights = RightsModelUtil.getOrCreateObjectRights(configuration, description);

        for (ObjectRight objectRight : objectRights.getRights())
        {
            Marker marker = getFirstMarker(CHECK_ID, objectRight, dtProject);
            assertNull(marker);
        }
    }

    @Test
    public void testCustomRoleIncorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String roleFqn = "Role.CustomRole.Rights";

        updateRole(dtProject, roleFqn, CONFIGURATION_FQN, STANDART_ROLES, null);

        IBmObject top = getTopObjectByFqn(roleFqn, dtProject);
        assertTrue(top instanceof RoleDescription);

        RoleDescription description = (RoleDescription)top;
        EObject configuration = getTopObjectByFqn(CONFIGURATION_FQN, dtProject);
        ObjectRights objectRights = RightsModelUtil.getOrCreateObjectRights(configuration, description);

        for (ObjectRight objectRight : objectRights.getRights())
        {
            Marker marker = getFirstMarker(CHECK_ID, objectRight, dtProject);
            assertNotNull(marker);
        }
    }

}
