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
package com.e1c.v8codestyle.bsl;

import java.util.List;

import com._1c.g5.v8.dt.bsl.model.AwaitExpression;
import com._1c.g5.v8.dt.bsl.model.AwaitStatement;
import com._1c.g5.v8.dt.bsl.model.BinaryExpression;
import com._1c.g5.v8.dt.bsl.model.Block;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.ForEachStatement;
import com._1c.g5.v8.dt.bsl.model.ForToStatement;
import com._1c.g5.v8.dt.bsl.model.FunctionStyleCreator;
import com._1c.g5.v8.dt.bsl.model.GotoStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LabeledStatement;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.Preprocessor;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItemStatements;
import com._1c.g5.v8.dt.bsl.model.RaiseStatement;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.bsl.model.UnaryExpression;
import com._1c.g5.v8.dt.bsl.model.WhileStatement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;

/**
 * Abstract Block processor for the BSL language model that performs full recursive traversal.
 * Subclasses can override any method to process specific statements or expressions.
 *
 * @author Artem Samohvalov
 */
public abstract class AbstractBlockProcessor
{

    protected final Block block;

    /**
     * Initialize with {@link Block}.
     *
     * @param block processing {@link Block}, cannot be {@code null}
     */
    public AbstractBlockProcessor(Block block)
    {
        this.block = block;
    }

    /**
     * Start processing for all statements in {@link Block}.
     */
    public void process()
    {
        doProcessInternal(block.allStatements());
    }

    /**
     * Do process internal.
     *
     * @param stats the stats, cannot be {@code null}
     */
    protected void doProcessInternal(List<? extends Statement> stats)
    {
        for (Statement stat : stats)
        {
            doProcessInternal(stat);
        }
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(Statement stat)
    {
        if (stat instanceof SimpleStatement)
            doProcessInternal((SimpleStatement)stat);
        else if (stat instanceof IfStatement)
            doProcessInternal((IfStatement)stat);
        else if (stat instanceof ForEachStatement)
            doProcessInternal((ForEachStatement)stat);
        else if (stat instanceof ReturnStatement)
            doProcessInternal((ReturnStatement)stat);
        else if (stat instanceof WhileStatement)
            doProcessInternal((WhileStatement)stat);
        else if (stat instanceof ForToStatement)
            doProcessInternal((ForToStatement)stat);
        else if (stat instanceof TryExceptStatement)
            doProcessInternal((TryExceptStatement)stat);
        else if (stat instanceof RaiseStatement)
            doProcessInternal((RaiseStatement)stat);
        else if (stat instanceof LabeledStatement)
            doProcessInternal((LabeledStatement)stat);
        else if (stat instanceof Preprocessor)
            doProcessInternal((Preprocessor)stat);
        else if (stat instanceof GotoStatement)
            doProcessInternal((GotoStatement)stat);
        else if (stat instanceof AwaitStatement)
            doProcessInternal((AwaitStatement)stat);
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(SimpleStatement stat)
    {
        doProcessInternal(stat.getLeft());
        doProcessInternal(stat.getRight());
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(IfStatement stat)
    {
        doProcessInternal(stat.getIfPart());
        for (Conditional cond : stat.getElsIfParts())
        {
            doProcessInternal(cond);
        }
        doProcessInternal(stat.getElseStatements());
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(WhileStatement stat)
    {
        doProcessInternal(stat.getPredicate());
        doProcessInternal(stat.getStatements());
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(ForToStatement stat)
    {
        doProcessInternal(stat.getInitializer());
        doProcessInternal(stat.getBound());
        doProcessInternal(stat.getVariableAccess());
        doProcessInternal(stat.getStatements());
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(ForEachStatement stat)
    {
        doProcessInternal(stat.getCollection());
        doProcessInternal(stat.getVariableAccess());
        doProcessInternal(stat.getStatements());
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(TryExceptStatement stat)
    {
        doProcessInternal(stat.getTryStatements());
        doProcessInternal(stat.getExceptStatements());
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(ReturnStatement stat)
    {
        doProcessInternal(stat.getExpression());
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(RaiseStatement stat)
    {
        for (Expression expr : stat.getExpressions())
        {
            doProcessInternal(expr);
        }
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(LabeledStatement stat)
    {
        doProcessInternal(stat.getStatement());
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(Preprocessor stat)
    {
        List<PreprocessorItem> allItems = BslUtil.getAllPreprocessorItems(stat);
        for (PreprocessorItem item : allItems)
        {
            if (item instanceof PreprocessorItemStatements)
                for (Statement statement : ((PreprocessorItemStatements)item).getStatements())
                    doProcessInternal(statement);
        }
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(GotoStatement stat)
    {
    }

    /**
     * Do process internal.
     *
     * @param stat the stat, cannot be {@code null}
     */
    protected void doProcessInternal(AwaitStatement stat)
    {
        doProcessInternal(stat.getExpression());
    }

    /**
     * Do process internal.
     *
     * @param cond the cond, cannot be {@code null}
     */
    protected void doProcessInternal(Conditional cond)
    {
        if (cond == null)
            return;
        doProcessInternal(cond.getPredicate());
        doProcessInternal(cond.getStatements());
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(Expression expr)
    {
        if (expr instanceof BinaryExpression)
            doProcessInternal((BinaryExpression)expr);
        else if (expr instanceof UnaryExpression)
            doProcessInternal((UnaryExpression)expr);
        else if (expr instanceof DynamicFeatureAccess)
            doProcessInternal((DynamicFeatureAccess)expr);
        else if (expr instanceof Invocation)
            doProcessInternal((Invocation)expr);
        else if (expr instanceof IndexAccess)
            doProcessInternal((IndexAccess)expr);
        else if (expr instanceof AwaitExpression)
            doProcessInternal((AwaitExpression)expr);
        else if (expr instanceof FunctionStyleCreator)
            doProcessInternal((FunctionStyleCreator)expr);
        else if (expr instanceof OperatorStyleCreator)
            doProcessInternal((OperatorStyleCreator)expr);
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(BinaryExpression expr)
    {
        doProcessInternal(expr.getLeft());
        doProcessInternal(expr.getRight());
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(UnaryExpression expr)
    {
        doProcessInternal(expr.getOperand());
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(DynamicFeatureAccess expr)
    {
        doProcessInternal(expr.getSource());
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(Invocation expr)
    {
        doProcessInternal(expr.getMethodAccess());
        for (Expression param : expr.getParams())
        {
            doProcessInternal(param);
        }
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(IndexAccess expr)
    {
        doProcessInternal(expr.getSource());
        doProcessInternal(expr.getIndex());
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(FunctionStyleCreator expr)
    {
        doProcessInternal(expr.getTypeNameExpression());
        doProcessInternal(expr.getParamsExpression());
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(AwaitExpression expr)
    {
        doProcessInternal(expr.getExpression());
    }

    /**
     * Do process internal.
     *
     * @param expr the expr, cannot be {@code null}
     */
    protected void doProcessInternal(OperatorStyleCreator expr)
    {
        for (Expression param : expr.getParams())
        {
            doProcessInternal(param);
        }
    }
}
