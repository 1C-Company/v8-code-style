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
import org.junit.Ignore;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.ServerExecutionSafeModeCheck;

/**
 * The test for class {@link ServerExecutionSafeModeCheck}.
 *
 * @author Maxim Galios
 *
 */
public class ServerExecutionSafeModeCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "ServerExecutionSafeModeCheck";

    private static final String COMMON_MODULE_SERVER_CALL_FILE_NAME =
        "/src/CommonModules/CommonModuleServerCall/Module.bsl";

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

    private static final String FORM_MODULE_FILE_NAME = "/src/CommonForms/Form/Module.bsl";

    public ServerExecutionSafeModeCheckTest()
    {
        super(ServerExecutionSafeModeCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test that warning is not shown without Server in Environment
     *
     * @throws Exception
     */
    @Test
    public void testCommonModule() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_FILE_NAME);
        assertEquals(0, markers.size());
    }

    /**
     * Test safe mode is enabled before Execute/Выполнить and Eval/Вычислить in common module
     *
     * @throws Exception
     */
    @Test
    @Ignore("https://github.com/1C-Company/v8-code-style/issues/1377")
    public void testCommonModuleServerCall() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_SERVER_CALL_FILE_NAME);
        assertEquals(6, markers.size());

        assertEquals("4", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("5", markers.get(1).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("11", markers.get(2).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("26", markers.get(3).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("29", markers.get(4).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("30", markers.get(5).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test safe mode is enabled before Execute/Выполнить and Eval/Вычислить in form module
     *
     * @throws Exception
     */
    @Test
    public void testFormModule() throws Exception
    {
        List<Marker> markers = getMarkers(FORM_MODULE_FILE_NAME);
        assertEquals(4, markers.size());

        assertEquals("4", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("5", markers.get(1).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("14", markers.get(2).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("15", markers.get(3).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    private List<Marker> getMarkers(String moduleFileName)
    {
        String moduleId = Path.ROOT.append(getTestConfigurationName()).append(moduleFileName).toString();
        List<Marker> markers = List.of(markerManager.getMarkers(getProject().getWorkspaceProject(), moduleId));

        String checkId = getCheckId();

        assertNotNull(checkId);
        return markers.stream()
            .filter(marker -> checkId.equals(getCheckIdFromMarker(marker, getProject())))
            .collect(Collectors.toList());
    }
}
