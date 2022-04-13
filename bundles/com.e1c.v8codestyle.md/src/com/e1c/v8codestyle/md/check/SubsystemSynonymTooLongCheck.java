/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *     Denis Maslennikov - issue #37
 *******************************************************************************/
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__SYNONYM;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SUBSYSTEM;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SUBSYSTEM__INCLUDE_IN_COMMAND_INTERFACE;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EMap;

import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.Subsystem;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Check top subsystem synonym length that should be less then 35 by default or value
 * of the parameter MAX_SUBSYSTEM_SYNONYM_LENGTH, if it is set.
 *
 * @author Denis Maslennikov
 */
public class SubsystemSynonymTooLongCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "subsystem-synonym-too-long"; //$NON-NLS-1$

    public static final String MAX_SUBSYSTEM_SYNONYM_LENGTH = "max-subsystem-synonym-length"; //$NON-NLS-1$

    public static final String MAX_SUBSYSTEM_SYNONYM_DEFAULT = "35"; //$NON-NLS-1$

    public static final String SUBSYSTEM_SYNONYM_LANGS_FILTER = "subsystem-synonym-lang-filter"; //$NON-NLS-1$

    public static final String SUBSYSTEM_SYNONYM_LANGS_DEFAULT = ""; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.SubsystemSynonymTooLongCheck_title)
            .description(Messages.SubsystemSynonymTooLongCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .extension(new TopObjectFilterExtension())
            .issueType(IssueType.UI_STYLE)
            .topObject(SUBSYSTEM)
            .checkTop()
            .features(MD_OBJECT__SYNONYM, SUBSYSTEM__INCLUDE_IN_COMMAND_INTERFACE)
            .parameter(MAX_SUBSYSTEM_SYNONYM_LENGTH, Integer.class, MAX_SUBSYSTEM_SYNONYM_DEFAULT,
                Messages.SubsystemSynonymTooLongCheck_Maximum_section_name_length)
            .parameter(SUBSYSTEM_SYNONYM_LANGS_FILTER, String.class, SUBSYSTEM_SYNONYM_LANGS_DEFAULT,
                Messages.SubsystemSynonymTooLongCheck_Exclude_languages_comma_separated);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (!(object instanceof MdObject) || monitor.isCanceled())
        {
            return;
        }

        Subsystem subsystem = (Subsystem)object;
        // Check subsystem is not top-level and included to command interface
        if (subsystem.getParentSubsystem() != null || !subsystem.isIncludeInCommandInterface())
        {
            return;
        }

        int max = parameters.getInt(MAX_SUBSYSTEM_SYNONYM_LENGTH);
        if (max <= 0)
        {
            return;
        }

        String excludeLang = parameters.getString(SUBSYSTEM_SYNONYM_LANGS_FILTER);

        EMap<String, String> synonyms = subsystem.getSynonym();

        for (Map.Entry<String, String> entry : synonyms.entrySet())
        {
            String key = entry.getKey();
            if (!excludeLang.contains(key))
            {
                String name = entry.getValue();
                if (name != null && name.length() > max)
                {
                    resultAceptor.addIssue(MessageFormat.format(
                        Messages.SubsystemSynonymTooLongCheck_Length_of_section_name_more_than_symbols_for_language,
                        name.length(), max, key), MD_OBJECT__SYNONYM);
                }
            }
        }
    }
}
