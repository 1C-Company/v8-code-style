/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.FormDataByValueToServerCheck;

/**
 * The test for {@link FormDataByValueToServerCheck} check.
 *
 * @author Ivan Sergeev
 */
public class FormDataByValueToServerCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "FormDataByValueToServer";

    private static final String FQN = "FormDataByValueToServer.Form.Form";

    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/Form/Module.bsl";

    /**
     * Instantiates a new form data by value to server check test.
     */
    public FormDataByValueToServerCheckTest()
    {
        super(FormDataByValueToServerCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return COMMON_FORM_FILE_NAME;
    }

    /**
     * Form data is passed by value to the non-context server call.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormDataByValueToServer() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-data-by-value-to-server.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals(Integer.valueOf(8), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Form data is not passed by value to the non-context server call.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormDataNotByValueToServer() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-data-by-value-to-server-compliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Form data is passed to the context server call, so it is not reported.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormDataToContextServerCall() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-data-by-value-to-server-context.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }
}
