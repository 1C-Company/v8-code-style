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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.BackgroundJobExecuteCallCheck;

/**
 * The test for {@link BackgroundJobExecuteCallCheck}
 * 
 * @author Artem Samohvalov
 */
public class BackgroundJobExecuteCallCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "ExecuteExternalCodeCheckTest";

    public BackgroundJobExecuteCallCheckTest()
    {
        super(BackgroundJobExecuteCallCheck.class);
    }

    /**
     * Test error
     * Test call target method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallTargetMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "background-job-execute-call.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test no error
     * Test call another method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallAnotherMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-background-job-execute-call.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }
}
