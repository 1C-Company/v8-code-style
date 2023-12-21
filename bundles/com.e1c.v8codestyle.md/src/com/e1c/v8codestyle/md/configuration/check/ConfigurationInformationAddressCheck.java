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
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__CONFIGURATION_INFORMATION_ADDRESS;

import java.text.MessageFormat;

import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.e1c.v8codestyle.md.check.AbstractUiStringCheck;
import com.google.inject.Inject;

/**
 * Check configuration version should match a pattern
 *
 * @author Dmitriy Marmyshev
 */
public final class ConfigurationInformationAddressCheck
    extends AbstractUiStringCheck
{
    private static final String CHECK_ID = "configuration-information-address"; //$NON-NLS-1$
    private static final String PARAM_PATTERN_DEFAULT = ""; //$NON-NLS-1$

    @Inject
    public ConfigurationInformationAddressCheck(IV8ProjectManager v8ProjectManager)
    {
        super(CONFIGURATION__CONFIGURATION_INFORMATION_ADDRESS, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        super.configureCheck(builder);
        builder.title("Configuration information address")
            .description("Configuration information address")
            .extension(new StandardCheckExtension(482, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__CONFIGURATION_INFORMATION_ADDRESS);
    }

    @Override
    protected String getUiStringPatternDefaultValue()
    {
        return PARAM_PATTERN_DEFAULT;
    }

    @Override
    protected String getUiStringPatternTitle()
    {
        return "Address pattern";
    }

    @Override
    protected String getUiStringIsEmptyForAll()
    {
        return "Configuration information address is empty for all languages";
    }

    @Override
    protected String getUiStringIsEmpty(String languageCode)
    {
        return MessageFormat.format("Configuration information address for language \"{0}\" is empty", languageCode);
    }

    @Override
    protected String getUiStringShouldMatchPattern(String languageCode, String patternText)
    {
        return MessageFormat.format(
            "Configuration information address for language \"{0}\" should match pattern: \"{1}\"", languageCode,
            patternText);
    }

}
