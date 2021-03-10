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
 *******************************************************************************/
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__DATA_LOCK_CONTROL_MODE;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.check.CheckComplexity;
import com._1c.g5.v8.dt.check.ICheckParameters;
import com._1c.g5.v8.dt.check.components.BasicCheck;
import com._1c.g5.v8.dt.check.settings.IssueSeverity;
import com._1c.g5.v8.dt.check.settings.IssueType;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.DefaultDataLockControlMode;

/**
 * Check configuration data lock mode shoul be managed
 *
 * @author Dmitriy Marmyshev
 */
public class ConfigurationDataLock
    extends BasicCheck
{

    public static final String CHECK_ID = "configuration-data-lock-mode"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        //@formatter:off
        builder.title(Messages.ConfigurationDataLock_title)
            .description(Messages.ConfigurationDataLock_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__DATA_LOCK_CONTROL_MODE);
        //@formatter:on
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Configuration configuration = (Configuration)object;
        if (!DefaultDataLockControlMode.MANAGED.equals(configuration.getDataLockControlMode()))
        {
            resultAceptor.addIssue(Messages.ConfigurationDataLock_message, CONFIGURATION__DATA_LOCK_CONTROL_MODE);
        }
    }

}
