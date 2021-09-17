package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SCHEDULED_JOB;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SCHEDULED_JOB__DESCRIPTION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SCHEDULED_JOB__PREDEFINED;

import org.eclipse.core.runtime.IProgressMonitor;

import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

public class MdScheduledJobPresentationCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "mdo-scheduled-job-presentation"; //$NON-NLS-1$

    public MdScheduledJobPresentationCheck()
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
        // TODO Auto-generated method stub

    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdScheduledJobPresentationCheck_title)
            .description(Messages.MdObjectNameLength_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .extension(new TopObjectFilterExtension())
            .issueType(IssueType.UI_STYLE)
            .topObject(SCHEDULED_JOB)
            .checkTop()
            .features(SCHEDULED_JOB__DESCRIPTION, SCHEDULED_JOB__PREDEFINED);
    }

}
