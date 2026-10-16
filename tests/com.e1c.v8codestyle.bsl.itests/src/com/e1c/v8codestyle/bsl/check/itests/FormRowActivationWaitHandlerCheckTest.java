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

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.FormRowActivationWaitHandlerCheck;

/**
 * The test for {@link FormRowActivationWaitHandlerCheck} check.
 *
 * @author Ivan Sergeev
 */
public class FormRowActivationWaitHandlerCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "FormDataByValueToServer";

    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/Form/Module.bsl";

    /**
     * Instantiates a new form row activation wait handler check test.
     */
    public FormRowActivationWaitHandlerCheckTest()
    {
        super(FormRowActivationWaitHandlerCheck.class);
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
     * The row activation handler has no idle handler.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoWaitHandler() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-row-activation-wait-handler.bsl");

        assertEquals(List.of(2), getMarkerLines());
    }

    /**
     * The row activation handler attaches a not one-time idle handler.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotOneTimeWaitHandler() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-row-activation-wait-handler-not-one-time.bsl");

        assertEquals(List.of(2), getMarkerLines());
    }

    /**
     * The row activation handler attaches a one-time idle handler.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOneTimeWaitHandler() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-row-activation-wait-handler-compliant.bsl");

        assertEquals(List.of(), getMarkerLines());
    }

    /**
     * The row activation handler starts the command update.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCommandUpdate() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-row-activation-wait-handler-command-update.bsl");

        assertEquals(List.of(), getMarkerLines());
    }

    /**
     * The row activation handler calls the context server method directly.
     *
     * @throws Exception the exception
     */
    @Test
    public void testServerCallInWaitHandler() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-row-activation-wait-handler-server-call.bsl");

        assertEquals(List.of(4), getMarkerLines());
    }

    private List<Integer> getMarkerLines()
    {
        return getModuleMarkers().stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
    }
}
