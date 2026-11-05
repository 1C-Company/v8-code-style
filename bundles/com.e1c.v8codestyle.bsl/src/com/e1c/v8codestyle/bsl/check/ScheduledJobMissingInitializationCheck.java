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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
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
import com.e1c.v8codestyle.internal.bsl.ExpressionAccessHelper;
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

    private static final String ATTRIBUTE_NAME = "attribute"; //$NON-NLS-1$
    private static final String DELIMITER = ","; //$NON-NLS-1$
    private static final String METHOD_SEPARATOR = "\\."; //$NON-NLS-1$

    // @formatter:off
    private static final List<String> DEFAULT_ATTRIBUTES = List.of(
        "ОбщегоНазначения.ПриНачалеВыполненияРегламентногоЗадания", //$NON-NLS-1$
        "ОбщегоНазначенияБПО.ПриНачалеВыполненияРегламентногоЗадания", //$NON-NLS-1$
        "CommonUse.OnStartScheduledJobExecution", //$NON-NLS-1$
        "CommonCEL.OnStartScheduledJobExecution"); //$NON-NLS-1$
    // @formatter:on

    private static final String DEFAULT_ATTRIBUTE = String.join(DELIMITER, DEFAULT_ATTRIBUTES);

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
            .parameter(ATTRIBUTE_NAME, String.class, DEFAULT_ATTRIBUTE,
                Messages.ScheduledJobMissingInitializationCheck_ParameterTitle)
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

        Map<String, Set<String>> paramAttributes = getInitCalls(parameters);

        for (Method method : module.allMethods())
        {
            if (progressMonitor.isCanceled())
            {
                return;
            }

            if (method.getName() != null && scheduledJobMethodNames.contains(method.getName().toLowerCase())
                && !hasInitCall(method, paramAttributes))
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

    private boolean hasInitCall(Method method, Map<String, Set<String>> attributes)
    {
        Set<String> declared = new HashSet<>();

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
                    return isCheckedCall(invocation, attributes) && isDeclaredUsedInParams(declared, invocation);
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

    @SuppressWarnings("unused") // unused key on computeIfAbsent line
    private Map<String, Set<String>> getInitCalls(ICheckParameters parameters)
    {
        Map<String, Set<String>> result = new HashMap<>();

        for (String parameter : parameters.getString(ATTRIBUTE_NAME).split(DELIMITER))
        {
            String trimmed = parameter.trim();

            if (!trimmed.isBlank())
            {
                String[] parts = trimmed.split(METHOD_SEPARATOR);

                if (parts.length >= 2)
                {
                    // if no key - put new element in map
                    // then add value to the set
                    result.computeIfAbsent(parts[parts.length - 2].toLowerCase(), key -> new HashSet<>())
                        .add(parts[parts.length - 1].toLowerCase());
                }
            }
        }

        return result;
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

    private boolean isCheckedCall(Invocation invocation, Map<String, Set<String>> initCalls)
    {
        Expression access = invocation.getMethodAccess();

        if (access instanceof DynamicFeatureAccess dynamicAccess
            && dynamicAccess.getSource() instanceof StaticFeatureAccess sourceAccess && sourceAccess.getName() != null
            && dynamicAccess.getName() != null)
        {
            Set<String> methods = initCalls.get(sourceAccess.getName().toLowerCase());
            return methods != null && methods.contains(dynamicAccess.getName().toLowerCase());
        }

        return false;
    }

    private boolean isDeclaredUsedInParams(Set<String> declared, Invocation invocation)
    {
        if (declared.isEmpty())
        {
            return true;
        }

        Set<String> notUsed = new HashSet<>(declared);
        for (Expression param : invocation.getParams())
        {
            String name =
                ExpressionAccessHelper.getBaseFeatureAccess(param).map(StaticFeatureAccess::getName).orElse(null);
            if (name != null)
            {
                notUsed.remove(name.toLowerCase());
            }

            if (notUsed.isEmpty())
            {
                return true;
            }
        }

        return notUsed.isEmpty();
    }
}
