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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.IF_STATEMENT;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.BinaryExpression;
import com._1c.g5.v8.dt.bsl.model.BinaryOperation;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.WhileStatement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks definition type variable.
 *
 *  @author Ivan Sergeev
 */
public class DefinitionTypeVariableCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "definition-type-variable"; //$NON-NLS-1$

    private static final String TYPE = "Type"; //$NON-NLS-1$

    private static final String TYPE_RU = "Тип"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DefinitionTypeVariableCheck_Title)
            .description(Messages.DefinitionTypeVariableCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(442, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(IF_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof IfStatement statement)
        {
            if (statement.getIfPart().getPredicate() instanceof BinaryExpression binaryExp)
            {
                if (binaryExp.getOperation().equals(BinaryOperation.EQ)
                    || binaryExp.getOperation().equals(BinaryOperation.NE))
                {
                    Expression expressionLeft = binaryExp.getLeft();
                    Expression expressionRight = binaryExp.getRight();
                    if (NodeModelUtils.findActualNodeFor(expressionLeft)
                        .getText()
                        .toLowerCase()
                        .contains("метаданные()") //$NON-NLS-1$
                        && !NodeModelUtils.findActualNodeFor(expressionRight)
                            .getText()
                            .toLowerCase()
                            .contains("метаданны")) //$NON-NLS-1$
                    {
                        if (binaryExp.getRight() instanceof StaticFeatureAccess sfa)
                        {
                            String sfaName = sfa.getName();
                            Method method = EcoreUtil2.getContainerOfType(statement, Method.class);
                            List<Statement> statements = method.allStatements();
                            if (!checkSfa(sfaName, statements))
                            {
                                resultAcceptor.addIssue(Messages.DefinitionTypeVariableCheck_Issue);
                            }
                        }
                        else if (binaryExp.getRight() instanceof Invocation inv)
                        {
                            if (!inv.getMethodAccess().getName().equalsIgnoreCase(TYPE_RU)
                                || !inv.getMethodAccess().getName().equalsIgnoreCase(TYPE))
                            {
                                resultAcceptor.addIssue(Messages.DefinitionTypeVariableCheck_Issue);
                            }
                        }
                        else if (binaryExp.getRight() instanceof StringLiteral)
                        {
                            resultAcceptor.addIssue(Messages.DefinitionTypeVariableCheck_Issue);
                        }
                    }
                }
            }
        }
    }

    private boolean checkSfa(String name, List<Statement> statements)
    {
        for (Statement statement : statements)
        {
            if (statement instanceof SimpleStatement simpState)
            {
                if (checkSimpleState(simpState, name))
                {
                    return true;
                }
            }
            else if (statement instanceof IfStatement ifStatement)
            {
                List<Statement> ifStatements = ifStatement.getIfPart().getStatements();
                if (checkSfa(name, ifStatements))
                {
                    return true;
                }
                List<Statement> elseStatements = ifStatement.getElseStatements();
                if (checkSfa(name, elseStatements))
                {
                    return true;
                }
                List<Conditional> elseIfParts = ifStatement.getElsIfParts();
                for (Conditional conditional : elseIfParts)
                {
                    List<Statement> statementsElsIf = conditional.getStatements();
                    if (checkSfa(name, statementsElsIf))
                    {
                        return true;
                    }
                }
            }
            else if (statement instanceof ForStatement forStatement)
            {
                List<Statement> forStatements = forStatement.getStatements();
                if (checkSfa(name, forStatements))
                {
                    return true;
                }
            }
            else if (statement instanceof WhileStatement whileStatement)
            {
                List<Statement> forStatements = whileStatement.getStatements();
                if (checkSfa(name, forStatements))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSimpleState(SimpleStatement statement, String name)
    {
        if (statement.getLeft() instanceof StaticFeatureAccess left)
        {
            if (name.equalsIgnoreCase(left.getName()))
            {
                if (statement.getRight() instanceof Invocation invocation)
                {
                    String nameInv = invocation.getMethodAccess().getName();
                    if (TYPE_RU.equalsIgnoreCase(nameInv) || TYPE.equalsIgnoreCase(nameInv))
                    {
                        return true;
                    }
                    else if (!invocation.getParams().isEmpty())
                    {
                        List<Expression> params = invocation.getParams();
                        for (Expression param : params)
                        {
                            if (param instanceof Invocation inv)
                            {
                                String invName = inv.getMethodAccess().getName();
                                if (TYPE_RU.equalsIgnoreCase(invName) || TYPE.equalsIgnoreCase(invName))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
