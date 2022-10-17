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
import com.e1c.v8codestyle.bsl.check.ExportVariableInObjectModuleCheck;

/**
 * Tests for {@link ExportVariableInObjectModuleCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ExportVariableInObjectModuleCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String PROJECT_NAME = "ExportVariableInObjectModuleCheck";
    private static final String CATALOG_FILE_NAME = "/src/Catalogs/Catalog/ObjectModule.bsl";
    private static final Object CHECK_ID = "export-variable-object-module";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testExportVariable() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("3", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
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
