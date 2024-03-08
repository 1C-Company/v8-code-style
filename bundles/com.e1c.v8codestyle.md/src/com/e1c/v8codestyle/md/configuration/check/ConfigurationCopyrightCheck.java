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
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__COPYRIGHT;

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
public final class ConfigurationCopyrightCheck
    extends AbstractUiStringCheck
{
    private static final String CHECK_ID = "configuration-copyright"; //$NON-NLS-1$
    private static final String PARAM_PATTERN_DEFAULT =
        "Copyright \\(C\\) .*, \\d{4}\\-\\d{4}\\. (All rights reserved|Все права защищены)\\."; //$NON-NLS-1$

    @Inject
    public ConfigurationCopyrightCheck(IV8ProjectManager v8ProjectManager)
    {
        super(CONFIGURATION__COPYRIGHT, v8ProjectManager);
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
        builder.title("Configuration copyright")
            .description("Configuration copyright")
            .extension(new StandardCheckExtension(482, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__COPYRIGHT);
    }

    @Override
    protected String getUiStringPatternDefaultValue()
    {
        return PARAM_PATTERN_DEFAULT;
    }

    @Override
    protected String getUiStringPatternTitle()
    {
        return "Copyright pattern";
    }

    @Override
    protected String getUiStringIsEmptyForAll()
    {
        return "Copyright is empty for all languages";
    }

    @Override
    protected String getUiStringIsEmpty(String languageCode)
    {
        return MessageFormat.format("Copyright for language \"{0}\" is empty", languageCode);
    }

    @Override
    protected String getUiStringShouldMatchPattern(String languageCode, String patternText)
    {
        return MessageFormat.format("Copyright for language \"{0}\" should match pattern: \"{1}\"", languageCode,
            patternText);
    }
}
