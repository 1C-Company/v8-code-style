/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
import com.e1c.v8codestyle.bsl.check.ModuleStructureRegionsCheck;

/**
 * Tests for {@link ModuleStructureRegionsCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureRegionsCheckTest
    extends AbstractSingleModuleTestBase
{

    public ModuleStructureRegionsCheckTest()
    {
        super(ModuleStructureRegionsCheck.class);
    }

    @Test
    public void testRegionIsOnTop() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure.bsl");

        assertEquals(3, getModuleMarkers().size());

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("There is no \"Internal\" region in the module", marker.getMessage());

    }
}
