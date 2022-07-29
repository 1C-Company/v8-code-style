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

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItemStatements;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.google.common.collect.Lists;

/**
 * Common functionality for transaction analysis.
 *
 * @author Artem Iliukhin
 */
abstract class AbstractTransactionCheck
    extends BasicCheck
{

    protected static final String COMMIT_TRANSACTION_RU = "ЗафиксироватьТранзакцию"; //$NON-NLS-1$
    protected static final String COMMIT_TRANSACTION = "CommitTransaction"; //$NON-NLS-1$
    protected static final String BEGIN_TRANSACTION = "BeginTransaction"; //$NON-NLS-1$
    protected static final String BEGIN_TRANSACTION_RU = "НачатьТранзакцию"; //$NON-NLS-1$
    protected static final String ROLLBACK_TRANSACTION = "RollbackTransaction"; //$NON-NLS-1$
    protected static final String ROLLBACK_TRANSACTION_RU = "ОтменитьТранзакцию"; //$NON-NLS-1$

    /**
     * Checks if is rollback statement.
     *
     * @param statement the statement
     * @return true, if is rollback statement
     */
    protected final boolean isRollbackStatement(Statement statement)
    {
        if (statement instanceof SimpleStatement && ((SimpleStatement)statement).getLeft() instanceof Invocation)
        {
            Invocation invocation = (Invocation)((SimpleStatement)statement).getLeft();
            String invocName = invocation.getMethodAccess().getName();
            if (ROLLBACK_TRANSACTION_RU.equals(invocName) || ROLLBACK_TRANSACTION.equals(invocName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the all statements.
     *
     * @param statements the statements
     * @return the all statements, cannot return {@code null}.
     */
    protected final List<Statement> getAllStatement(List<Statement> statements)
    {
        List<Statement> res = Lists.newArrayList(statements);

        Statement lastStatement = getLastStatement(res);
        while (lastStatement instanceof RegionPreprocessor)
        {
            RegionPreprocessor regionPreprocessorStatement = (RegionPreprocessor)lastStatement;
            PreprocessorItem item = regionPreprocessorStatement.getItem();
            if (item instanceof PreprocessorItemStatements && item.hasStatements())
            {
                PreprocessorItemStatements itemStatements = (PreprocessorItemStatements)item;
                res.addAll(itemStatements.getStatements());
            }
            item = regionPreprocessorStatement.getItemAfter();
            if (item instanceof PreprocessorItemStatements && item.hasStatements())
            {
                PreprocessorItemStatements itemStatements = (PreprocessorItemStatements)item;
                res.addAll(itemStatements.getStatements());
            }
            lastStatement = getLastStatement(res);
        }
        return res;
    }

    /**
     * Gets the last statement or {@code null} if not found.
     *
     * @param statements the statements
     * @return the last statement, can return {@code null}.
     */
    protected final Statement getLastStatement(List<Statement> statements)
    {
        int index = statements.size() - 1;
        while (index >= 0 && statements.get(index) instanceof EmptyStatement)
        {
            index--;
        }
        if (index >= 0)
        {
            return statements.get(index);
        }
        return null;
    }

    /**
     * Gets the next statement or {@code null} if not found.
     *
     * @param statement the statement
     * @return the next statement, can return {@code null}
     */
    protected final Statement getNextStatement(Statement statement)
    {
        Iterator<EObject> it = EcoreUtil2.getAllContainers(statement).iterator();
        while (it.hasNext())
        {
            EObject container = it.next();
            List<Statement> st = null;
            if (container instanceof LoopStatement)
            {
                st = ((LoopStatement)container).getStatements();
            }
            else if (container instanceof Conditional)
            {
                st = ((Conditional)container).getStatements();
            }
            else if (container instanceof IfStatement)
            {
                st = ((IfStatement)container).getElseStatements();
            }
            else if (container instanceof TryExceptStatement)
            {
                st = getStatementsFromContainer((TryExceptStatement)container);
            }
            else if (container instanceof PreprocessorItemStatements)
            {
                st = ((PreprocessorItemStatements)container).getStatements();
            }
            else
            {
                st = getStatementsFromContainer(container);
            }
            if (st != null)
            {
                int index = st.indexOf(statement);
                if (index != -1 && index + 1 < st.size())
                {
                    return st.get(index + 1);
                }
            }
        }
        return null;
    }

    /**
     * Gets the statement from invoc or {@code null} if not found.
     *
     * @param invocation the invocation
     * @return the statement from invoc, can return {@code null}
     */
    protected final Statement getStatementFromInvoc(Invocation invocation)
    {
        EObject container = invocation.eContainer();
        while (!(container instanceof Statement))
        {
            container = container.eContainer();
        }
        return container instanceof Statement ? (Statement)container : null;
    }

    /**
     * Checks if is commit statement.
     *
     * @param statement the statement
     * @return true, if is commit statement
     */
    protected final boolean isCommitStatement(Statement statement)
    {
        if (statement instanceof SimpleStatement && ((SimpleStatement)statement).getLeft() instanceof Invocation)
        {
            Invocation invocation = (Invocation)((SimpleStatement)statement).getLeft();
            String invocName = invocation.getMethodAccess().getName();
            if (COMMIT_TRANSACTION_RU.equals(invocName) || COMMIT_TRANSACTION.equals(invocName))
            {
                return true;
            }
        }
        return false;
    }

    private List<Statement> getStatementsFromContainer(TryExceptStatement container)
    {
        List<Statement> res = Lists.newArrayList();
        res.addAll(container.getTryStatements());
        res.addAll(container.getExceptStatements());
        return res;
    }

    @SuppressWarnings("unchecked")
    private List<Statement> getStatementsFromContainer(EObject container)
    {
        Object obj = container.eGet(BslPackage.Literals.BLOCK__STATEMENTS);
        return obj instanceof List ? (List<Statement>)obj : null;
    }
}
