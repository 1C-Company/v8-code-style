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
import com.e1c.v8codestyle.bsl.check.FormContextServerCallInEventHandlerCheck;

/**
 * The test for {@link FormContextServerCallInEventHandlerCheck} check.
 *
 * @author Ivan Sergeev
 */
public class FormContextServerCallInEventHandlerCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "FormDataByValueToServer"; //$NON-NLS-1$

    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/Form/Module.bsl"; //$NON-NLS-1$

    /**
     * Instantiates a new form context server call in event handler check test.
     */
    public FormContextServerCallInEventHandlerCheckTest()
    {
        super(FormContextServerCallInEventHandlerCheck.class);
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
     * The event handler calls the context server method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testContextServerCallInEventHandler() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-context-server-call-in-event-handler.bsl"); //$NON-NLS-1$

        assertEquals(List.of(3, 8), getMarkerLines());
    }

    /**
     * The event handler calls the server method without context.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoContextServerCallInEventHandler() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-context-server-call-in-event-handler-no-context.bsl"); //$NON-NLS-1$

        assertEquals(List.of(), getMarkerLines());
    }

    /**
     * The context server method is called from a procedure that is not an event handler.
     *
     * @throws Exception the exception
     */
    @Test
    public void testContextServerCallInNotEventHandler() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-context-server-call-in-not-event-handler.bsl"); //$NON-NLS-1$

        assertEquals(List.of(), getMarkerLines());
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
