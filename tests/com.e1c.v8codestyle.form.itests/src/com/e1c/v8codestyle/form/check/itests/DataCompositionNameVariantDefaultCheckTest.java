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
package com.e1c.v8codestyle.form.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.DataCompositionNameVariantDefaultCheck;

/**
 * Test {@link DataCompositionNameVariantDefaultCheck} data coposition schema variant name.
 *
 * @author Ivan Sergeev
 */
public class DataCompositionNameVariantDefaultCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "data-composition-variant-name-default"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "ReportVariantName";

    private static final String FQN_DL1 = "Report.TestReport.Template.MainDataCompositionSchema.Template";

    private static final String FQN_DL2 = "Report.TestReport.Template.MainDataCompositionSchema2.Template";

    @Test
    public void testNameVariantDefault() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        IBmObject object = getTopObjectByFqn(FQN_DL1, project);
        assertNotNull(object);
        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), project);
        assertNotNull(marker);
    }
    @Test
    public void testNameVariantNonDefault() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        IBmObject object =
            getTopObjectByFqn(FQN_DL2, project);
        assertNotNull(object);
        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), project);
        assertNull(marker);
    }
}
