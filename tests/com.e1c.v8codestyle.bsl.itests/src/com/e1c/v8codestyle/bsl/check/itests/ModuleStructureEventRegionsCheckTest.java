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

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ModuleStructureEventRegionsCheck;

/**
 * Tests for {@link ModuleStructureEventRegionsCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureEventRegionsCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "module-structure-event-regions"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "StructureModule";

    private static final String CATALOG_MODULE_MANAGER_EVENT_FILE_NAME =
        "/src/Catalogs/CatalogInRegion/ManagerModule.bsl";
    private static final String CATALOG_MODULE_MANAGER_EVENT_WRONG_REGION_FILE_NAME =
        "/src/Catalogs/CatalogInWrongRegion/ManagerModule.bsl";
    private static final String CATALOG_MODULE_MANAGER_EVENT_WRONG_METHOD_FILE_NAME =
        "/src/Catalogs/CatalogInRegionWrongMethod/ManagerModule.bsl";
    private static final String COMMON_MODULE_EVENT_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testEventModuleManagerInWrongRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_MODULE_MANAGER_EVENT_WRONG_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(16), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testEventModuleManagerInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_MODULE_MANAGER_EVENT_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testEventModuleManagerInRegionWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_MODULE_MANAGER_EVENT_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(8), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testEventCommonModuleInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_EVENT_FILE_NAME);
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
