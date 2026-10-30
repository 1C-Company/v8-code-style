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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.BackgroundJobExecuteCallCheck;

/**
 * The test for {@link BackgroundJobExecuteCallCheck}
 * Without SSL
 * 
 * @author Artem Samohvalov
 */
public class BackgroundJobExecuteCallCheckWithoutSSLTest
    extends AbstractSingleModuleTestBase
{
    public BackgroundJobExecuteCallCheckWithoutSSLTest()
    {
        super(BackgroundJobExecuteCallCheck.class);
    }

    /**
     * Test no error
     * Test call target method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallTargetMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "background-job-execute-call.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
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
}
