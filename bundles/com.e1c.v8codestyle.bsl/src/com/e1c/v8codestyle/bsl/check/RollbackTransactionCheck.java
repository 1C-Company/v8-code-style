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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Rollback transaction must be in a try-catch,
 * there is no begin transaction for rollback transaction,
 * there should be no executable code between exception and rollback transaction,
 * there is no commit transaction for begin transaction.
 *
 * @author Artem Iliukhin
 */
public final class RollbackTransactionCheck
    extends AbstractTransactionCheck
{

    private static final String CHECK_ID = "rollback-transaction"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RollbackTransactionCheck_Transactions_is_broken)
            .description(Messages.RollbackTransactionCheck_Transactions_is_broken_des)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation inv = (Invocation)object;
        FeatureAccess featureAccess = inv.getMethodAccess();
        if (featureAccess instanceof StaticFeatureAccess)
        {
            String nameFeature = featureAccess.getName();
            if (!(ROLLBACK_TRANSACTION_RU.equalsIgnoreCase(nameFeature)
                || ROLLBACK_TRANSACTION.equalsIgnoreCase(nameFeature)))
            {
                return;
            }

            TryExceptStatement tryExceptStatement = EcoreUtil2.getContainerOfType(inv, TryExceptStatement.class);
            if (tryExceptStatement == null)
            {
                resultAceptor.addIssue(Messages.RollbackTransactionCheck_Rollback_transaction_must_be_in_try_catch,
                    inv);
            }

            if (monitor.isCanceled())
            {
                return;
            }

            Method method = EcoreUtil2.getContainerOfType(inv, Method.class);
            if (method == null)
            {
                return;
            }

            List<Invocation> invocations = EcoreUtil2.getAllContentsOfType(method, Invocation.class);
            for (Invocation invocation : invocations)
            {
                String invocName = invocation.getMethodAccess().getName();
                if (BEGIN_TRANSACTION_RU.equals(invocName) || BEGIN_TRANSACTION.equals(invocName))
                {
                    if (monitor.isCanceled())
                    {
                        return;
                    }
                    Statement statement = getStatementFromInvoc(invocation);
                    Statement nextStatement = null;
                    if (statement != null)
                    {
                        nextStatement = getNextStatement(statement);
                    }
                    if (nextStatement instanceof TryExceptStatement)
                    {
                        anlyseTryExcept(invocation, (TryExceptStatement)nextStatement, resultAceptor);
                    }

                    break;
                }
                else if (ROLLBACK_TRANSACTION_RU.equals(invocName) || ROLLBACK_TRANSACTION.equals(invocName))
                {
                    resultAceptor.addIssue(
                        Messages.RollbackTransactionCheck_No_begin_transaction_for_rollback_transaction, invocation);
                }
            }
        }
    }

    private void anlyseTryExcept(Invocation beginTrans, TryExceptStatement tryExceptStatement,
        ResultAcceptor resultAceptor)
    {
        List<Statement> tryStatements = getAllStatement(tryExceptStatement.getTryStatements());
        if (!tryStatements.isEmpty())
        {
            analyseTryPart(beginTrans, tryStatements, resultAceptor);
        }

        List<Statement> exceptStatement = getAllStatement(tryExceptStatement.getExceptStatements());
        if (!exceptStatement.isEmpty())
        {
            analyseExceptPart(exceptStatement, resultAceptor);
        }
    }

    private void analyseTryPart(Invocation beginTrans, List<Statement> tryStatements, ResultAcceptor resultAceptor)
    {
        boolean invocThereIs = false;
        for (Statement statement : tryStatements)
        {
            if (isCommitStatement(statement))
            {
                invocThereIs = true;
                break;
            }

        }

        if (!invocThereIs)
        {
            resultAceptor.addIssue(Messages.RollbackTransactionCheck_No_commit_transaction_for_begin_transaction,
                beginTrans);
        }
    }

    private void analyseExceptPart(List<Statement> exceptStatement, ResultAcceptor resultAceptor)
    {
        boolean invocThereIs = false;
        for (Statement statement : exceptStatement)
        {
            if (isRollbackStatement(statement))
            {
                invocThereIs = true;
                break;
            }

        }

        if (invocThereIs)
        {
            Statement firstStatement = exceptStatement.get(0);
            if (!isRollbackStatement(firstStatement))
            {
                resultAceptor.addIssue(
                    Messages.RollbackTransactionCheck_Should_be_no_executable_code_between_exception_and_rollback,
                    firstStatement);
            }
        }
    }
}
