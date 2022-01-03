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
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

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
    private static final String NAME_COMMIT_TRANSACTION_RU = "ЗафиксироватьТранзакцию"; //$NON-NLS-1$
    private static final String NAME_COMMIT_TRANSACTION = "CommitTransaction"; //$NON-NLS-1$
    private static final String NAME_DATA_LOCK = "DataLock"; //$NON-NLS-1$
    private static final String NAME_DATA_LOCK_RU = "БлокировкаДанных"; //$NON-NLS-1$
    private static final String NAME_LOCK = "Lock"; //$NON-NLS-1$
    private static final String NAME_LOCK_RU = "Заблокировать"; //$NON-NLS-1$
    private static final String NAME_BEGIN_TRANSACTION_RU = "НачатьТранзакцию"; //$NON-NLS-1$
    private static final String NAME_BEGIN_TRANSACTION = "BeginTransaction"; //$NON-NLS-1$
    private static final String NAME_ROLLBACK_TRANSACTION = "RollbackTransaction"; //$NON-NLS-1$
    private static final String NAME_ROLLBACK_TRANSACTION_RU = "ОтменитьТранзакцию"; //$NON-NLS-1$

    public LockOutOfTryCheck()
    {
        super();
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
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DynamicFeatureAccess dynamicFeatureAccess = (DynamicFeatureAccess)object;
        String name = dynamicFeatureAccess.getName();
        if (!(name.equalsIgnoreCase(NAME_LOCK_RU) || name.equalsIgnoreCase(NAME_LOCK)))
        {
            return;
        }

        Expression source = dynamicFeatureAccess.getSource();
        if (!(source instanceof StaticFeatureAccess))
        {
            return;
        }

        String nameLeft = ((StaticFeatureAccess)source).getName();
        if (!(nameLeft.equalsIgnoreCase(NAME_DATA_LOCK_RU) || nameLeft.equalsIgnoreCase(NAME_DATA_LOCK)))
        {
            return;
        }

        TryExceptStatement statement = EcoreUtil2.getContainerOfType(source, TryExceptStatement.class);
        if (statement == null)
        {
            resultAceptor.addIssue(Messages.LockOutOfTry_Method_lock_out_of_try, object);
            return;
        }

        List<SimpleStatement> tryStatements = statement.getTryStatements()
            .stream()
            .filter(SimpleStatement.class::isInstance)
            .map(SimpleStatement.class::cast)
            .collect(Collectors.toList());

        checkBeginTransaction(object, resultAceptor, tryStatements);

        checkCommitTransaction(object, resultAceptor, tryStatements);

        List<SimpleStatement> exceptStatements = statement.getExceptStatements()
            .stream()
            .filter(SimpleStatement.class::isInstance)
            .map(SimpleStatement.class::cast)
            .collect(Collectors.toList());

        checkRollbackTransaction(object, resultAceptor, exceptStatements);
    }

    private void checkRollbackTransaction(Object object, ResultAcceptor resultAceptor,
        List<SimpleStatement> simpleStatements)
    {
        if (simpleStatements.isEmpty())
        {
            resultAceptor.addIssue(Messages.LockOutOfTry_RollbackTransaction_method_called_first, object);
        }
        else
        {
            SimpleStatement first = simpleStatements.get(0);
            Expression left = first.getLeft();
            if (left instanceof Invocation)
            {
                FeatureAccess staticFeatureAccess = ((Invocation)left).getMethodAccess();
                String nameFeature = staticFeatureAccess.getName();
                if (!(staticFeatureAccess instanceof StaticFeatureAccess))
                {
                    resultAceptor.addIssue(Messages.LockOutOfTry_RollbackTransaction_method_called_first, object);
                }
                if (staticFeatureAccess instanceof StaticFeatureAccess
                    && !(nameFeature.equalsIgnoreCase(NAME_ROLLBACK_TRANSACTION_RU)
                        || nameFeature.equalsIgnoreCase(NAME_ROLLBACK_TRANSACTION)))
                {
                    resultAceptor.addIssue(Messages.LockOutOfTry_RollbackTransaction_method_called_first, object);
                }
            }
            else
            {
                resultAceptor.addIssue(Messages.LockOutOfTry_RollbackTransaction_method_called_first, object);
            }
        }
    }

    private void checkCommitTransaction(Object object, ResultAcceptor resultAceptor,
        List<SimpleStatement> simpleStatements)
    {
        if (simpleStatements.isEmpty())
        {
            resultAceptor.addIssue(Messages.LockOutOfTry_CommitTransaction_should_be_the_last, object);
        }
        else
        {
            SimpleStatement last = simpleStatements.get(simpleStatements.size() - 1);
            Expression left = last.getLeft();
            if (left instanceof Invocation)
            {
                FeatureAccess staticFeatureAccess = ((Invocation)left).getMethodAccess();
                String nameFeature = staticFeatureAccess.getName();
                if (!(staticFeatureAccess instanceof StaticFeatureAccess))
                {
                    resultAceptor.addIssue(Messages.LockOutOfTry_CommitTransaction_should_be_the_last, object);
                }
                if (staticFeatureAccess instanceof StaticFeatureAccess
                    && !(nameFeature.equalsIgnoreCase(NAME_COMMIT_TRANSACTION_RU)
                        || nameFeature.equalsIgnoreCase(NAME_COMMIT_TRANSACTION)))
                {
                    resultAceptor.addIssue(Messages.LockOutOfTry_CommitTransaction_should_be_the_last, object);
                }
            }
            else
            {
                resultAceptor.addIssue(Messages.LockOutOfTry_CommitTransaction_should_be_the_last, object);
            }
        }
    }

    private void checkBeginTransaction(Object object, ResultAcceptor resultAceptor,
        List<SimpleStatement> simpleStatements)
    {
        for (SimpleStatement simpleStatement : simpleStatements)
        {
            Expression left = simpleStatement.getLeft();
            if (left instanceof Invocation)
            {
                FeatureAccess staticFeatureAccess = ((Invocation)left).getMethodAccess();
                String nameFeature = staticFeatureAccess.getName();
                if (staticFeatureAccess instanceof StaticFeatureAccess
                    && nameFeature.equalsIgnoreCase(NAME_BEGIN_TRANSACTION_RU)
                    || nameFeature.equalsIgnoreCase(NAME_BEGIN_TRANSACTION))
                {
                    resultAceptor.addIssue(Messages.LockOutOfTry_BeginTransaction_method_must_by_outside_try_block,
                        object);
                }
            }
        }
    }
}
