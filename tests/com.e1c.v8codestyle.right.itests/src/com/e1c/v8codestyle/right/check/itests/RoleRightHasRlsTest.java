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
package com.e1c.v8codestyle.right.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.rights.model.ObjectRight;
import com._1c.g5.v8.dt.rights.model.ObjectRights;
import com._1c.g5.v8.dt.rights.model.Rls;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.right.check.RoleRightHasRls;

/**
 * Tests for {@link RoleRightHasRls} check.
 *
 * @author Dmitriy Marmyshev
 */
public class RoleRightHasRlsTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "role-right-has-rls"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "RoleRightHasRls";

    /**
     * Test role with full-access has RLS
     *
     * @throws Exception the exception
     */
    @Test
    public void testFullAccessHasRls() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject top = getTopObjectByFqn("Role.FullAccess.Rights", dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(2, description.getRights().size());

        ObjectRights rights = description.getRights().get(1);
        assertEquals(1, rights.getRights().size());
        ObjectRight right = rights.getRights().get(0);
        assertEquals(1, right.getRestrictionsByCondition().size());
        Rls rls = right.getRestrictionsByCondition().get(0);

        Marker marker = getFirstMarker(CHECK_ID, rls, dtProject);
        assertNotNull(marker);

    }

    /**
     * Test role with full-access has no RLS
     *
     * @throws Exception the exception
     */
    @Test
    public void testFullAccessHasNoRls() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject top = getTopObjectByFqn("Role.SystemAdministrator.Rights", dtProject);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        assertNotNull(markers);
        assertEquals(0, markers.length);

    }

    /**
     * Test role none full-access has RLS
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoneFullAccessHasRls() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject top = getTopObjectByFqn("Role.OtherRls.Rights", dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());

        ObjectRights rights = description.getRights().get(0);
        assertEquals(2, rights.getRights().size());
        ObjectRight right = rights.getRights().get(0);
        assertEquals(1, right.getRestrictionsByCondition().size());
        Rls rls = right.getRestrictionsByCondition().get(0);

        Marker marker = getFirstMarker(CHECK_ID, rls, dtProject);
        assertNull(marker);

    }

}
