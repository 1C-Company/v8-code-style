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
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__VERSION;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * Check configuration version should match a pattern
 *
 * @author Dmitriy Marmyshev
 */
public final class ConfigurationVersionCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "configuration-version"; //$NON-NLS-1$
    private static final String PARAM_VERSION_PATTERN = "versionPattern"; //$NON-NLS-1$
    private static final String PARAM_VERSION_PATTERN_DEFAULT =
        "^([1-9]|\\d{2,})\\.\\d{1,2}\\.([1-9]|\\d{2,})\\.([1-9]|\\d{2,})$"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("Configuration version format")
            .description("Configuration version format")
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.LIBRARY_DEVELOPMENT_AND_USAGE)
            .extension(new StandardCheckExtension(483, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__VERSION)
            .parameter(PARAM_VERSION_PATTERN, String.class, PARAM_VERSION_PATTERN_DEFAULT, "Version pattern");
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Configuration configuration = (Configuration)object;
        String version = configuration.getVersion();

        if (StringUtils.isBlank(version))
        {
            resultAceptor.addIssue("Version is blank", CONFIGURATION__VERSION);
        }
        else
        {
            String versionPattern = parameters.getString(PARAM_VERSION_PATTERN);
            if (!StringUtils.isBlank(versionPattern)
                && !Pattern.compile(versionPattern, Pattern.UNICODE_CHARACTER_CLASS).matcher(version).matches())
            {
                String message =
                    MessageFormat.format("Version \"{0}\" should match pattern: \"{1}\"", version, versionPattern);
                resultAceptor.addIssue(message, CONFIGURATION__VERSION);
            }
        }
    }

}
