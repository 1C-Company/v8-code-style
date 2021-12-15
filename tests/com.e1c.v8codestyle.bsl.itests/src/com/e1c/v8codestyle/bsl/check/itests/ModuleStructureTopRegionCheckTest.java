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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.ModuleStructureTopRegionCheck;

/**
 * Tests for {@link ModuleStructureTopRegionCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructureTopRegionCheckTest
    extends AbstractSingleModuleTestBase
{

    public ModuleStructureTopRegionCheckTest()
    {
        super(ModuleStructureTopRegionCheck.class);
    }

    /**
     * Test that the region is on top of the module structure.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRegionIsOnTop() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test that top module structure region is sub region.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSubRegion() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-sub-region.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        Marker marker = markers.get(0);
        assertEquals("14", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        // FIXME remove after marker duplication fix
        marker = markers.get(1);
        assertEquals("14", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

    }

    /**
     * Test that top module structure region is sub region and goes after method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSubRegionAfterMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-sub-region-after-method.bsl");

        List<Marker> markers = getModuleMarkers();
        // FIXME has to be an error in line 18
        assertTrue(markers.isEmpty());

    }
}
