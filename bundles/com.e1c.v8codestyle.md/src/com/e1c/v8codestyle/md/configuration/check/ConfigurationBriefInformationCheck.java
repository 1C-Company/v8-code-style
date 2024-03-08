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
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__BRIEF_INFORMATION;

import java.text.MessageFormat;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EMap;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.e1c.v8codestyle.md.check.AbstractUiStringCheck;
import com.google.inject.Inject;

/**
 * Check configuration version should match a pattern
 *
 * @author Dmitriy Marmyshev
 */
public final class ConfigurationBriefInformationCheck
    extends AbstractUiStringCheck
{
    private static final String CHECK_ID = "configuration-brief-information"; //$NON-NLS-1$
    private static final String PARAM_PATTERN_DEFAULT = ""; //$NON-NLS-1$

    @Inject
    public ConfigurationBriefInformationCheck(IV8ProjectManager v8ProjectManager)
    {
        super(CONFIGURATION__BRIEF_INFORMATION, v8ProjectManager);
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
        builder.title("Configuration brief information")
            .description("Configuration brief information")
            .extension(new StandardCheckExtension(482, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__BRIEF_INFORMATION);
    }

    @Override
    protected String getUiStringPatternDefaultValue()
    {
        return PARAM_PATTERN_DEFAULT;
    }

    @Override
    protected String getUiStringPatternTitle()
    {
        return "Pattern";
    }

    @Override
    protected String getUiStringIsEmptyForAll()
    {
        return "Brief information is empty for all languages";
    }

    @Override
    protected String getUiStringIsEmpty(String languageCode)
    {
        return MessageFormat.format("Brief information for language \"{0}\" is empty", languageCode);
    }

    @Override
    protected String getUiStringShouldMatchPattern(String languageCode, String patternText)
    {
        return MessageFormat.format("Brief information for language \"{0}\" should match pattern: \"{1}\"",
            languageCode, patternText);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        super.check(object, resultAceptor, parameters, monitor);

        Configuration config = (Configuration)object;
        EMap<String, String> synonym = config.getSynonym();
        if (synonym.isEmpty())
        {
            return;
        }
        EMap<String, String> briefInfo = config.getBriefInformation();
        for (Entry<String, String> entry : briefInfo)
        {
            String model = synonym.get(entry.getKey());
            if (!StringUtils.isBlank(model) && !model.equalsIgnoreCase(entry.getValue()))
            {

                resultAceptor.addIssue(
                    MessageFormat.format("Value for language \"{0}\" should equals \"{1}\"", entry.getKey(), model),
                    CONFIGURATION__BRIEF_INFORMATION);
            }
        }
    }

}
