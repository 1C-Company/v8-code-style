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

import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.DefaultDataLockControlMode;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Check configuration data lock mode should be managed
 *
 * @author Dmitriy Marmyshev
 */
public final class ConfigurationDataLock
    extends BasicCheck
{

    private static final String CHECK_ID = "configuration-data-lock-mode"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ConfigurationDataLock(IV8ProjectManager v8ProjectManager)
    {
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ConfigurationDataLock_title)
            .description(Messages.ConfigurationDataLock_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__DATA_LOCK_CONTROL_MODE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Configuration configuration = (Configuration)object;
        IV8Project v8Project = v8ProjectManager.getProject(configuration);
        if (v8Project instanceof IConfigurationProject
            && DefaultDataLockControlMode.MANAGED != configuration.getDataLockControlMode())
        {
            resultAceptor.addIssue(Messages.ConfigurationDataLock_message, CONFIGURATION__DATA_LOCK_CONTROL_MODE);
        }
    }

}
