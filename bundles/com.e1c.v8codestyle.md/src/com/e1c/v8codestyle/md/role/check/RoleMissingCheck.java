/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.md.role.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__ROLES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__SCRIPT_VARIANT;
import static java.util.Map.entry;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.e1c.v8codestyle.md.check.SkipAdoptedInExtensionMdObjectExtension;

/**
 * The check that the configuration has required roles.
 *
 * @author Aleksey Kalugin
 *
 */
public class RoleMissingCheck
    extends BasicCheck<Object>
{
    private static final String CHECK_ID = "role-missing"; //$NON-NLS-1$

    //@formatter:off
    private static final Map<ScriptVariant, List<String>> ROLE_NAMES = Map.ofEntries(
        entry(ScriptVariant.ENGLISH,
            List.of("FullAccess", "SystemAdministrator", "InteractiveOpenExternalReportsAndDataProcessors")), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        entry(ScriptVariant.RUSSIAN,
            List.of("ПолныеПрава", "АдминистраторСистемы", "ИнтерактивноеОткрытиеВнешнихОтчетовИОбработок")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    );
    //@formatter:on

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof Configuration))
        {
            return;
        }

        var configuration = (Configuration)object;
        var roles = configuration.getRoles();
        for (var roleName : ROLE_NAMES.get(configuration.getScriptVariant()))
        {
            if (monitor.isCanceled()) {
                return;
            }

            if (roles.stream().noneMatch(role -> roleName.equals(role.getName())))
            {
                resultAceptor.addIssue(
                    MessageFormat.format(Messages.RoleMissing_message, roleName),
                    CONFIGURATION__ROLES);
            }
        }
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RoleMissing_title)
            .description(Messages.RoleMissing_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.TRIVIAL)
            .issueType(IssueType.WARNING)
            .extension(new TopObjectFilterExtension())
            .extension(new StandardCheckExtension(488, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipAdoptedInExtensionMdObjectExtension())
            .topObject(CONFIGURATION)
            .checkTop()
            .features(CONFIGURATION__SCRIPT_VARIANT, CONFIGURATION__ROLES);
    }

}
