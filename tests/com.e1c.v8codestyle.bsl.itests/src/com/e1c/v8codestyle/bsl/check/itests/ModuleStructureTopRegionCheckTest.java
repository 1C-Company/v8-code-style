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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.ModuleStructureTopRegionCheck;

/**
 * Tests for {@link ModuleStructureTopRegionCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructureTopRegionCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String CATALOG_FORM_MODULE_FILE_NAME = "/src/Catalogs/Catalog/Forms/ItemForm/Module.bsl";
    private static final String CHECK_ID = "module-structure-top-region"; //$NON-NLS-1$

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
        assertEquals(1, markers.size());
        assertEquals(Integer.valueOf(12), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
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
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(18), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test that top module structure regions is compliant.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTopRegionCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-top-region-compliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test that top module structure regions is non compliant.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTopRegionNonCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-top-region-noncompliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test that top module structure region is on top.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTopRegionNotOnTop() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-region-is-not-on-top.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(10), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test that top module structure region is duplicated.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTopRegionIsDuplicated() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-region-is-duplicated.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        assertEquals(Messages.ModuleStructureTopRegionCheck_Region_has_duplicate, markers.get(0).getMessage());
        assertEquals(Messages.ModuleStructureTopRegionCheck_Region_has_duplicate, markers.get(1).getMessage());
    }

    /**
     * Test that top module structure region is wrong order.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTopRegionIsWrongOrder() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-region-is-wrong-order.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        assertEquals(Messages.ModuleStructureTopRegionCheck_Region_has_the_wrong_order, markers.get(0).getMessage());
        assertEquals(Messages.ModuleStructureTopRegionCheck_Region_has_the_wrong_order, markers.get(1).getMessage());
    }

    /**
     * Test that top module structure regions in form module is compliant.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTopRegionFormModuleCompliant() throws Exception
    {
        Marker marker = getFirstMarker(CHECK_ID, CATALOG_FORM_MODULE_FILE_NAME, getProject());
        assertNull(marker);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return "ModuleStructureTopRegionCheck";
    }
}
