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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.CachedPublicMethodCheck;

/**
 * Tests for {@link CachedPublicMethodCheck} check
 *
 * @author Artem Iliukhin
 */
public class CachedPublicMethodCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String PROJECT_NAME = "CachedPublicCheck";
    private static final String CACHED_FILE_NAME = "/src/CommonModules/CommonModuleCached/Module.bsl";
    private static final String COMPLIANT_CACHED_FILE_NAME =
        "/src/CommonModules/CommonModuleCachedCompliant/Module.bsl";
    private static final String NON_CACHED_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";
    private static final String CHECK_ID = "public-method-caching";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testCachedMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CACHED_FILE_NAME);
        assertEquals(1, markers.size());
        assertEquals("4", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testCachedMethodCompliant() throws Exception
    {
        List<Marker> markers = getMarkers(COMPLIANT_CACHED_FILE_NAME);
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testNonCachedMethod() throws Exception
    {
        List<Marker> markers = getMarkers(NON_CACHED_FILE_NAME);
        assertTrue(markers.isEmpty());
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
