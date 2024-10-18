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
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.AccessibilityAtClientInObjectModuleCheck;

/**
 * Tests for {@link AccessibilityAtClientInObjectModuleCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class AccessibilityAtClientInObjectModuleCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "AccessibilityAtClient";

    private static final String MANAGER_MODULE_FILE_NAME = "/src/Catalogs/Products/ManagerModule.bsl";

    private static final String OBJECT_MODULE_FILE_NAME = "/src/Catalogs/Products/ObjectModule.bsl";

    public AccessibilityAtClientInObjectModuleCheckTest()
    {
        super(AccessibilityAtClientInObjectModuleCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleId()
    {
        return Path.ROOT.append(getTestConfigurationName()).append(MANAGER_MODULE_FILE_NAME).toString();
    }

    @Test
    public void testObjectModule() throws Exception
    {
        List<Marker> markers = getObjectModuleMarkers();
        assertEquals(3, markers.size());
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(2, 5, 10), errorLines);
    }

    @Test
    public void testManagerModule() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(2, 27), errorLines);
    }

    private List<Marker> getObjectModuleMarkers()
    {
        String moduleId = Path.ROOT.append(getTestConfigurationName()).append(OBJECT_MODULE_FILE_NAME).toString();
        List<Marker> markers = List.of(markerManager.getMarkers(getProject().getWorkspaceProject(), moduleId));

        String chekcId = getCheckId();
        assertNotNull(chekcId);

        return markers.stream()
            .filter(m -> chekcId.equals(getCheckIdFromMarker(m, getProject())))
            .collect(Collectors.toList());
    }

}
