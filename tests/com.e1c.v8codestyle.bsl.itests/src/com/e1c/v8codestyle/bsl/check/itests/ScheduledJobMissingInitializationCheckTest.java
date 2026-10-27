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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.ScheduledJobMissingInitializationCheck;

/**
 * The test for check {@link ScheduledJobMissingInitializationCheck}
 * 
 * @author Artem Samohvalov
 */
public class ScheduledJobMissingInitializationCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "ScheduledJobMissingInitializationCheck";

    public ScheduledJobMissingInitializationCheckTest()
    {
        super(ScheduledJobMissingInitializationCheck.class);
    }

    /**
     * Test error.
     *
     * @throws Exception the exception
     */
    @Test
    public void testError() throws Exception
    {
        long id = getTopObjectIdByFqn("ScheduledJob.ScheduledJobEmptyMethod", getProject());
        Marker marker = getFirstMarker(getCheckId(), id, getProject());
        assertNotNull(marker);
    }

    /**
     * Test error.
     * the init call is not the first statement
     *
     * @throws Exception the exception
     */
    @Test
    public void testErrorInitNoFirst() throws Exception
    {
        long id = getTopObjectIdByFqn("ScheduledJob.ScheduledJobSecondPosInitCallMethod", getProject());
        Marker marker = getFirstMarker(getCheckId(), id, getProject());
        assertNotNull(marker);
    }

    /**
     * Test no error.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoError() throws Exception
    {
        long id = getTopObjectIdByFqn("ScheduledJob.ScheduledJobInitCallMethod", getProject());
        Marker marker = getFirstMarker(getCheckId(), id, getProject());
        assertNull(marker);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }
}
