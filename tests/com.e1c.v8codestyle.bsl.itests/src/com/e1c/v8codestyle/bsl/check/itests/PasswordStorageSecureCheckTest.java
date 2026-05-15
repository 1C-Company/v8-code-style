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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.PasswordStorageSecureCheck;

/**
 * Tests for {@link PasswordStorageSecureCheck} check.
 *
 * @author Ivan Sergeev
 */
public class PasswordStorageSecureCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String PROJECT_NAME = "PasswordSecureStorageCheckTest";

    private static final String SECURE_STORAGE_NOT_USED = "CommonForm.Form.Form";

    private static final String SECURE_STORAGE_USED = "CommonForm.FormUse.Form";


    private static final String CHECK_ID = "secure-password-storage";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testNotUseSecurityStorage() throws Exception
    {
        IBmObject object = getTopObjectByFqn(SECURE_STORAGE_NOT_USED, getProject());
        assertNotNull(object);
        Marker marker = getFirstMarker(CHECK_ID, object.bmGetId(), getProject());
        assertNotNull(marker);
    }

    @Test
    public void testUseSecurityStorage() throws Exception
    {
        IBmObject object = getTopObjectByFqn(SECURE_STORAGE_USED, getProject());
        assertNotNull(object);
        Marker marker = getFirstMarker(CHECK_ID, object.bmGetId(), getProject());
        assertNull(marker);
    }
}
