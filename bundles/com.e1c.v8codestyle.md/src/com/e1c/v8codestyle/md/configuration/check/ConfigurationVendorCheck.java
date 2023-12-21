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
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__VENDOR;

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
 * Check configuration vendor should match a pattern
 *
 * @author Dmitriy Marmyshev
 */
public final class ConfigurationVendorCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "configuration-vendor"; //$NON-NLS-1$
    private static final String PARAM_VENDOR_PATTERN = "vendorPattern"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("")
            .description("")
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(482, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__VENDOR)
            .parameter(PARAM_VENDOR_PATTERN, String.class, StringUtils.EMPTY, "Vendor pattern");
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Configuration configuration = (Configuration)object;
        String vendor = configuration.getVendor();

        if (StringUtils.isBlank(vendor))
        {
            resultAceptor.addIssue("Vendor is blank", CONFIGURATION__VENDOR);
        }
        else
        {
            String vendorPattern = parameters.getString(PARAM_VENDOR_PATTERN);
            if (!StringUtils.isBlank(vendorPattern)
                && Pattern.compile(vendorPattern, Pattern.UNICODE_CHARACTER_CLASS).matcher(vendor).matches())
            {
                String message =
                    MessageFormat.format("Vendor \"{0}\" should match pattern: \"{1}\"", vendor, vendorPattern);
                resultAceptor.addIssue(message, CONFIGURATION__VENDOR);
            }
        }
    }

}
