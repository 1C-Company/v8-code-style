/**
 * Copyright (C) 2022, 1C
 */
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
    private static final String CHECK_ID = "undefined-variable"; //$NON-NLS-1$

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
        if (fa.eContainer() instanceof Invocation)
        {
            return;
        }

        String varName = fa.getName();
        if (fa.getImplicitVariable() == null)
        {
            String msg = NLS.bind(Messages.ModuleUndefinedVariable_msg, varName);
            if (!fa.getFeatureEntries().isEmpty())
            {
                Method method = EcoreUtil2.getContainerOfType(fa, Method.class);
                if (method.allDeclareStatements()
                    .stream()
                    .flatMap(ds -> ds.getVariables().stream())
                    .anyMatch(v -> v.getName().equals(varName)))
                {
                    return;
                }

                FeatureEntry entry = fa.getFeatureEntries().get(0);
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
}
