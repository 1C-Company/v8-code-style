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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks query result is empty.
 *
 *  @author Ivan Sergeev
 */
public class EmptyQueryResultCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "empty-query-result"; //$NON-NLS-1$

    private static final String NEXT = "Next"; //$NON-NLS-1$

    private static final String NEXT_RU = "Следующий"; //$NON-NLS-1$

    private static final String SELECT = "Select"; //$NON-NLS-1$

    private static final String SELECT_RU = "Выбрать"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.EmptyQueryResultCheck_Title)
            .description(Messages.EmptyQueryResultCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(438, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(SIMPLE_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        SimpleStatement statement = (SimpleStatement)object;
        if (statement.getLeft() instanceof StaticFeatureAccess left)
        {
            if ("Запрос".equalsIgnoreCase(left.getName()) || "Query".equalsIgnoreCase(left.getName())) //$NON-NLS-1$ //$NON-NLS-2$
            {
                Method method = EcoreUtil2.getContainerOfType(statement, Method.class);
                List<Statement> statements = method.allStatements();
                int index = indexStatement(statements, statement);
                if (index == -1)
                {
                    return;
                }
                String nameSelect = left.getName();
                if (nameSelect == null)
                {
                    return;
                }
                SimpleStatement selectStatement =
                    (SimpleStatement)searchSelectStatement(statements.subList(index, statements.size() - 1),
                        nameSelect);
                if (selectStatement != null)
                {
                    int indexSelectStatement = indexStatement(statements, selectStatement);
                    if (indexSelectStatement == -1)
                    {
                        return;
                    }
                    StaticFeatureAccess sfa = (StaticFeatureAccess)selectStatement.getLeft();
                    String sfaName = sfa.getName();
                    if (sfaName == null)
                    {
                        return;
                    }
                    Statement findIf = searchCheckQuoryResult(
                        statements.subList(indexSelectStatement, statements.size() - 1), sfaName);
                    if (findIf instanceof IfStatement)
                    {
                        resultAceptor.addIssue(Messages.EmptyQueryResultCheck_Issue, findIf);
                    }
                }
                else if (selectStatement == null)
                {
                    String name = null;
                    Statement findIf = searchCheckQuoryResult(statements.subList(index, statements.size() - 1), name);
                    if (findIf != null)
                    {
                        resultAceptor.addIssue(Messages.EmptyQueryResultCheck_Issue, findIf);
                    }
                }
            }
        }
    }

    private int indexStatement(List<Statement> statements, Statement searchStatement)
    {
        for (int i = 0; i < statements.size() - 1; i++)
        {
            if (statements.get(i) == searchStatement)
            {
                return i;
            }
        }
        return -1;
    }

    private Statement searchSelectStatement(List<Statement> statements, String name)
    {
        for (Statement statement : statements)
        {
            if (statement instanceof SimpleStatement simpState)
            {
                if (simpState.getRight() instanceof Invocation inv)
                {
                    String textInv = NodeModelUtils.findActualNodeFor(inv).getText();
                    String nameInv = inv.getMethodAccess().getName();
                    if (SELECT_RU.equalsIgnoreCase(nameInv) || SELECT.equalsIgnoreCase(nameInv))
                    {
                        if (textInv.contains(name))
                        {
                            if (simpState.getLeft() instanceof StaticFeatureAccess)
                            {
                                return statement;
                            }
                        }
                    }
                }
            }
            else if (statement instanceof IfStatement ifStatement)
            {
                Statement stat = searchIfStatement(ifStatement, name);
                if (stat != null)
                {
                    return stat;
                }
            }
            else if (statement instanceof ForStatement forStatement)
            {
                Statement stat = searchForStatement(forStatement, name);
                if (stat != null)
                {
                    return stat;
                }
            }
        }
        return null;
    }

    private Statement searchCheckQuoryResult(List<Statement> statements, String name)
    {
        for (Statement statement : statements)
        {
            if (statement instanceof IfStatement ifStatement)
            {
                if (name == null)
                {
                    if (ifStatement.getIfPart().getPredicate() instanceof Invocation inv)
                    {
                        String text = NodeModelUtils.findActualNodeFor(inv).getText().toLowerCase();
                        if ((text.contains(SELECT_RU.toLowerCase()) || text.contains(SELECT.toLowerCase()))
                            && (NEXT_RU.equalsIgnoreCase(inv.getMethodAccess().getName())
                                || NEXT.equalsIgnoreCase(inv.getMethodAccess().getName())))
                        {
                            List<Statement> ifStatements = ifStatement.getIfPart().getStatements();
                            for (Statement ifStatementIn : ifStatements)
                            {
                                if (ifStatementIn instanceof EmptyStatement)
                                {
                                    continue;
                                }
                                else if (ifStatementIn instanceof ReturnStatement returnStatement)
                                {
                                    if (returnStatement.getExpression() instanceof BooleanLiteral)
                                    {
                                        return ifStatement;
                                    }
                                }
                                else if (!(ifStatementIn instanceof ReturnStatement))
                                {
                                    return null;
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (ifStatement.getIfPart().getPredicate() instanceof Invocation inv
                        && NodeModelUtils.findActualNodeFor(inv).getText().toLowerCase().contains(name.toLowerCase())
                        && (NEXT_RU.equalsIgnoreCase(inv.getMethodAccess().getName())
                            || NEXT.equalsIgnoreCase(inv.getMethodAccess().getName())))
                    {
                        List<Statement> ifStatements = ifStatement.getIfPart().getStatements();
                        for (Statement ifStatementIn : ifStatements)
                        {
                            if (ifStatementIn instanceof EmptyStatement)
                            {
                                continue;
                            }
                            else if (ifStatementIn instanceof ReturnStatement returnStatement)
                            {
                                if (returnStatement.getExpression() instanceof BooleanLiteral)
                                {
                                    return ifStatement;
                                }
                            }
                            else if (!(ifStatementIn instanceof ReturnStatement))
                            {
                                return null;
                            }
                        }
                    }
                    Statement stat = searchIfStatement(ifStatement, name);
                    if (stat != null)
                    {
                        return stat;
                    }
                }
            }
            else if (statement instanceof ForStatement forStatement)
            {
                if (name == null)
                {
                    return null;
                }
                Statement stat = searchForStatement(forStatement, name);
                if (stat != null)
                {
                    return stat;
                }
            }
            else if (statement instanceof TryExceptStatement tryExceptStatement)
            {
                if (name == null)
                {
                    return null;
                }
                tryExceptStatement.getTryStatements();
            }
        }
        return null;
    }

    private Statement searchIfStatement(IfStatement ifStatement, String name)
    {
        List<Statement> ifStatements = ifStatement.getIfPart().getStatements();
        Statement stat = searchSelectStatement(ifStatements, name);
        if (stat != null)
        {
            return stat;
        }
        List<Statement> elseStatements = ifStatement.getElseStatements();
        stat = searchSelectStatement(elseStatements, name);
        if (stat != null)
        {
            return stat;
        }
        List<Conditional> elseIfParts = ifStatement.getElsIfParts();
        for (Conditional conditional : elseIfParts)
        {
            List<Statement> statementsElsIf = conditional.getStatements();
            stat = searchSelectStatement(statementsElsIf, name);
            if (stat != null)
            {
                return stat;
            }
        }
        return null;
    }

    private Statement searchForStatement(ForStatement forStatement, String name)
    {
        List<Statement> forStatements = forStatement.getStatements();
        Statement stat = searchSelectStatement(forStatements, name);
        if (stat != null)
        {
            return stat;
        }
        return null;
    }
}
