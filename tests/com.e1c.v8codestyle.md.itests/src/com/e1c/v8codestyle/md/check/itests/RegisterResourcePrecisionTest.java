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
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdObjectNameLength;

/**
 * Tests for {@link MdObjectNameLength} check
 *
 * @author Timur Mukhamedishin
 *
 */
public class RegisterResourcePrecisionTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "register-resource-precision"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "RegisterResourcePrecision";

    /**
     * Test that accounting register resource precision longer than maximal length.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAccountingRegisterResourcePrecision() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object =
            getTopObjectByFqn("AccountingRegister.AccountingRegisterTest", dtProject);
        assertNotNull(object);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNotNull(marker);
    }

    /**
     * Test that accumulation register resource precision longer than maximal length.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAccumulationRegisterResourcePrecision() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn("AccumulationRegister.AccumulationRegisterTest", dtProject);
        assertNotNull(object);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNotNull(marker);
    }
}
