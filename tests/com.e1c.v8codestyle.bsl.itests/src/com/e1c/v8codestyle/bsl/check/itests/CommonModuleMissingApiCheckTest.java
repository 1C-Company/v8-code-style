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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.CommonModuleMissingApiCheck;

/**
 * Tests for {@link CommonModuleMissingApiCheck} check
 *
 * @author Artem Iliukhin
 */
public class CommonModuleMissingApiCheckTest
    extends AbstractSingleModuleTestBase
{
    public CommonModuleMissingApiCheckTest()
    {
        super(CommonModuleMissingApiCheck.class);
    }

    @Test
    public void testProgrammingInterface() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "common-module-missing-api.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);

        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

}
