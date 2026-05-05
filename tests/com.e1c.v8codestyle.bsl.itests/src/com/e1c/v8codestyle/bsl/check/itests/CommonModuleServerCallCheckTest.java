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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.CommonModuleServerCallCheck;

/**
 * Tests for {@link CommonModuleServerCallCheck} check.
 *
 * @author Ivan Sergeev
 */
public class CommonModuleServerCallCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String PROJECT_NAME = "CommonModueServerCallCheckTest";

    private static final String METHOD_NOT_USED = "CommonModule.CommonServerCallTestIncorrect";

    private static final String METHOD_USED = "CommonModule.CommonServerCallTestCorrect";


    private static final String CHECK_ID = "common-module-server-call";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testServerCallFlagIncorrect() throws Exception
    {
        IBmObject object = getTopObjectByFqn(METHOD_NOT_USED, getProject());
        assertNotNull(object);
        Marker marker = getFirstMarker(CHECK_ID, object.bmGetId(), getProject());
        assertNotNull(marker);
    }

    @Test
    public void testServerCallFlagCorrect() throws Exception
    {
        IBmObject object = getTopObjectByFqn(METHOD_USED, getProject());
        assertNotNull(object);
        Marker marker = getFirstMarker(CHECK_ID, object.bmGetId(), getProject());
        assertNull(marker);
    }
}
