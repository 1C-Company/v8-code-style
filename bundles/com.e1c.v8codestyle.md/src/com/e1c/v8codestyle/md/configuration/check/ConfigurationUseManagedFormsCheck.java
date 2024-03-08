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
package com.e1c.v8codestyle.md.configuration.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__USE_MANAGED_FORM_IN_ORDINARY_APPLICATION;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.e1c.v8codestyle.md.check.SkipAdoptedInExtensionMdObjectExtension;

/**
 * Check configuration should set use managed forms in ordinary application.
 *
 * @author Dmitriy Marmyshev
 */
public final class ConfigurationUseManagedFormsCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "configuration-use-managed-form-in-ordinary-application"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("Configuration use managed forms in ordinary application")
            .description("Configuration use managed forms in ordinary application")
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PORTABILITY)
            .extension(new StandardCheckExtension(467, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(SkipAdoptedInExtensionMdObjectExtension.instance())
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__USE_MANAGED_FORM_IN_ORDINARY_APPLICATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Configuration configuration = (Configuration)object;
        if (!configuration.isUseManagedFormInOrdinaryApplication())
        {
            resultAceptor.addIssue("Configuration should use managed forms in ordinary application",
                CONFIGURATION__USE_MANAGED_FORM_IN_ORDINARY_APPLICATION);
        }
    }

}
