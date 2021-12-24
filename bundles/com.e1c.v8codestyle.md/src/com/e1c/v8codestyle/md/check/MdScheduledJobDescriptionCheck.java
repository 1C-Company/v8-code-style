/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *     Sergey Kozynskiy - issue #431
 *******************************************************************************/
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SCHEDULED_JOB;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SCHEDULED_JOB__DESCRIPTION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SCHEDULED_JOB__PREDEFINED;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.metadata.mdclass.ScheduledJob;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;


/**
 * The check the predefined {@link SCHEDULED_JOB} has't description.
 *
 * @author Sergey Kozynskiy
 */
public class MdScheduledJobDescriptionCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "mdo-scheduled-job-description"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdScheduledJobDescriptionCheck_title)
            .description(Messages.MdScheduledJobDescriptionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(SCHEDULED_JOB)
            .checkTop()
            .features(SCHEDULED_JOB__DESCRIPTION, SCHEDULED_JOB__PREDEFINED);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }

        ScheduledJob mdObject = (ScheduledJob)object;
        String description = mdObject.getDescription();

        if (mdObject.isPredefined() && !StringUtils.isBlank(description))
        {
            resultAceptor.addIssue(Messages.MdScheduledJobDescriptionCheck_message, SCHEDULED_JOB__DESCRIPTION);
        }
    }
}
