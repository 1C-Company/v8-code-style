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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.ExecuteStatement;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
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

    private static final Collection<String> EVAL = Set.of("Eval", "Вычислить"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final Collection<String> SERVER_PRAGMAS = Set.of("НаСервере", "AtServer", //$NON-NLS-1$ //$NON-NLS-2$
        "НаСервереБезКонтекста", "AtServerNoContext", //$NON-NLS-1$ //$NON-NLS-2$
        "НаКлиентеНаСервереБезКонтекста", "AtClientAtServerNoContext"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final Collection<String> SAFE_MODE_INVOCATIONS = Set.of("УстановитьБезопасныйРежим", "SetSafeMode"); //$NON-NLS-1$ //$NON-NLS-2$

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
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION)
            .checkedObjectType(EXECUTE_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        EObject eObject = (EObject)object;
        if (monitor.isCanceled() || (!isExecute(eObject) && !isEval(eObject)))
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(eObject, Module.class);
        if (((module.getModuleType() == ModuleType.COMMON_MODULE && isServerCallModule(module, monitor))
            || hasServerPragma(eObject, monitor)) && !isSafeModeEnabled(eObject))
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

    private boolean isServerCallModule(Module module, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return false;
        }

        CommonModule commonModule = (CommonModule)module.getOwner();
        return !monitor.isCanceled() && commonModule.isServerCall() && commonModule.isServer();
    }

    private boolean hasServerPragma(EObject eObject, IProgressMonitor monitor)
    {
        Method surroundingMethod = EcoreUtil2.getContainerOfType(eObject, Method.class);
        if (monitor.isCanceled() || surroundingMethod == null)
        {
            return false;
        }
        List<Pragma> pragmas = surroundingMethod.getPragmas();
        if (monitor.isCanceled() || pragmas.isEmpty())
        {
            return false;
        }
        return pragmas.stream().anyMatch(pragma -> SERVER_PRAGMAS.contains(pragma.getSymbol()));
    }

    private boolean isSafeModeEnabled(EObject eObject)
    {
        Method surroundingMethod = EcoreUtil2.getContainerOfType(eObject, Method.class);
        if (surroundingMethod == null)
        {
            return false;
        }
        List<Statement> statements = surroundingMethod.allStatements();
        List<Statement> safeModeStatements =
            statements.stream().filter(this::isSafeModeMethod).collect(Collectors.toList());

        if (safeModeStatements.isEmpty())
        {
            return false;
        }

        List<Integer> safeModeStatementIndexes =
            safeModeStatements.stream().map(statements::indexOf).collect(Collectors.toList());
        int executeStatementIndex = findExecuteStatementIndex(statements, eObject);
        if (executeStatementIndex == -1)
        {
            return false;
        }

        int lastIndexBeforeExecute = findLastSafeModeStatement(safeModeStatementIndexes, executeStatementIndex);
        if (lastIndexBeforeExecute == -1)
        {
            return false;
        }
        Statement lastSafeModeStatement = statements.get(lastIndexBeforeExecute);
        return isSafeModeEnabledInLastStatement(lastSafeModeStatement);
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

        return SAFE_MODE_INVOCATIONS.contains(name) && params.size() == 1 && params.get(0) instanceof BooleanLiteral;
    }

    private int findExecuteStatementIndex(List<Statement> statements, EObject eObject)
    {
        return eObject instanceof Invocation
            ? statements.indexOf(EcoreUtil2.getContainerOfType(eObject, Statement.class)) : statements.indexOf(eObject);
    }

    private int findLastSafeModeStatement(List<Integer> safeStatementIndexes, int executeIndex)
    {
        int lastIndexBeforeExecute = -1;

        for (int index : safeStatementIndexes)
        {
            if (index >= executeIndex)
            {
                break;
            }
            lastIndexBeforeExecute = index;
        }
        return lastIndexBeforeExecute;
    }

    private boolean isSafeModeEnabledInLastStatement(Statement statement)
    {
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
        return eObject instanceof Invocation && EVAL.contains(((Invocation)eObject).getMethodAccess().getName());
    }
}
