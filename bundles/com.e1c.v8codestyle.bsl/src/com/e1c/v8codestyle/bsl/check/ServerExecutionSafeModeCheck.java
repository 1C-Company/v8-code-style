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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.EXECUTE_STATEMENT;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Block;
import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.ExecuteStatement;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check if safe mode is enabled before Execute or Eval
 *
 * @author Maxim Galios
 *
 */
public class ServerExecutionSafeModeCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "server-execution-safe-mode"; //$NON-NLS-1$

    private static final String EVAL = "Eval"; //$NON-NLS-1$
    private static final String EVAL_RU = "Вычислить"; //$NON-NLS-1$

    private static final String SAFE_MODE = "SetSafeMode"; //$NON-NLS-1$
    private static final String SAFE_MODE_RU = "УстановитьБезопасныйРежим"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ServerExecutionSafeModeCheck_title)
            .description(Messages.ServerExecutionSafeModeCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.SECURITY)
            .extension(new StandardCheckExtension(770, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION)
            .checkedObjectType(EXECUTE_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        EObject eObject = (EObject)object;
        if (!isServerEnv(eObject) || (!isExecute(eObject) && !isEval(eObject)))
        {
            return;
        }

        if (!isSafeModeEnabledBeforeExecution(eObject, monitor))
        {
            if (isExecute(eObject))
            {
                resultAceptor.addIssue(Messages.ServerExecutionSafeModeCheck_execute_issue, eObject);
            }
            else if (isEval(eObject))
            {
                resultAceptor.addIssue(Messages.ServerExecutionSafeModeCheck_eval_issue,
                    ((Invocation)eObject).getMethodAccess());
            }
        }
    }

    private boolean isServerEnv(EObject eObject)
    {
        Environmental environmental = EcoreUtil2.getContainerOfType(eObject, Environmental.class);
        Environments environments = environmental.environments();
        return environments.contains(Environment.SERVER);
    }

    private boolean isSafeModeEnabledBeforeExecution(EObject eObject, IProgressMonitor monitor)
    {
        EObject container = eObject.eContainer();

        while (container != null)
        {
            if (monitor.isCanceled())
            {
                return true;
            }
            Optional<Boolean> isSafeModeEnabledForContainer = isSafeModeEnabledForContainer(container, eObject);
            if (isSafeModeEnabledForContainer.isPresent())
            {
                return isSafeModeEnabledForContainer.get();
            }
            container = container.eContainer();
        }
        return false;
    }

    private Optional<Boolean> isSafeModeEnabledForContainer(EObject container, EObject eObject)
    {
        if (container instanceof IfStatement)
        {
            return isSafeModeEnabledForIfStatement((IfStatement)container, eObject);
        }
        else if (container instanceof LoopStatement)
        {
            return isSafeModeEnabledForLoopStatement((LoopStatement)container, eObject);
        }
        else if (container instanceof TryExceptStatement)
        {
            return isSafeModeEnabledForTryExceptStatement((TryExceptStatement)container, eObject);
        }
        else if (container instanceof Block)
        {
            return isSafeModeEnabledForBlock((Block)container, eObject);
        }

        return Optional.empty();
    }

    private Optional<Boolean> isSafeModeEnabledForIfStatement(IfStatement ifStatement, EObject eObject)
    {
        List<Statement> ifStatements = ifStatement.getIfPart().getStatements();
        Optional<Boolean> ifStatementSafeModeEnabled = isSafeModeEnabledInStatementList(ifStatements, eObject);

        if (ifStatementSafeModeEnabled.isPresent())
        {
            return ifStatementSafeModeEnabled;
        }

        List<Conditional> elseIfParts = ifStatement.getElsIfParts();
        for (Conditional part : elseIfParts)
        {
            List<Statement> elseIfStatements = part.getStatements();
            Optional<Boolean> elseIfStatementSafeModeEnabled =
                isSafeModeEnabledInStatementList(elseIfStatements, eObject);

            if (elseIfStatementSafeModeEnabled.isPresent())
            {
                return elseIfStatementSafeModeEnabled;
            }
        }

        List<Statement> elseStatements = ifStatement.getElseStatements();
        return isSafeModeEnabledInStatementList(elseStatements, eObject);
    }

    private Optional<Boolean> isSafeModeEnabledForLoopStatement(LoopStatement loopStatement, EObject eObject)
    {
        List<Statement> loopStatements = loopStatement.getStatements();
        return isSafeModeEnabledInStatementList(loopStatements, eObject);
    }

    private Optional<Boolean> isSafeModeEnabledForTryExceptStatement(TryExceptStatement tryExceptStatement,
        EObject eObject)
    {
        List<Statement> tryStatements = tryExceptStatement.getTryStatements();
        Optional<Boolean> tryStatementSafeModeEnabled = isSafeModeEnabledInStatementList(tryStatements, eObject);

        if (tryStatementSafeModeEnabled.isPresent())
        {
            return tryStatementSafeModeEnabled;
        }

        List<Statement> exceptStatements = tryExceptStatement.getExceptStatements();
        return isSafeModeEnabledInStatementList(exceptStatements, eObject);
    }

    private Optional<Boolean> isSafeModeEnabledForBlock(Block block, EObject eObject)
    {
        List<Statement> statements = block.allStatements();
        return isSafeModeEnabledInStatementList(statements, eObject);
    }

    private Optional<Boolean> isSafeModeEnabledInStatementList(List<Statement> statements, EObject eObject)
    {
        int executeIndex = findExecuteStatementIndex(statements, eObject);
        if (executeIndex == -1)
        {
            return Optional.empty();
        }

        for (int i = executeIndex - 1; i >= 0; i--)
        {
            Statement statement = statements.get(i);
            Optional<Boolean> safeModeEnabled = isSafeModeEnabled(statement);
            if (safeModeEnabled.isPresent())
            {
                return safeModeEnabled;
            }
        }
        return Optional.empty();
    }

    private Optional<Boolean> isSafeModeEnabled(Statement statement)
    {
        if (statement instanceof SimpleStatement && isSafeModeMethod(statement))
        {
            return Optional.of(isSafeModeEnabledStatement(statement));
        }
        else if (statement instanceof IfStatement)
        {
            List<Optional<Boolean>> safeModeStatementResults = new ArrayList<>();
            IfStatement ifStatement = (IfStatement)statement;

            List<Statement> ifStatements = ifStatement.getIfPart().getStatements();
            safeModeStatementResults.add(safeModeStatementResult(ifStatements));

            List<Conditional> elseIfParts = ifStatement.getElsIfParts();
            for (Conditional part : elseIfParts)
            {
                List<Statement> elseIfStatements = part.getStatements();
                safeModeStatementResults.add(safeModeStatementResult(elseIfStatements));
            }

            List<Statement> elseStatements = ifStatement.getElseStatements();
            safeModeStatementResults.add(safeModeStatementResult(elseStatements));

            return validateSafeModeStatements(safeModeStatementResults);
        }
        else if (statement instanceof LoopStatement)
        {
            LoopStatement loopStatement = (LoopStatement)statement;
            List<Statement> loopStatements = loopStatement.getStatements();
            return safeModeStatementResult(loopStatements);
        }
        else if (statement instanceof TryExceptStatement)
        {
            List<Optional<Boolean>> safeModeStatementResults = new ArrayList<>();
            TryExceptStatement tryExceptStatement = (TryExceptStatement)statement;
            List<Statement> tryStatements = tryExceptStatement.getTryStatements();
            safeModeStatementResults.add(safeModeStatementResult(tryStatements));

            List<Statement> exceptStatements = tryExceptStatement.getExceptStatements();
            safeModeStatementResults.add(safeModeStatementResult(exceptStatements));

            return validateSafeModeStatements(safeModeStatementResults);
        }
        return Optional.empty();
    }

    private Optional<Boolean> safeModeStatementResult(List<Statement> statements)
    {
        for (int i = statements.size() - 1; i >= 0; i--)
        {
            Statement statement = statements.get(i);
            Optional<Boolean> res = isSafeModeEnabled(statement);
            if (res.isPresent())
            {
                return res;
            }
        }
        return Optional.empty();
    }

    private Optional<Boolean> validateSafeModeStatements(List<Optional<Boolean>> safeModeStatements)
    {
        if (safeModeStatements.stream().allMatch(Optional::isEmpty))
        {
            return Optional.empty();
        }
        if (safeModeStatements.stream().filter(Optional::isPresent).anyMatch(s -> !s.get()))
        {
            return Optional.of(false);
        }
        if (safeModeStatements.stream().allMatch(s -> s.isPresent() && s.get()))
        {
            return Optional.of(true);
        }
        return Optional.empty();
    }

    private boolean isSafeModeMethod(Statement statement)
    {
        if (!(statement instanceof SimpleStatement))
        {
            return false;
        }

        SimpleStatement simpleStatement = (SimpleStatement)statement;
        Expression leftExpression = simpleStatement.getLeft();
        if (!(leftExpression instanceof Invocation))
        {
            return false;
        }

        Invocation invocation = (Invocation)leftExpression;
        String name = invocation.getMethodAccess().getName();
        List<Expression> params = invocation.getParams();

        return (SAFE_MODE_RU.equalsIgnoreCase(name) || SAFE_MODE.equalsIgnoreCase(name)) && params.size() == 1
            && params.get(0) instanceof BooleanLiteral;
    }

    private int findExecuteStatementIndex(List<Statement> statements, EObject eObject)
    {
        if (eObject instanceof ExecuteStatement)
        {
            int index = statements.indexOf(eObject);
            if (index != -1)
            {
                return index;
            }
        }

        Statement statement = EcoreUtil2.getContainerOfType(eObject, Statement.class);

        while (statement != null)
        {
            int statementIndex = statements.indexOf(statement);
            if (statementIndex != -1)
            {
                return statementIndex;
            }
            statement = EcoreUtil2.getContainerOfType(statement.eContainer(), Statement.class);
        }

        return -1;
    }

    private boolean isSafeModeEnabledStatement(Statement statement)
    {
        if (statement == null)
        {
            return false;
        }
        SimpleStatement simpleStatement = (SimpleStatement)statement;
        Invocation invocation = (Invocation)simpleStatement.getLeft();

        List<Expression> params = invocation.getParams();
        return ((BooleanLiteral)params.get(0)).isIsTrue();
    }

    private boolean isExecute(EObject eObject)
    {
        return eObject instanceof ExecuteStatement;
    }

    private boolean isEval(EObject eObject)
    {
        if (eObject instanceof Invocation)
        {
            String name = ((Invocation)eObject).getMethodAccess().getName();
            return EVAL_RU.equalsIgnoreCase(name) || EVAL.equalsIgnoreCase(name);
        }
        return false;
    }
}
