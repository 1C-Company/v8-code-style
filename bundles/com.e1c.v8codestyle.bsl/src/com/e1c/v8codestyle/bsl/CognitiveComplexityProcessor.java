/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Manaev Konstantin - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.dt.bsl.model.BinaryExpression;
import com._1c.g5.v8.dt.bsl.model.BinaryOperation;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FunctionStyleCreator;
import com._1c.g5.v8.dt.bsl.model.GotoStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.bsl.model.UnaryExpression;
import com._1c.g5.v8.dt.bsl.model.WhileStatement;

/**
 * The cognitive complexity processor.
 *
 * @author Manaev Konstantin
 */
public final class CognitiveComplexityProcessor
    implements IComplexityProcessor
{
    private static final String TRENARY_OPERATOR = "?"; //$NON-NLS-1$

    @Override
    public int compute(Method method, IProgressMonitor monitor)
    {
        int nestedLevel = 0;
        int complexityValue = 0;
        String uniqueName = method.getName();
        for (Statement statement : method.allStatements())
        {
            if (monitor.isCanceled())
            {
                return 0;
            }
            complexityValue += computeStatementComplexity(statement, nestedLevel, uniqueName, monitor);
        }
        return complexityValue;
    }

    private int computeStatementComplexity(Statement statement, int nestedLevel, String methodName,
        IProgressMonitor monitor)
    {
        int complexityValue = 0;
        if (statement instanceof LoopStatement)
        {
            complexityValue += 1 + nestedLevel;
            if (statement instanceof WhileStatement)
            {
                complexityValue += computeExpressionComplexity(((WhileStatement)statement).getPredicate(), nestedLevel,
                    methodName, monitor);
            }
            for (Statement substatement : ((LoopStatement)statement).getStatements())
            {
                if (monitor.isCanceled())
                {
                    return 0;
                }
                complexityValue += computeStatementComplexity(substatement, nestedLevel + 1, methodName, monitor);
            }
        }
        else if (statement instanceof IfStatement)
        {
            complexityValue += nestedLevel;
            IfStatement ifStatement = (IfStatement)statement;
            Conditional ifPart = ifStatement.getIfPart();
            complexityValue += computeConditionalComplexity(ifPart, nestedLevel, methodName, monitor);
            for (Conditional elseIfPart : ifStatement.getElsIfParts())
            {
                if (monitor.isCanceled())
                {
                    return 0;
                }
                complexityValue += computeConditionalComplexity(elseIfPart, nestedLevel, methodName, monitor);
            }
            EList<Statement> substatements = ifStatement.getElseStatements();
            if (!substatements.isEmpty())
            {
                complexityValue++;
                for (Statement substatement : substatements)
                {
                    if (monitor.isCanceled())
                    {
                        return 0;
                    }
                    complexityValue += computeStatementComplexity(substatement, nestedLevel + 1, methodName, monitor);
                }
            }
        }
        else if (statement instanceof TryExceptStatement)
        {
            TryExceptStatement tryExceptStatement = (TryExceptStatement)statement;
            for (Statement substatement : tryExceptStatement.getTryStatements())
            {
                if (monitor.isCanceled())
                {
                    return 0;
                }
                complexityValue += computeStatementComplexity(substatement, nestedLevel, methodName, monitor);
            }
            for (Statement substatement : tryExceptStatement.getExceptStatements())
            {
                if (monitor.isCanceled())
                {
                    return 0;
                }
                complexityValue += nestedLevel + 1;
                complexityValue += computeStatementComplexity(substatement, nestedLevel + 1, methodName, monitor);
            }
        }
        else if (statement instanceof GotoStatement)
        {
            complexityValue++;
        }
        else if (statement instanceof SimpleStatement)
        {
            SimpleStatement simpleStatement = (SimpleStatement)statement;
            complexityValue += computeExpressionComplexity(simpleStatement.getLeft(), nestedLevel, methodName, monitor);
            Expression right = simpleStatement.getRight();
            if (right != null)
            {
                complexityValue += computeExpressionComplexity(right, nestedLevel, methodName, monitor);
            }
        }

        return complexityValue;
    }

    private int computeConditionalComplexity(Conditional conditional, int nestedLevel, String methodName,
        IProgressMonitor monitor)
    {
        int complexityValue = 1;
        complexityValue += computeExpressionComplexity(conditional.getPredicate(), nestedLevel, methodName, monitor);
        for (Statement substatement : conditional.getStatements())
        {
            if (monitor.isCanceled())
            {
                return 0;
            }
            complexityValue += computeStatementComplexity(substatement, nestedLevel + 1, methodName, monitor);
        }
        return complexityValue;
    }

    private int computeExpressionComplexity(Expression expression, int nestedLevel, String methodName,
        IProgressMonitor monitor)
    {
        int complexityValue = 0;
        if (expression instanceof BinaryExpression)
        {
            BinaryExpression binaryExpression = (BinaryExpression)expression;
            BinaryOperation operation = binaryExpression.getOperation();
            if (operation == BinaryOperation.AND || operation == BinaryOperation.OR)
            {
                complexityValue++;
            }
            complexityValue +=
                computeExpressionComplexity(binaryExpression.getLeft(), nestedLevel, methodName, monitor);
            complexityValue +=
                computeExpressionComplexity(binaryExpression.getRight(), nestedLevel, methodName, monitor);
        }
        else if (expression instanceof UnaryExpression)
        {
            complexityValue += computeExpressionComplexity(((UnaryExpression)expression).getOperand(), nestedLevel,
                methodName, monitor);
        }
        else if (expression instanceof DynamicFeatureAccess)
        {
            complexityValue += computeExpressionComplexity(((DynamicFeatureAccess)expression).getSource(), nestedLevel,
                methodName, monitor);
        }
        else if (expression instanceof Invocation)
        {
            Invocation invocation = (Invocation)expression;

            FeatureAccess method = invocation.getMethodAccess();
            if (method.getName().equals(TRENARY_OPERATOR))
            {
                complexityValue += 1 + nestedLevel;
            }
            else if (method instanceof StaticFeatureAccess && method.getName().equalsIgnoreCase(methodName))
            {
                complexityValue++;
            }
            for (Expression parameter : invocation.getParams())
            {
                if (monitor.isCanceled())
                {
                    return 0;
                }
                complexityValue += computeExpressionComplexity(parameter, nestedLevel + 1, methodName, monitor);
            }
        }
        else if (expression instanceof IndexAccess)
        {
            IndexAccess indexAccess = (IndexAccess)expression;
            complexityValue += computeExpressionComplexity(indexAccess.getSource(), nestedLevel, methodName, monitor);
            complexityValue += computeExpressionComplexity(indexAccess.getIndex(), nestedLevel, methodName, monitor);
        }
        else if (expression instanceof FunctionStyleCreator)
        {
            FunctionStyleCreator creator = (FunctionStyleCreator)expression;
            complexityValue +=
                computeExpressionComplexity(creator.getTypeNameExpression(), nestedLevel, methodName, monitor);
            Expression params = creator.getParamsExpression();
            if (params != null)
            {
                complexityValue += computeExpressionComplexity(params, nestedLevel, methodName, monitor);
            }
        }
        else if (expression instanceof OperatorStyleCreator)
        {
            for (Expression parameter : ((OperatorStyleCreator)expression).getParams())
            {
                if (monitor.isCanceled())
                {
                    return 0;
                }
                complexityValue += computeExpressionComplexity(parameter, nestedLevel, methodName, monitor);
            }
        }
        return complexityValue;
    }
}
