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

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * The try operator was not found after calling begin transaction.
 *
 * @author Artem Iliukhin
 */
public final class BeginTransactionCheck
    extends AbstractTransactionCheck
{

    private static final String CHECK_ID = "begin-transaction"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.BeginTransactionCheck_Begin_transaction_is_incorrect)
            .description(Messages.BeginTransactionCheck_Try_must_be_after_begin)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .disable()
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
            if (monitor.isCanceled())
            {
                return;
            }

            String nameFeature = featureAccess.getName();
            if (!(BEGIN_TRANSACTION_RU.equalsIgnoreCase(nameFeature)
                || BEGIN_TRANSACTION.equalsIgnoreCase(nameFeature)))
            {
                return;
            }

            Statement statement = getStatementFromInvoc(inv);
            boolean tryWasFound = false;
            while (statement != null)
            {
                statement = getNextStatement(statement);
                if (statement instanceof TryExceptStatement)
                {
                    tryWasFound = true;
                    break;
                }
            }

            if (!tryWasFound)
            {
                resultAceptor.addIssue(Messages.BeginTransactionCheck_Try_was_not_found_after_calling_begin, inv);
            }
        }
    }
}
