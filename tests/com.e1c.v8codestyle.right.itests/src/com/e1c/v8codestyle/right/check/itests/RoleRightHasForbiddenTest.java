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

import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.rights.model.ObjectRight;
import com._1c.g5.v8.dt.rights.model.ObjectRights;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.right.check.RightDelete;
import com.e1c.v8codestyle.right.check.RightInteractiveClearDeletionMarkPredefinedData;
import com.e1c.v8codestyle.right.check.RightInteractiveDelete;
import com.e1c.v8codestyle.right.check.RightInteractiveDeleteMarkedPredefinedData;
import com.e1c.v8codestyle.right.check.RightInteractiveDeletePredefinedData;
import com.e1c.v8codestyle.right.check.RightInteractiveSetDeletionMarkPredefinedData;

/**
 * Tests for all forbidden rights checks:
 *  {@link RightDelete},
 *  {@link RightInteractiveDelete},
 *  {@link RightInteractiveDeleteMarkedPredefinedData},
 *  {@link RightInteractiveDeletePredefinedData},
 *  {@link RightInteractiveClearDeletionMarkPredefinedData},
 *  {@link RightInteractiveSetDeletionMarkPredefinedData}.
 *
 * @author Dmitriy Marmyshev
 */
public class RoleRightHasForbiddenTest
    extends CheckTestBase
{

    private static final String CHECK_ID_1 = "right-interactive-delete";

    private static final String CHECK_ID_2 = "right-interactive-delete-marked-predefined-data";

    private static final String CHECK_ID_3 = "right-interactive-delete-predefined-data";

    private static final String CHECK_ID_4 = "right-interactive-clear-deletion-mark-predefined-data";

    private static final String CHECK_ID_5 = "right-interactive-set-deletion-mark-predefined-data";

    private static final String CHECK_ID_6 = "right-delete";

    private static final String PROJECT_NAME = "RoleRightHasForbidden";

    private static final String FQN_FORBIDDEN_RIGHTS = "Role.ForbiddenRights.Rights";

    private static final String FQN_FORBIDDEN_RIGHTS2 = "Role.FullAccess.Rights";

    private static final String FQN_ALLOWED_RIGHTS = "Role.AllowedRights.Rights";

    private IDtProject dtProject;

    @Override
    protected boolean enableCleanUp()
    {
        return false;
    }

    @Before
    public void initProject() throws Exception
    {
        IProject project = testingWorkspace.getProject(PROJECT_NAME);
        if (project.isAccessible())
        {
            testingWorkspace.waitForBuildCompletion();
            dtProject = dtProjectManager.getDtProject(project);
            assertNotNull(dtProject);
            waitForDD(dtProject);
        }
        else
        {
            dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        }
        assertNotNull(dtProject);
    }

    /**
     * Test role has forbidden right, check {@link RightInteractiveDelete}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasRightInteractiveDelete() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_FORBIDDEN_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_DELETE);
        assertNotNull(right);

        Marker marker = getFirstMarker(CHECK_ID_1, right, dtProject);
        assertNotNull(marker);

    }

    /**
     * Test role has forbidden right in FullAccess role, check {@link RightInteractiveDelete}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasRightInteractiveDelete2() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_FORBIDDEN_RIGHTS2, dtProject);
        assertTrue(top instanceof RoleDescription);

        Marker marker = getFirstMarker(CHECK_ID_1, top, dtProject);
        assertNotNull(marker);

    }

    /**
     * Test role has forbidden right, check {@link RightInteractiveDeleteMarkedPredefinedData}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasRightInteractiveDeleteMarkedPredefinedData() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_FORBIDDEN_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_DELETE_MARKED_PREDEFINED_DATA);
        assertNotNull(right);

        Marker marker = getFirstMarker(CHECK_ID_2, right, dtProject);
        assertNotNull(marker);

    }

    /**
     * Test role has forbidden right, check {@link RightInteractiveDeletePredefinedData}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasRightInteractiveDeletePredefinedData() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_FORBIDDEN_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_DELETE_PREDEFINED_DATA);
        assertNotNull(right);

        Marker marker = getFirstMarker(CHECK_ID_3, right, dtProject);
        assertNotNull(marker);

    }

    /**
     * Test role has forbidden right, check {@link RightInteractiveClearDeletionMarkPredefinedData}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasRightInteractiveClearDeletionMarkPredefinedData() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_FORBIDDEN_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_CLEAR_DELETION_MARK_PREDEFINED_DATA);
        assertNotNull(right);

        Marker marker = getFirstMarker(CHECK_ID_4, right, dtProject);
        assertNotNull(marker);

    }

    /**
     * Test role has forbidden right, check {@link RightInteractiveSetDeletionMarkPredefinedData}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasRightInteractiveSetDeletionMarkPredefinedData() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_FORBIDDEN_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_SET_DELETION_MARK_PREDEFINED_DATA);
        assertNotNull(right);

        Marker marker = getFirstMarker(CHECK_ID_5, right, dtProject);
        assertNotNull(marker);

    }

    /**
     * Test role has forbidden right, check {@link RightDelete}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasRightDelete() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_FORBIDDEN_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.DELETE);
        assertNotNull(right);

        Marker marker = getFirstMarker(CHECK_ID_6, right, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test role has no forbidden right, check {@link RightInteractiveDelete}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasNoRightInteractiveDelete() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_ALLOWED_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_DELETE);
        assertNull(right);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        Marker marker = getAnyFirstMarker(CHECK_ID_1, markers);
        assertNull(marker);

    }

    /**
     * Test role has no forbidden right, check {@link RightInteractiveDeleteMarkedPredefinedData}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasNoRightInteractiveDeleteMarkedPredefinedData() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_ALLOWED_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_DELETE_MARKED_PREDEFINED_DATA);
        assertNull(right);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        Marker marker = getAnyFirstMarker(CHECK_ID_2, markers);
        assertNull(marker);

    }

    /**
     * Test role has no forbidden right, check {@link RightInteractiveDeletePredefinedData}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasNoRightInteractiveDeletePredefinedData() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_ALLOWED_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_DELETE_PREDEFINED_DATA);
        assertNull(right);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        Marker marker = getAnyFirstMarker(CHECK_ID_3, markers);
        assertNull(marker);

    }

    /**
     * Test role has no forbidden right, check {@link RightInteractiveClearDeletionMarkPredefinedData}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasNoRightInteractiveClearDeletionMarkPredefinedData() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_ALLOWED_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_CLEAR_DELETION_MARK_PREDEFINED_DATA);
        assertNull(right);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        Marker marker = getAnyFirstMarker(CHECK_ID_4, markers);
        assertNull(marker);

    }

    /**
     * Test role has no forbidden right, check {@link RightInteractiveSetDeletionMarkPredefinedData}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasNoRightInteractiveSetDeletionMarkPredefinedData() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_ALLOWED_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.INTERACTIVE_SET_DELETION_MARK_PREDEFINED_DATA);
        assertNull(right);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        Marker marker = getAnyFirstMarker(CHECK_ID_5, markers);
        assertNull(marker);

    }

    /**
     * Test role has no forbidden right, check {@link RightDelete}
     *
     * @throws Exception the exception
     */
    @Test
    public void testRoleHasNoRightDelete() throws Exception
    {
        IBmObject top = getTopObjectByFqn(FQN_ALLOWED_RIGHTS, dtProject);
        assertTrue(top instanceof RoleDescription);
        RoleDescription description = (RoleDescription)top;
        assertEquals(1, description.getRights().size());
        ObjectRights rights = description.getRights().get(0);
        ObjectRight right = getObjectRight(rights, RightName.DELETE);
        assertNotNull(right);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        Marker marker = getAnyFirstMarker(CHECK_ID_6, markers);
        assertNull(marker);

    }

    private ObjectRight getObjectRight(ObjectRights rights, RightName name)
    {
        for (ObjectRight right : rights.getRights())
        {
            if (name.getName().equals(right.getRight().getName()))
            {
                return right;
            }

        }
        return null;
    }

    private Marker getAnyFirstMarker(String chekcId, Marker[] markers)
    {
        for (int i = 0; i < markers.length; i++)
        {
            Marker marker = markers[i];
            CheckUid checkUid = checkRepository.getUidForShortUid(marker.getCheckId(), dtProject.getWorkspaceProject());
            if (chekcId.equals(checkUid.getCheckId()))
            {
                return marker;
            }
        }
        return null;
    }

}
