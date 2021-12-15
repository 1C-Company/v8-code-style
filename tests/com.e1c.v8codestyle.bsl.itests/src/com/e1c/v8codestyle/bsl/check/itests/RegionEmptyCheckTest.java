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
import com.e1c.v8codestyle.bsl.check.RegionEmptyCheck;

/**
 * Tests for {@link RegionEmptyCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class RegionEmptyCheckTest
    extends AbstractSingleModuleTestBase
{

    public RegionEmptyCheckTest()
    {
        super(RegionEmptyCheck.class);
    }

    /**
     * Test region is empty.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRegionIsEmpty() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "region-empty.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("2", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test region after another region is empty.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRegionAfterIsEmpty() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "region-empty-after.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("10", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test region before another region is empty.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRegionBeforeIsEmpty() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "region-empty-before.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("2", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test region has empty sub region.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRegionHasEmptySubRegion() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "region-empty-sub-region.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("4", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test region is not empty.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRegionIsNotEmpty() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "region-not-empty.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }
}
