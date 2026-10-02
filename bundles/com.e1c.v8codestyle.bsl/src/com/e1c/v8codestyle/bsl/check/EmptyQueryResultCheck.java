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
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com.e1c.g5.v8.dt.check.BslDirectLocationIssue;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.DirectLocation;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.Issue;
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
                int index = statements.indexOf(statement);
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
                    (SimpleStatement)searchSelectStatement(statements.subList(index, statements.size()),
                        nameSelect);
                if (selectStatement != null)
                {
                    int indexSelectStatement = statements.indexOf(selectStatement);
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
                    Statement findIf = searchCheckQueryResult(
                        statements.subList(indexSelectStatement, statements.size()), sfaName);
                    if (findIf instanceof IfStatement ifStatement)
                    {
                        addIssue(resultAceptor, ifStatement);
                    }
                }
                else if (selectStatement == null)
                {
                    String name = null;
                    Statement findIf = searchCheckQueryResult(statements.subList(index, statements.size()), name);
                    if (findIf instanceof IfStatement ifStatement)
                    {
                        addIssue(resultAceptor, ifStatement);
                    }
                }
            }
        }
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

    private Statement searchCheckQueryResult(List<Statement> statements, String name)
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
            else if (statement instanceof LoopStatement forStatement)
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
                List<Statement> tryStatements = tryExceptStatement.getTryStatements();
                Statement stat = searchSelectStatement(tryStatements, name);
                if (stat != null)
                {
                    return stat;
                }
                else if (stat == null)
                {
                    Statement tryStatment = searchCheckQueryResult(tryStatements, name);
                    if (tryStatment != null)
                    {
                        return tryStatment;
                    }
                }
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
        else if (stat == null)
        {
            Statement ifStatment = searchCheckQueryResult(ifStatements, name);
            if (ifStatment != null)
            {
                return ifStatment;
            }
        }
        List<Statement> elseStatements = ifStatement.getElseStatements();
        stat = searchSelectStatement(elseStatements, name);
        if (stat != null)
        {
            return stat;
        }
        else if (stat == null)
        {
            Statement elseStatment = searchCheckQueryResult(elseStatements, name);
            if (elseStatment != null)
            {
                return elseStatment;
            }
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
            else if (stat == null)
            {
                Statement elseIfStatment = searchCheckQueryResult(statementsElsIf, name);
                if (elseIfStatment != null)
                {
                    return elseIfStatment;
                }
            }
        }
        return null;
    }

    private Statement searchForStatement(LoopStatement loopStatement, String name)
    {
        List<Statement> forStatements = loopStatement.getStatements();
        Statement stat = searchSelectStatement(forStatements, name);
        if (stat != null)
        {
            return stat;
        }
        else if (stat == null)
        {
            Statement elseIfStatment = searchCheckQueryResult(forStatements, name);
            if (elseIfStatment != null)
            {
                return elseIfStatment;
            }
        }
        return null;
    }

    private void addIssue(ResultAcceptor resultAceptor, Statement statement)
    {
        ICompositeNode node = NodeModelUtils.findActualNodeFor(statement);
        if (node == null)
        {
            return;
        }
        String nodeText = node.getText();
        int indexThen = nodeText.toLowerCase().indexOf("then"); //$NON-NLS-1$
        int indexThenRu = nodeText.toLowerCase().indexOf("тогда"); //$NON-NLS-1$
        String firstLine = null;
        if (indexThenRu != -1)
        {
            firstLine = nodeText.substring(0, indexThenRu + 5);
            firstLine = firstLine.substring(firstLine.toLowerCase().indexOf("если"), firstLine.length()); //$NON-NLS-1$
        }
        else if (indexThen != -1)
        {
            firstLine = nodeText.substring(0, indexThen + 4);
            firstLine = firstLine.substring(firstLine.toLowerCase().indexOf("if"), firstLine.length()); //$NON-NLS-1$
        }
        if (firstLine != null)
        {
            DirectLocation directLocation =
                new DirectLocation(node.getOffset(), firstLine.length(), node.getStartLine(), statement);
            Issue issue = new BslDirectLocationIssue(Messages.EmptyQueryResultCheck_Issue, directLocation);
            resultAceptor.addIssue(issue);
        }
    }
}
