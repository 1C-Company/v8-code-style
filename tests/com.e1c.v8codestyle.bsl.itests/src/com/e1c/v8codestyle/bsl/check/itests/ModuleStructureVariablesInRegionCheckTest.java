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
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ModuleStructureVariablesInRegionCheck;

/**
 * Tests for {@link ModuleStructureVariablesInRegionCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureVariablesInRegionCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "module-structure-var-in-region"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "ModuleStructureVariablesInRegionCheck"; //$NON-NLS-1$

    private static final String CATALOG_FILE_NAME = "/src/Catalogs/Catalog/ObjectModule.bsl"; //$NON-NLS-1$
    private static final String CATALOG_VAR_IN_LINE_FILE_NAME = "/src/Catalogs/CatalogComplient/ObjectModule.bsl"; //$NON-NLS-1$
    private static final String CATALOG_NO_REGION_FILE_NAME = "/src/Catalogs/CatalogNoRegion/ObjectModule.bsl"; //$NON-NLS-1$
    private static final String CATALOG_OUT_OF_REGION_FILE_NAME = "/src/Catalogs/CatalogOutOfRegion/ObjectModule.bsl"; //$NON-NLS-1$
    private static final String CATALOG_WRONG_REGION_FILE_NAME = "/src/Catalogs/CatalogWrongRegion/ObjectModule.bsl"; //$NON-NLS-1$

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testVariableInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FILE_NAME);
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testVariablesInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_VAR_IN_LINE_FILE_NAME);
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testNoVariablesRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_NO_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(2), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testOutOfVariablesRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_OUT_OF_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(5), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testWrongRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_WRONG_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(6), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
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
