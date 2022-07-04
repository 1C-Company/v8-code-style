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

/**
 * Commit transaction must be in a try-catch,
 * there should be no executable code between commit transaction and exception,
 * there is no begin transaction for commit transaction,
 * there is no rollback transaction for begin transaction.
 *
 * @author Artem Iliukhin
 */
public final class CommitTransactionCheck
    extends AbstractTransactionCheck
{

    private static final String CHECK_ID = "commit-transaction"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CommitTransactionCheck_Transactions_is_broken)
            .description(Messages.CommitTransactionCheck_Transactions_is_broken_des)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .disable()
            //.extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
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
            if (!(COMMIT_TRANSACTION_RU.equalsIgnoreCase(nameFeature)
                || COMMIT_TRANSACTION.equalsIgnoreCase(nameFeature)))
            {
                return;
            }

            TryExceptStatement tryExceptStatement = EcoreUtil2.getContainerOfType(inv, TryExceptStatement.class);
            if (tryExceptStatement == null)
            {
                resultAceptor.addIssue(Messages.CommitTransactionCheck_Commit_transaction_must_be_in_try_catch, inv);
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
                else if (COMMIT_TRANSACTION_RU.equals(invocName) || COMMIT_TRANSACTION.equals(invocName))
                {
                    resultAceptor.addIssue(Messages.CommitTransactionCheck_No_begin_transaction_for_commit_transaction,
                        invocation);
                }
            }
        }
    }

    private void anlyseTryExcept(Invocation beginTrans, TryExceptStatement tryExceptStatement,
        ResultAcceptor resultAceptor)
    {
        if (tryExceptStatement.getExceptStatements().isEmpty())
        {
            resultAceptor.addIssue(Messages.CommitTransactionCheck_Transaction_contains_empty_except,
                tryExceptStatement);
        }

        List<Statement> tryStatements = getAllStatement(tryExceptStatement.getTryStatements());
        if (!tryStatements.isEmpty())
        {
            analyseTryPart(tryStatements, resultAceptor);
        }

        List<Statement> exceptStatement = getAllStatement(tryExceptStatement.getExceptStatements());
        if (!exceptStatement.isEmpty())
        {
            analyseExceptPart(beginTrans, exceptStatement, resultAceptor);
        }
    }

    private void analyseExceptPart(Invocation beginTrans, List<Statement> exceptStatement, ResultAcceptor resultAceptor)
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

        if (!invocThereIs)
        {
            resultAceptor.addIssue(Messages.CommitTransactionCheck_No_rollback_transaction_for_begin_transaction,
                beginTrans);
        }
    }

    private void analyseTryPart(List<Statement> tryStatements,
        ResultAcceptor resultAceptor)
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

        if (invocThereIs)
        {
            Statement lastStatement = getLastStatement(tryStatements);
            if (!isCommitStatement(lastStatement))
            {
                resultAceptor.addIssue(
                    Messages.CommitTransactionCheck_Should_be_no_executable_code_between_commit_and_exception,
                    lastStatement);
            }
        }
    }
}
