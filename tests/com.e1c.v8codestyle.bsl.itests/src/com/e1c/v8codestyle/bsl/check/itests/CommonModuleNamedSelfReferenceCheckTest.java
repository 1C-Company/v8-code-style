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

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.CommonModuleNamedSelfReferenceCheck;

/**
 * Test for the class {@link CommonModuleNamedSelfReferenceCheck}
 *
 * @author Maxim Galios
 *
 */
public class CommonModuleNamedSelfReferenceCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "CommonModuleNamedSelfReferenceCheck";

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/MyCommonModule/Module.bsl";

    private static final String CACHED_COMMON_MODULE_FILE_NAME = "/src/CommonModules/MyCommonModuleCached/Module.bsl";

    public CommonModuleNamedSelfReferenceCheckTest()
    {
        super(CommonModuleNamedSelfReferenceCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Check self references by name in common module
     *
     * @throws Exception
     */
    @Test
    public void testCommonModule() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_FILE_NAME);
        assertEquals(2, markers.size());

        assertEquals(Integer.valueOf(6), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(Integer.valueOf(6), markers.get(1).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Check self references by name in cached common moudle
     *
     * @throws Exception
     */
    @Test
    public void testCachedCommonModule() throws Exception
    {
        List<Marker> markers = getMarkers(CACHED_COMMON_MODULE_FILE_NAME);
        assertEquals(0, markers.size());
    }

    private List<Marker> getMarkers(String moduleFileName)
    {
        String moduleId = Path.ROOT.append(getTestConfigurationName()).append(moduleFileName).toString();
        List<Marker> markers = List.of(markerManager.getMarkers(getProject().getWorkspaceProject(), moduleId));

        String chekcId = getCheckId();

        assertNotNull(chekcId);
        return markers.stream()
            .filter(marker -> chekcId.equals(getCheckIdFromMarker(marker, getProject())))
            .collect(Collectors.toList());
    }
}
