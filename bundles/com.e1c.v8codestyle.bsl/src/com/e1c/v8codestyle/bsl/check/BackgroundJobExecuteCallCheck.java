/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.strict.check.AbstractTypeCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Finding the call to BackgroundJobs.Execute and add issue if there SSL in configuration
 * 
 * @author Artem Samohvalov
 */
public class BackgroundJobExecuteCallCheck
    extends AbstractTypeCheck
{
    private static final String TARGET_METHOD_NAME = "Execute"; //$NON-NLS-1$
    private static final String TARGET_METHOD_NAME_RU = "Выполнить"; //$NON-NLS-1$

    private static final String TARGET_MODULE_NAME = "BackgroundJobs"; //$NON-NLS-1$
    private static final String TARGET_MODULE_NAME_RU = "ФоновыеЗадания"; //$NON-NLS-1$

    @Inject
    public BackgroundJobExecuteCallCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, INamingService namingService, IBmModelManager bmModelManager,
        IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return "background-job-execute-call"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.BackgroundJobExecuteCallCheck_Title)
            .description(Messages.BackgroundJobExecuteCallCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(642, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor progressMonitor)
    {
        Invocation invocation = (Invocation)object;

        if (invocation.getMethodAccess() instanceof DynamicFeatureAccess dynamicAccess
            && dynamicAccess.getSource() instanceof StaticFeatureAccess staticAccess
            && isCheckedCall(staticAccess.getName(), dynamicAccess.getName()))
        {
            if (SslConfigurationHelper.hasSSL(bmTransaction))
            {
                resultAcceptor.addIssue(Messages.BackgroundJobExecuteCallCheck_Issue, invocation);
            }
        }
    }

    private boolean isCheckedCall(String moduleName, String methodName)
    {
        return (methodName.equalsIgnoreCase(TARGET_METHOD_NAME_RU) || methodName.equalsIgnoreCase(TARGET_METHOD_NAME))
            && (moduleName.equalsIgnoreCase(TARGET_MODULE_NAME_RU) || moduleName.equalsIgnoreCase(TARGET_MODULE_NAME));
    }
}
