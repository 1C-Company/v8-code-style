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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__COMMON_MODULES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SCHEDULED_JOB;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SCHEDULED_JOB__METHOD_NAME;

import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ScheduledJob;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.SslConfigurationHelper;
import com.e1c.v8codestyle.bsl.strict.check.AbstractTypeCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.e1c.v8codestyle.internal.bsl.MethodAccessHelper;
import com.google.inject.Inject;

/**
 * The check find scheduled job methods and adds issue if it's there no CommonUse.OnStartScheduledJobExecution call on the first statement
 *
 * @author Artem Samohvalov
 */
public class ScheduledJobMissingInitializationCheck
    extends AbstractTypeCheck
{
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
            .topObject(SCHEDULED_JOB)
            .checkTop()
            .features(SCHEDULED_JOB__METHOD_NAME);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor progressMonitor)
    {
        ScheduledJob scheduledJob = (ScheduledJob)object;

        if (!SslConfigurationHelper.hasSSL(bmTransaction))
        {
            return;
        }

        Optional<Method> method = getMethodByName(scheduledJob, scheduledJob.getMethodName());
        if (method.isPresent() && !hasInitCall(method.get()))
        {
            resultAcceptor.addIssue(Messages.ScheduledJobMissingInitializationCheck_Issue, SCHEDULED_JOB__METHOD_NAME);
        }
    }

    private Optional<Method> getMethodByName(EObject context, String fullName)
    {
        if (StringUtils.isBlank(fullName))
        {
            return Optional.empty();
        }

        String[] parts = fullName.split("\\."); //$NON-NLS-1$
        if (parts.length < 3)
        {
            return Optional.empty();
        }
        String moduleName = parts[parts.length - 2];
        String methodName = parts[parts.length - 1];

        return getMethodByNameAndModule(methodName, moduleName, context);
    }

    private Optional<Method> getMethodByNameAndModule(String methodName, String moduleName, EObject context)
    {
        IScope modules = scopeProvider.getScope(context, CONFIGURATION__COMMON_MODULES);
        for (IEObjectDescription item : modules.getAllElements())
        {
            if (!moduleName.equalsIgnoreCase(item.getName().getLastSegment()))
            {
                continue;
            }

            EObject moduleObject = item.getEObjectOrProxy();
            if (moduleObject.eIsProxy())
            {
                moduleObject = EcoreUtil.resolve(moduleObject, context);
            }
            if (!(moduleObject instanceof CommonModule commonModule))
            {
                return Optional.empty();
            }

            Module bslModule = commonModule.getModule();
            if (bslModule == null)
            {
                return Optional.empty();
            }

            return bslModule.allMethods().stream().filter(m -> methodName.equalsIgnoreCase(m.getName())).findFirst();
        }

        return Optional.empty();
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
