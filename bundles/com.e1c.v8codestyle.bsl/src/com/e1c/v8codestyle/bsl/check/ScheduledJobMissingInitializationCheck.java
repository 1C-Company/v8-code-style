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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Procedure;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
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
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.COMMON_MODULE))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor progressMonitor)
    {
        Module module = (Module)object;

        if (!SslConfigurationHelper.hasSSL(bmTransaction))
        {
            return;
        }

        CommonModule commonModule = (CommonModule)module.getOwner();

        Set<String> scheduledJobMethodNames =
            getScheduledJobMethodNames(module, commonModule.getName(), progressMonitor);
        if (scheduledJobMethodNames.isEmpty())
        {
            return;
        }

        for (Method method : module.getMethods())
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }

            if (method.isExport() && method instanceof Procedure && method.getName() != null
                && scheduledJobMethodNames.contains(method.getName().toLowerCase()) && !hasInitCall(method))
            {
                resultAcceptor.addIssue(Messages.ScheduledJobMissingInitializationCheck_Issue, method,
                    NAMED_ELEMENT__NAME);
            }
        }
    }

    private Set<String> getScheduledJobMethodNames(Module module, String moduleName, IProgressMonitor progressMonitor)
    {
        Set<String> result = new HashSet<>();

        IScope jobs = scopeProvider.getScope(module, MdClassPackage.Literals.CONFIGURATION__SCHEDULED_JOBS);
        for (IEObjectDescription item : jobs.getAllElements())
        {
            if (progressMonitor.isCanceled())
            {
                return Set.of();
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

            if (methodPath != null)
            {
                Optional<String> methodName = getMethodName(methodPath, moduleName);
                if (methodName.isPresent())
                {
                    result.add(methodName.get().toLowerCase());
                }
            }
        }

        return result;
    }

    private boolean hasInitCall(Method method)
    {
        List<String> declared = new ArrayList<>();

        for (Statement statement : method.allStatements())
        {
            if (statement instanceof EmptyStatement)
            {
                continue;
            }

            if (statement instanceof SimpleStatement simple)
            {
                if (simple.getLeft() instanceof Invocation invocation)
                {
                    return MethodAccessHelper.isNameMatching(invocation, TARGET_METHOD_NAME_RU, TARGET_METHOD_NAME,
                        TARGET_MODULE_NAME_RU, TARGET_MODULE_NAME) && isDeclaredUsedInParams(declared, invocation);
                }

                if (simple.getLeft() instanceof StaticFeatureAccess staticAccess && staticAccess.getName() != null)
                {
                    declared.add(staticAccess.getName().toLowerCase());
                    continue;
                }
            }

            return false;
        }

        return false;
    }

    private Optional<String> getMethodName(String string, String moduleName)
    {
        String[] parts = string.split("\\."); //$NON-NLS-1$

        if (parts.length >= 2)
        {
            String currentModuleName = parts[parts.length - 2];
            if (currentModuleName.equalsIgnoreCase(moduleName))
            {
                return Optional.of(parts[parts.length - 1]);
            }
        }

        return Optional.empty();
    }

    private boolean isDeclaredUsedInParams(List<String> declared, Invocation invocation)
    {
        for (String variableName : declared)
        {
            if (!isVariablePassed(variableName, invocation))
            {
                return false;
            }
        }

        return true;
    }

    private boolean isVariablePassed(String variableName, Invocation invocation)
    {
        for (Expression param : invocation.getParams())
        {
            if (param instanceof StaticFeatureAccess access && variableName.equalsIgnoreCase(access.getName()))
            {
                return true;
            }
        }

        return false;
    }
}
