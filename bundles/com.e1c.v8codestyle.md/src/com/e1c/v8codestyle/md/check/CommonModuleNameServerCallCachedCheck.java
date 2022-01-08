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
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__RETURN_VALUES_REUSE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER_CALL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Check the postfix for a module with the server call or cached attribute.
 *
 * @author Artem Iliukhin
 */
public final class CommonModuleNameServerCallCachedCheck
    extends BasicCheck
{

    private static final String SERVER_CALL_RU = "ВызовСервера"; //$NON-NLS-1$
    private static final String SERVER_CALL = "ServerCall"; //$NON-NLS-1$
    private static final String CACHED_RU = "ПовтИсп"; //$NON-NLS-1$
    private static final String CACHED = "Cached"; //$NON-NLS-1$
    private static final String CHECK_ID = "server-call-cached"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CommonModuleNameServerCallPostfixCheck_Common_module_postfix_title)
            .description(Messages.CommonModuleNameServerCallPostfixCheck_Common_module_name_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new TopObjectFilterExtension())
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(MD_OBJECT__NAME, COMMON_MODULE__RETURN_VALUES_REUSE, COMMON_MODULE__SERVER_CALL);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        CommonModule commonModule = (CommonModule)object;
        String name = commonModule.getName();
        if (commonModule.getReturnValuesReuse() != ReturnValuesReuse.DONT_USE
            && !(name.endsWith(CACHED) || name.endsWith(CACHED_RU)))
        {
            resultAceptor.addIssue(Messages.CommonModuleNameServerCallPostfixCheck_Postfix_cached, MD_OBJECT__NAME);
        }
        if (commonModule.isServerCall() && !(name.endsWith(SERVER_CALL) || name.endsWith(SERVER_CALL_RU)))
        {
            resultAceptor.addIssue(Messages.CommonModuleNameServerCallPostfixCheck_Postfix_ServerCall, MD_OBJECT__NAME);
        }
    }
}
