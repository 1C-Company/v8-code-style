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
 *******************************************************************************/
package com.e1c.v8codestyle.md.commonmodule.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__PRIVILEGED;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.e1c.v8codestyle.md.check.MdObjectNameWithoutSuffix;
import com.e1c.v8codestyle.md.check.SkipAdoptedInExtensionMdObjectExtension;

/**
 * Check privileged common module name has "FullAccess" suffix
 *
 * @author Artem Iliukhin
 */
public class CommonModuleNamePrivilegedCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "common-module-name-full-access"; //$NON-NLS-1$

    private static final String NAME_SUFFIX_DEFAULT = "ПолныеПрава,FullAccess"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CommonModuleNamePrivilegedCheck_Title)
            .description(Messages.CommonModuleNamePrivilegedCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.CRITICAL)
            .issueType(IssueType.WARNING)
            .extension(new TopObjectFilterExtension())
            .extension(new MdObjectNameWithoutSuffix(NAME_SUFFIX_DEFAULT))
            .extension(new StandardCheckExtension(469, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipAdoptedInExtensionMdObjectExtension())
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(MD_OBJECT__NAME, COMMON_MODULE__PRIVILEGED);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        CommonModule commonModule = (CommonModule)object;
        if (!commonModule.isPrivileged())
        {
            return;
        }

        String message = MessageFormat.format(Messages.CommonModuleNamePrivilegedCheck_Issue,
            parameters.getString(MdObjectNameWithoutSuffix.NAME_SUFFIX_PARAMETER_NAME));
        resultAceptor.addIssue(message, MD_OBJECT__NAME);
    }
}
