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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.Procedure;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage;
import com._1c.g5.v8.dt.metadata.mdclass.ScheduledJob;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.SslConfigurationHelper;
import com.e1c.v8codestyle.bsl.strict.check.AbstractTypeCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.e1c.v8codestyle.internal.bsl.MethodAccessHelper;
import com.google.inject.Inject;

/**
 * Finds common module methods used as scheduled job handlers and adds an issue if the first executable statement
 * of the method is not a call of the CommonUse.OnStartScheduledJobExecution procedure.
 * 
 * @author Artem Samohvalov
 */
public class ScheduledJobMissingInitializationCheck
    extends AbstractTypeCheck
{
    private static final String SCHEDULED_JOB_METHOD_NAME_USER_DATA = "methodName"; //$NON-NLS-1$

    private static final String TARGET_MODULE_NAME = "CommonUse"; //$NON-NLS-1$
    private static final String TARGET_MODULE_NAME_RU = "ОбщегоНазначения"; //$NON-NLS-1$

    private static final String TARGET_METHOD_NAME = "OnStartScheduledJobExecution"; //$NON-NLS-1$
    private static final String TARGET_METHOD_NAME_RU = "ПриНачалеВыполненияРегламентногоЗадания"; //$NON-NLS-1$

    @Inject
    public ScheduledJobMissingInitializationCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, INamingService namingService, IBmModelManager bmModelManager,
        IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return "scheduled-job-missing-initialization"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ScheduledJobMissingInitializationCheck_Title)
            .description(Messages.ScheduledJobMissingInitializationCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(539, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(new ModuleTopObjectNameFilterExtension())
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor progressMonitor)
    {
        Method method = (Method)object;
        Module module = EcoreUtil2.getContainerOfType(method, Module.class);

        if (method.isExport() && method instanceof Procedure && module != null
            && module.getOwner() instanceof CommonModule commonModule && !hasInitCall(method)
            && SslConfigurationHelper.hasSSL(bmTransaction)
            && isScheduledJobMethod(method, module, commonModule, progressMonitor))
        {
            resultAcceptor.addIssue(Messages.ScheduledJobMissingInitializationCheck_Issue, method, NAMED_ELEMENT__NAME);
        }
    }

    private boolean isScheduledJobMethod(Method method, Module module, CommonModule commonModule,
        IProgressMonitor progressMonitor)
    {
        String moduleName = commonModule.getName();
        String methodName = method.getName();

        if (StringUtils.isBlank(moduleName) || StringUtils.isBlank(methodName))
        {
            return false;
        }

        String suffix = "." + moduleName.toLowerCase() + "." + methodName.toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
        IScope jobs = scopeProvider.getScope(module, MdClassPackage.Literals.CONFIGURATION__SCHEDULED_JOBS);
        for (IEObjectDescription item : jobs.getAllElements())
        {
            if (progressMonitor.isCanceled())
            {
                return false;
            }

            String methodPath = item.getUserData(SCHEDULED_JOB_METHOD_NAME_USER_DATA);
            if (methodPath == null)
            {
                EObject mdObject = item.getEObjectOrProxy();
                if (mdObject instanceof ScheduledJob job)
                {
                    methodPath = job.getMethodName();
                }
            }

            if (methodPath != null && methodPath.toLowerCase().endsWith(suffix))
            {
                return true;
            }
        }

        return false;
    }

    private boolean hasInitCall(Method method)
    {
        for (Statement statement : method.allStatements())
        {
            if (statement instanceof EmptyStatement)
            {
                continue;
            }
            if (statement instanceof SimpleStatement simple && simple.getLeft() instanceof Invocation invocation)
            {
                return MethodAccessHelper.isNameMatching(invocation, TARGET_METHOD_NAME_RU, TARGET_METHOD_NAME,
                    TARGET_MODULE_NAME_RU, TARGET_MODULE_NAME);
            }
            return false;
        }
        return false;
    }
}
