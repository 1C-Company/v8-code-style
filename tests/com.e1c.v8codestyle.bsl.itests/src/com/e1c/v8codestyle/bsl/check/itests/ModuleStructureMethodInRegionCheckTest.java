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

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ModuleStructureMethodInRegionCheck;

/**
 * Tests for {@link ModuleStructureMethodInRegionCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureMethodInRegionCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "module-structure-method-in-regions"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "ModuleStructureRegionsCheck";

    private static final String COMMON_MODULE_NO_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleNoRegion/Module.bsl";
    private static final String COMMON_MODULE_AFTER_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleAfterRegion/Module.bsl";
    private static final String CATALOG_MODULE_MANAGER_EVENT_OUT_REGION_FILE_NAME =
        "/src/Catalogs/CatalogOutOfRegion/ManagerModule.bsl";

    private static final String COMMON_MODULE_IN_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleInRegion/Module.bsl";
    private static final String COMMON_MODULE_EXPORT_IN_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleExportInRegion/Module.bsl";
    private static final String COMMON_MODULE_EXPORT_IN_NON_INTERFACE_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleInNonInterfaceRegion/Module.bsl";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testNoRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_NO_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("1", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testMethodAfterRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_AFTER_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("5", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testEventModuleManagerOutOfRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_MODULE_MANAGER_EVENT_OUT_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("10", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testExportMethodInNonInterfaceRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_EXPORT_IN_NON_INTERFACE_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("3", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testMethodInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_IN_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("3", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testExportMethodInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_EXPORT_IN_REGION_FILE_NAME);
        assertEquals(0, markers.size());
    }

    private List<Marker> getMarkers(String moduleFileName)
    {
        String moduleId = Path.ROOT.append(getTestConfigurationName()).append(moduleFileName).toString();
        List<Marker> markers = List.of(markerManager.getMarkers(getProject().getWorkspaceProject(), moduleId));

        return markers.stream()
            .filter(marker -> CHECK_ID.equals(getCheckIdFromMarker(marker, getProject())))
            .collect(Collectors.toList());
    }

}
