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

package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.DAILY_SCHEDULE;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.DAILY_SCHEDULE__REPEAT_PAUSE;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.DAILY_SCHEDULE__REPEAT_PERIOD_IN_DAY;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.SCHEDULE;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.SCHEDULE__DAILY_SCHEDULES;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.SCHEDULE__REPEAT_PAUSE;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.SCHEDULE__REPEAT_PERIOD_IN_DAY;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.schedule.model.DailySchedule;
import com._1c.g5.v8.dt.schedule.model.Schedule;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * Check mdo ScheduledJobs that a periodicity of execution a job is less than one minute
 *
 * @author Manaev Konstantin
 */
public final class MdScheduledJobPeriodicityCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "scheduled-job-periodicity-too-short"; //$NON-NLS-1$
    private static final String MINIMUM_JOB_INTERVAL = "minimum-job-interval"; //$NON-NLS-1$
    private static final String MINIMUM_JOB_INTERVAL_DEFAULT = "60"; //$NON-NLS-1$

    public MdScheduledJobPeriodicityCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof DailySchedule || object instanceof Schedule))
        {
            return;
        }

        EStructuralFeature feature = null;
        int repeatPeriod = 0;
        int repeatPause = 0;
        int minJobInterval = parameters.getInt(MINIMUM_JOB_INTERVAL);

        if (object instanceof Schedule)
        {
            if (!(((Schedule)object).getDailySchedules().isEmpty()))
            {
                return;
            }
            repeatPeriod = ((Schedule)object).getRepeatPeriodInDay();
            repeatPause = ((Schedule)object).getRepeatPause();
            if (repeatPeriod > 0 && repeatPeriod < minJobInterval)
            {
                feature = SCHEDULE__REPEAT_PERIOD_IN_DAY;
            }
            else
            {
                feature = SCHEDULE__REPEAT_PAUSE;
            }
        }
        else if (object instanceof DailySchedule)
        {
            repeatPeriod = ((DailySchedule)object).getRepeatPeriodInDay();
            repeatPause = ((DailySchedule)object).getRepeatPause();
            if (repeatPeriod > 0 && repeatPeriod < minJobInterval)
            {
                feature = DAILY_SCHEDULE__REPEAT_PERIOD_IN_DAY;
            }
            else
            {
                feature = DAILY_SCHEDULE__REPEAT_PAUSE;
            }
        }

        if (repeatPeriod > 0 && repeatPeriod < minJobInterval || repeatPause > 0 && repeatPause < minJobInterval)
        {
            resultAceptor.addIssue(MessageFormat.format(
                Messages.MdScheduledJobPeriodicityCheck_The_minimum_job_interval_is_less_then_minute, minJobInterval),
                feature);
        }
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(MessageFormat.format(Messages.MdScheduledJobPeriodicityCheck_title, MINIMUM_JOB_INTERVAL_DEFAULT))
            .description(
                MessageFormat.format(Messages.MdScheduledJobPeriodicityCheck_description, MINIMUM_JOB_INTERVAL_DEFAULT))
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .extension(new StandardCheckExtension(402, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipAdoptedInExtensionMdObjectExtension())
            .topObject(SCHEDULE)
            .checkTop()
            .containment(DAILY_SCHEDULE)
            .features(DAILY_SCHEDULE__REPEAT_PERIOD_IN_DAY, DAILY_SCHEDULE__REPEAT_PAUSE)
            .parameter(MINIMUM_JOB_INTERVAL, Integer.class, MINIMUM_JOB_INTERVAL_DEFAULT,
                Messages.MdScheduledJobPeriodicityCheck_Minimum_job_interval_description);
        builder.topObject(SCHEDULE)
            .checkTop()
            .features(SCHEDULE__DAILY_SCHEDULES, SCHEDULE__REPEAT_PERIOD_IN_DAY, SCHEDULE__REPEAT_PAUSE);
    }

}
