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
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__UPDATE_CATALOG_ADDRESS;

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
public final class ConfigurationUpdateCatalogAddressCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "configuration-update-catalog-address"; //$NON-NLS-1$
    private static final String PARAM_PATTERN = "addressPattern"; //$NON-NLS-1$
    private static final String PARAM_PATTERN_DEFAULT = ""; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("Configuration Update catalod address")
            .description("Configuration Update catalod address")
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(482, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__UPDATE_CATALOG_ADDRESS)
            .parameter(PARAM_PATTERN, String.class, PARAM_PATTERN_DEFAULT, "Address pattern");
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Configuration configuration = (Configuration)object;
        String address = configuration.getUpdateCatalogAddress();

        if (StringUtils.isBlank(address))
        {
            resultAceptor.addIssue("Update catalod address is blank", CONFIGURATION__UPDATE_CATALOG_ADDRESS);
        }
        else
        {
            String addressPattern = parameters.getString(PARAM_PATTERN);
            if (!StringUtils.isBlank(addressPattern)
                && Pattern.compile(addressPattern, Pattern.UNICODE_CHARACTER_CLASS).matcher(address).matches())
            {
                String message = MessageFormat.format("Update catalod address \"{0}\" should match pattern: \"{1}\"",
                    address, addressPattern);
                resultAceptor.addIssue(message, CONFIGURATION__UPDATE_CATALOG_ADDRESS);
            }
        }
    }

}
