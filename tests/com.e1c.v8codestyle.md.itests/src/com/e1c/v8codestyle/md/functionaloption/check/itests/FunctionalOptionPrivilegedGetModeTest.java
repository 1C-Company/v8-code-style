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

package com.e1c.v8codestyle.md.functionaloption.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.functionoption.check.FunctionalOptionPrivilegedGetModeCheck;

/**
 * The test class for {@link FunctionalOptionPrivilegedGetModeCheck}
 * @author Vadim Goncharov
 */
public class FunctionalOptionPrivilegedGetModeTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "functional-option-privileged-get-mode";

    private static final String PROJECT_NAME = "FunctionalOptionPrivilegedGetMode";

    /**
     * Test functional options use privileged get mode check.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFunctionalOptionsUsePrivilegedGetModeCheck() throws Exception
    {

        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);

        long id = getTopObjectIdByFqn("FunctionalOption.UseOrganisations", project);
        Marker marker = getFirstMarker(CHECK_ID, id, project);
        assertNotNull(marker);

        id = getTopObjectIdByFqn("FunctionalOption.UseWH", project);
        marker = getFirstMarker(CHECK_ID, id, project);
        assertNull(marker);
        
        id = getTopObjectIdByFqn("FunctionalOption.UseFinPlan", project);
        marker = getFirstMarker(CHECK_ID, id, project);
        assertNull(marker);
    }

}
