package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.DAILY_SCHEDULE;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.DAILY_SCHEDULE__REPEAT_PAUSE;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.DAILY_SCHEDULE__REPEAT_PERIOD_IN_DAY;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.SCHEDULE;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.SCHEDULE__DAILY_SCHEDULES;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.SCHEDULE__REPEAT_PAUSE;
import static com._1c.g5.v8.dt.schedule.model.SchedulePackage.Literals.SCHEDULE__REPEAT_PERIOD_IN_DAY;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.schedule.model.DailySchedule;
import com._1c.g5.v8.dt.schedule.model.Schedule;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/** Check mdo ScheduledJobs that a periodicity of execution a job is less than one minute
 * @author Manaev Konstantin
 *
 */
public class MdScheduledJobPeriodicityCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "shceduled-job-periodicity-too-short"; //$NON-NLS-1$
    private static final int MAX_REPEAT_SEC = 60;

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

        if (object instanceof Schedule)
        {
            if (!(((Schedule)object).getDailySchedules().isEmpty()))
            {
                return;
            }
            repeatPeriod = ((Schedule)object).getRepeatPeriodInDay();
            repeatPause = ((Schedule)object).getRepeatPause();
            if (repeatPeriod > 0 && repeatPeriod < MAX_REPEAT_SEC)
                feature = SCHEDULE__REPEAT_PERIOD_IN_DAY;
            else
                feature = SCHEDULE__REPEAT_PAUSE;
        }
        else if (object instanceof DailySchedule)
        {
            repeatPeriod = ((DailySchedule)object).getRepeatPeriodInDay();
            repeatPause = ((DailySchedule)object).getRepeatPause();
            if (repeatPeriod > 0 && repeatPeriod < MAX_REPEAT_SEC)
                feature = DAILY_SCHEDULE__REPEAT_PERIOD_IN_DAY;
            else
                feature = DAILY_SCHEDULE__REPEAT_PAUSE;
        }

        if (repeatPeriod > 0 && repeatPeriod < MAX_REPEAT_SEC || repeatPause > 0 && repeatPause < MAX_REPEAT_SEC)
        {
            String message =
                Messages.MdScheduledJobPeriodicityCheck_The_periodicity_of_execution_a_shceduled_job_is_less_than_one_minute;
            resultAceptor.addIssue(message, feature);
        }
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdScheduledJobPeriodicityCheck_title)
            .description(Messages.MdScheduledJobPeriodicityCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .topObject(SCHEDULE)
            .checkTop()
            .containment(DAILY_SCHEDULE)
            .features(DAILY_SCHEDULE__REPEAT_PERIOD_IN_DAY, DAILY_SCHEDULE__REPEAT_PAUSE);
        builder.topObject(SCHEDULE)
            .checkTop()
            .features(SCHEDULE__DAILY_SCHEDULES, SCHEDULE__REPEAT_PERIOD_IN_DAY, SCHEDULE__REPEAT_PAUSE);
    }

}
