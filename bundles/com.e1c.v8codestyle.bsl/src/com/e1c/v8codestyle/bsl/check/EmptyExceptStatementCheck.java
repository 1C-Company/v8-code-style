/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Viktor Gukov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.TRY_EXCEPT_STATEMENT;

import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

import java.util.List;

/**
 * Checks try-except-endtry statements, for empty except statement.
 *
 * @author Viktor Gukov
 *
 */
public class EmptyExceptStatementCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "empty-except-statement";
    
    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void check(Object object, ResultAcceptor acceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof TryExceptStatement))
        {
            return;
        }

        var tryExceptStatement = TryExceptStatement.class.cast(object);

        Optional.of(tryExceptStatement)
            .map(TryExceptStatement::getExceptStatements)
            .filter(List::isEmpty)
            .ifPresent(list -> acceptor.addIssue(Messages.EmptyExceptStatementCheck_description, tryExceptStatement));

    }

    @Override
    protected void configureCheck(CheckConfigurer configurer)
    {
        configurer.title(Messages.EmptyExceptStatementCheck_title)
            .description(Messages.EmptyExceptStatementCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .module()
            .checkedObjectType(TRY_EXCEPT_STATEMENT);
    }

}
