/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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

package com.e1c.v8codestyle.form.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.FormItemVisibleSettingsByRolesCheck;

/**
 * Tests for {@link FormItemVisibleSettingsByRolesCheck} check.
 * @author Vadim Goncharov
 */
public class FormItemVisibleSettingsByRolesCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "form-item-visible-settings-by-roles";

    private static final String PROJECT_NAME = "FormItemVisibleSettingsByRoles";

    /**
     * Test form item use role based settings.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormItemUseRoleBasedSettings() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);

        // Form attribute edit setting by roles
        long id = getTopObjectIdByFqn("CommonForm.TestForm1.Form", project);
        Marker marker = getFirstNestedMarker(CHECK_ID, id, project);
        assertNotNull(marker);

        // Form attribute view setting by roles
        id = getTopObjectIdByFqn("CommonForm.TestForm2.Form", project);
        marker = getFirstNestedMarker(CHECK_ID, id, project);
        assertNotNull(marker);

        // Form visible item view setting by roles
        id = getTopObjectIdByFqn("CommonForm.TestForm3.Form", project);
        marker = getFirstNestedMarker(CHECK_ID, id, project);
        assertNotNull(marker);

        // Form command use setting by roles
        id = getTopObjectIdByFqn("CommonForm.TestForm4.Form", project);
        marker = getFirstNestedMarker(CHECK_ID, id, project);
        assertNotNull(marker);
    }

    /**
     * Test form item not use role based settings.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormItemNotUseRoleBasedSettings() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);

        // Form attribute, visible item and command
        long id = getTopObjectIdByFqn("CommonForm.TestForm5.Form", project);
        Marker marker = getFirstNestedMarker(CHECK_ID, id, project);
        assertNull(marker);
    }

}
