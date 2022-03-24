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
package com.e1c.v8codestyle.bsl.check;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.validation.BslPreferences;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Check for undefined variable in bsl module
 *
 * @author Vadim Geraskin
 */
public class UndefinedVariableCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "module-undefined-variable"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        if (progressMonitor.isCanceled())
        {
            return;
        }
        StaticFeatureAccess fa = (StaticFeatureAccess)object;
        if (fa.eContainer() instanceof Invocation || fa.getImplicitVariable() == null)
        {
            return;
        }

        String varName = fa.getName();
        String msg = NLS.bind(Messages.ModuleUndefinedVariable_msg, varName);
        if (!fa.getFeatureEntries().isEmpty())
        {
            if (allStatementsAreDeclare(fa, varName))
            {
                return;
            }
            FeatureEntry entry = fa.getFeatureEntries().get(0);
            Environments envs = getEnv(entry);

            Invocation invocation = BslUtil.getInvocation(entry);
            if (invocation == null && !BslUtil.isEventHandler(entry))
            {
                msg += " [" + Arrays.stream(envs.toArray()) //$NON-NLS-1$
                    .map(McoreUtil::getEnvironmentText)
                    .collect(Collectors.joining(", ")) //$NON-NLS-1$
                    + ']';
            }
            else
            {
                return;
            }
        }
        resultAcceptor.addIssue(msg, fa);
    }

    @Override
    protected void configureCheck(CheckConfigurer configurationBuilder)
    {
        configurationBuilder.title(Messages.ModuleUndefinedVariableCheck_Title)
            .description(Messages.ModuleUndefinedVariableCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .module()
            .checkedObjectType(BslPackage.Literals.STATIC_FEATURE_ACCESS);
    }

    private static boolean allStatementsAreDeclare(StaticFeatureAccess fa, String varName)
    {
        Method method = EcoreUtil2.getContainerOfType(fa, Method.class);
        return method.allDeclareStatements()
            .stream()
            .flatMap(ds -> ds.getVariables().stream())
            .anyMatch(v -> v.getName().equals(varName));
    }

    private static Environments getEnv(FeatureEntry entry)
    {
        Environments envs = new Environments(entry.getEnvironments().toArray());
        if (entry.eContainer() instanceof StaticFeatureAccess && envs.contains(Environment.MNG_CLIENT)
            && !envs.contains(Environment.SERVER) && !BslPreferences.detectIsClientServer(entry))
        {
            StaticFeatureAccess sfa = (StaticFeatureAccess)entry.eContainer();
            if (sfa.getFeatureEntries()
                .stream()
                .anyMatch(item -> item != entry && item.getEnvironments().contains(Environment.SERVER)))
            {
                envs = envs.remove(Environment.MNG_CLIENT);
            }
        }

        return envs;
    }
}
