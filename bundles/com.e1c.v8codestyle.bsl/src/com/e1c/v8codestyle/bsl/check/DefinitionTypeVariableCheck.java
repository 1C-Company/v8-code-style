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
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.BinaryExpression;
import com._1c.g5.v8.dt.bsl.model.BinaryOperation;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
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
 * Checks definition type variable.
 *
 * @author Ivan Sergeev
 */
public class DefinitionTypeVariableCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "definition-type-variable"; //$NON-NLS-1$

    private static final String TYPE = "Type"; //$NON-NLS-1$

    private static final String TYPE_RU = "Тип"; //$NON-NLS-1$

    private static final String METADATA_LOWER = "метаданны"; //$NON-NLS-1$

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
                if (checkBinaryExpression(binaryExp, statement))
                {
                    addIssue(resultAcceptor, statement);
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
            else if (statement instanceof LoopStatement loopStatement)
            {
                List<Statement> forStatements = loopStatement.getStatements();
                if (checkSfa(name, forStatements))
                {
                    return true;
                }
            }
            else if (statement instanceof TryExceptStatement tryStatement)
            {
                List<Statement> tryStatementStatements = tryStatement.getTryStatements();
                if (checkSfa(name, tryStatementStatements))
                {
                    return true;
                }
                List<Statement> exceptStatement = tryStatement.getExceptStatements();
                if (checkSfa(name, exceptStatement))
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
                    if (isTypeMethod(invocation))
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
                                if (isTypeMethod(inv))
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

    private boolean checkBinaryExpression(BinaryExpression binaryExp, Statement statement)
    {
        if (binaryExp.getOperation().equals(BinaryOperation.EQ) || binaryExp.getOperation().equals(BinaryOperation.NE))
        {
            Expression expressionLeft = binaryExp.getLeft();
            Expression expressionRight = binaryExp.getRight();
            if (expressionLeft instanceof DynamicFeatureAccess dynamicFeatureAccess
                && dynamicFeatureAccess.getSource() instanceof Invocation invocation)
            {
                ICompositeNode rightNode = NodeModelUtils.findActualNodeFor(expressionRight);
                if (checkName(invocation) && rightNode != null
                    && !rightNode.getText().toLowerCase().contains(METADATA_LOWER))
                {
                    if (binaryExp.getRight() instanceof StaticFeatureAccess sfa)
                    {
                        String sfaName = sfa.getName();
                        Method method = EcoreUtil2.getContainerOfType(statement, Method.class);
                        if (method == null)
                        {
                            return false;
                        }
                        List<Statement> statements = method.allStatements();
                        if (!checkSfa(sfaName, statements))
                        {
                            return true;
                        }
                    }
                    else if (binaryExp.getRight() instanceof Invocation inv)
                    {
                        if (!isTypeMethod(inv))
                        {
                            return true;
                        }
                    }
                    else if (binaryExp.getRight() instanceof StringLiteral)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkName(Invocation invocation)
    {
        String name = invocation.getMethodAccess().getName();
        if ("Метаданные".equalsIgnoreCase(name) || "Metadata".equalsIgnoreCase(name)) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return true;
        }
        return false;
    }

    private boolean isTypeMethod(Invocation invocation)
    {
        String name = invocation.getMethodAccess().getName();
        return TYPE_RU.equalsIgnoreCase(name) || TYPE.equalsIgnoreCase(name);
    }

    private void addIssue(ResultAcceptor resultAcceptor, Statement statement)
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
            Issue issue = new BslDirectLocationIssue(Messages.DefinitionTypeVariableCheck_Issue, directLocation);
            resultAcceptor.addIssue(issue);
        }
    }
}
