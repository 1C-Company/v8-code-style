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

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
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
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(4, 5, 11, 26, 29, 30), errorLines);
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
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(4, 5, 14, 15), errorLines);
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
