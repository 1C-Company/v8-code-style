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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;

import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks for initialization of the data lock. If the creation of a lock is found, the call of the Lock() method is
 * checked, and the call must be in a try.
 *
 * @author Artem Iliukhin
 */
public final class LockOutOfTryCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "lock-out-of-try"; //$NON-NLS-1$
    private static final String NAME_DATA_LOCK = "DataLock"; //$NON-NLS-1$
    private static final String NAME_LOCK = "Lock"; //$NON-NLS-1$
    private static final String NAME_LOCK_RU = "Заблокировать"; //$NON-NLS-1$
    private final TypesComputer typesComputer;

    @Inject
    public LockOutOfTryCheck()
    {
        super();

        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        this.typesComputer = rsp.get(TypesComputer.class);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.LockOutOfTry_Lock_out_of_try)
            .description(Messages.LockOutOfTry_Checks_for_init_of_the_data_lock)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DynamicFeatureAccess dynamicFeatureAccess = (DynamicFeatureAccess)object;

        String name = dynamicFeatureAccess.getName();
        if (BslUtil.getInvocation(dynamicFeatureAccess) == null
            || !NAME_LOCK.equalsIgnoreCase(name) && !NAME_LOCK_RU.equalsIgnoreCase(name))
        {
            return;
        }

        Expression source = dynamicFeatureAccess.getSource();
        Environmental env = EcoreUtil2.getContainerOfType(source, Environmental.class);
        if (Objects.isNull(env))
        {
            return;
        }

        List<TypeItem> types = typesComputer.computeTypes(source, env.environments());
        for (TypeItem type : types)
        {
            if (NAME_DATA_LOCK.equals(McoreUtil.getTypeName(type))
                && Objects.isNull(EcoreUtil2.getContainerOfType(source, TryExceptStatement.class)))
            {
                resultAceptor.addIssue(Messages.LockOutOfTry_Method_lock_out_of_try, object);
            }
        }
    }
}
