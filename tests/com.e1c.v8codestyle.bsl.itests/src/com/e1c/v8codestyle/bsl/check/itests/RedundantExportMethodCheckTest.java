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
import com.e1c.v8codestyle.bsl.check.RedundantExportMethodCheck;

/**
 * Tests for {@link RedundantExportMethodCheck} check.
 *
 * @author Artem Iliukhin
 */
public class RedundantExportMethodCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "redundant-export-method";
    private static final String PROJECT_NAME = "ExcessExportCheck";
    private static final String MODULE_FILE_NAME = "/src/CommonModules/NoCallNoPublic/Module.bsl";
    private static final String MODULE_NO_CALL_PUBLIC_FILE_NAME = "/src/CommonModules/NoCallPublic/Module.bsl";
    private static final String MODULE_CALL_NO_PUBLIC_FILE_NAME = "/src/CommonModules/CallNoPublic/Module.bsl";
    private static final String CATALOG_FILE_NAME = "/src/Catalogs/Catalog/ObjectModule.bsl";
    private static final String CATALOG_FORM_FILE_NAME = "/src/Catalogs/Catalog/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_LIST_FORM_FILE_NAME = "/src/Catalogs/Catalog/Forms/ListForm/Module.bsl";
    private static final String MODULE_IS_EVENT_SUBSCRIPTION_FILE_NAME =
        "/src/CommonModules/isEventSubscription/Module.bsl";
    private static final String MODULE_IS_SCHEDULED_JOB_FILE_NAME = "/src/CommonModules/isScheduledJob/Module.bsl";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testNoCallNoPublic() throws Exception
    {
        List<Marker> markers = getMarkers(MODULE_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("1", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testNoCallPublic() throws Exception
    {
        List<Marker> markers = getMarkers(MODULE_NO_CALL_PUBLIC_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testCallNoPublic() throws Exception
    {
        List<Marker> markers = getMarkers(MODULE_CALL_NO_PUBLIC_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testLocalCall() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("2", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testNotifyCall() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FORM_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testNotifyWithRegionCall() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_LIST_FORM_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testEventSubscription() throws Exception
    {
        List<Marker> markers = getMarkers(MODULE_IS_EVENT_SUBSCRIPTION_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testScheduledJob() throws Exception
    {
        List<Marker> markers = getMarkers(MODULE_IS_SCHEDULED_JOB_FILE_NAME);
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
