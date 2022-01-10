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
 *     Manaev Konstantin - issue #38
 *******************************************************************************/

package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.emf.common.util.EList;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.schedule.model.DailySchedule;
import com._1c.g5.v8.dt.schedule.model.Schedule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdScheduledJobPeriodicityCheck;

/**
 * Tests for {@link MdScheduledJobPeriodicityCheck} check.
 *
 * @author Manaev Konstantin
 *
 */
public final class MdScheduledJobPeriodicityCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "shceduled-job-periodicity-too-short"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "MdScheduledJobPeriodicity";

    /**
     * Test that md scheduled job detailed repeat period less than one minute.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdScheduledJobDetailedRepeatPeriodLessOneMinute() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object =
            getTopObjectByFqn("ScheduledJob.ScheduledJobWithDetailedRepeatPeriodLessOneMinute.Schedule", dtProject);
        if (object instanceof Schedule)
        {
            Marker marker = null;
            EList<DailySchedule> dailySchedules = ((Schedule)object).getDailySchedules();
            for (DailySchedule dailySchedule : dailySchedules)
            {
                marker = getFirstMarker(CHECK_ID, ((IBmObject)dailySchedule).bmGetId(), dtProject);
                assertNotNull(marker);
            }
        }
    }

    /**
     * Test that md scheduled job detailed repeat period more than one minute ignores.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdScheduledJobDetailedRepeatPeriodMoreOneMinute() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object =
            getTopObjectByFqn("ScheduledJob.ScheduledJobWithDetailedRepeatPeriodMoreOneMinute.Schedule", dtProject);
        if (object instanceof Schedule)
        {
            Marker marker = null;
            EList<DailySchedule> dailySchedules = ((Schedule)object).getDailySchedules();
            for (DailySchedule dailySchedule : dailySchedules)
            {
                marker = getFirstMarker(CHECK_ID, ((IBmObject)dailySchedule).bmGetId(), dtProject);
                assertNull(marker);
            }
        }
    }

    /**
     * Test that md scheduled job with empty schedule ignores.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdScheduledJobEmptySchedule() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("ScheduledJob.ScheduledJobWithEmptySchedule.Schedule", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test that md scheduled job repeat pause less than one minute.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdScheduledJobRepeatPauseLessOneMinute() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("ScheduledJob.ScheduledJobWithRepeatPauseLessOneMinute.Schedule", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test that md scheduled job repeat period less than one minute.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdScheduledJobRepeatPeriodLessOneMinute() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("ScheduledJob.ScheduledJobWithRepeatPeriodLessOneMinute.Schedule", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test that md scheduled job detailed repeat period more than one minute ignores.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdScheduledJobRepeatPeriodMoreOneMinute() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("ScheduledJob.ScheduledJobWithRepeatPeriodMoreOneMinute.Schedule", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }
}
