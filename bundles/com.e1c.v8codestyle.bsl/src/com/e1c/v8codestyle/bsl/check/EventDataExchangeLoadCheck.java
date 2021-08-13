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
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.CONDITIONAL__PREDICATE;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PROCEDURE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.BinaryExpression;
import com._1c.g5.v8.dt.bsl.model.BinaryOperation;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Procedure;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Check event handlers OnWrite, BeforeWrite and BeforeDelete that has the checking of DataExchange.Load with return.
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 *
 */
public class EventDataExchangeLoadCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "data-exchange-load"; //$NON-NLS-1$

    private static final String PARAM_FINCTION_LIST = "dataExchangeLoadFunctionList"; //$NON-NLS-1$

    private static final String DEFAULT_FUNCTION_LIST = ""; //$NON-NLS-1$

    private static final String PARAM_CHECK_AT_BEGINNING = "checkAtBeginning"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_AT_BEGINNING = Boolean.toString(Boolean.FALSE);

    private static final String DOT = "."; //$NON-NLS-1$

    private static final Collection<String> DEFAULT_NAMES = Set.of("ПриЗаписи", //$NON-NLS-1$
        "OnWrite", "ПередЗаписью", "BeforeWrite", "ПередУдалением", "BeforeDelete"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    private static final Collection<String> СALL_FOR_CHECK = Set.of("DataExchange.Load", //$NON-NLS-1$
        "ОбменДанными.Загрузка"); //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.EventDataExchangeLoadCheck_title)
            .description(Messages.EventDataExchangeLoadCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PORTABILITY)
            .module()
            .checkedObjectType(PROCEDURE)
            .parameter(PARAM_CHECK_AT_BEGINNING, Boolean.class, DEFAULT_CHECK_AT_BEGINNING,
                Messages.EventDataExchangeLoadCheck_Check_at_the_beginning_of_event_handler)
            .parameter(PARAM_FINCTION_LIST, String.class, DEFAULT_FUNCTION_LIST,
                Messages.EventDataExchangeLoadCheck_Function_list_that_checks_DataExchange_Load);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof Procedure) || !((Procedure)object).isEvent()
            || !isNecessaryMethod((Procedure)object))
        {
            return;
        }

        Procedure method = (Procedure)object;

        Module module = EcoreUtil2.getContainerOfType(method, Module.class);

        if (!checkModuleType(module))
        {
            return;
        }

        List<Statement> statements = method.allStatements();
        if (statements.isEmpty())
        {
            return;
        }

        final boolean checkAtBeginning = parameters.getBoolean(PARAM_CHECK_AT_BEGINNING);

        Set<String> checkCalls = getCheckCalls(parameters);
        for (Statement methodStatement : statements)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (isDataExchangeLoadChecking(methodStatement, checkCalls))
            {
                if (methodStatement instanceof IfStatement && hasReturnStatement((IfStatement)methodStatement, monitor))
                {
                    return;
                }

                resultAceptor.addIssue(Messages.EventDataExchangeLoadCheck_No_return_in__DataExchange_Load__checking,
                    ((IfStatement)methodStatement).getIfPart(), CONDITIONAL__PREDICATE);

                return;
            }
            if (checkAtBeginning)
            {
                break;
            }
        }

        String message = MessageFormat.format(
            Messages.EventDataExchangeLoadCheck_Mandatory_checking_of__DataExchange_Load__is_absent_in_event_handler__0,
            method.getName());

        resultAceptor.addIssue(message, NAMED_ELEMENT__NAME);

    }

    private boolean isNecessaryMethod(Method method)
    {
        return DEFAULT_NAMES.contains(method.getName());
    }

    private boolean isDataExchangeLoadChecking(Statement statementMethod, Set<String> checkCalls)
    {
        if (statementMethod instanceof IfStatement)
        {
            Conditional conditional = ((IfStatement)statementMethod).getIfPart();
            Expression predicate = conditional.getPredicate();
            if (predicate instanceof DynamicFeatureAccess)
                return checkDynamicFeatureAccess((DynamicFeatureAccess)predicate, checkCalls);
            else if (predicate instanceof Invocation)
            {
                FeatureAccess expression = ((Invocation)predicate).getMethodAccess();
                if (expression instanceof DynamicFeatureAccess
                    && checkDynamicFeatureAccess((DynamicFeatureAccess)expression, checkCalls))
                    return true;
            }
            else if (predicate instanceof BinaryExpression)
            {
                Map<Expression, BinaryOperation> operand =
                    getMapOperandOperator((BinaryExpression)predicate, BinaryOperation.OR);
                for (Entry<Expression, BinaryOperation> temp : operand.entrySet())
                {
                    Expression expression = temp.getKey();
                    if (expression instanceof Invocation)
                        expression = ((Invocation)expression).getMethodAccess();
                    if (expression instanceof DynamicFeatureAccess
                        && checkDynamicFeatureAccess((DynamicFeatureAccess)expression, checkCalls))
                        return true;
                }
            }
        }

        return false;
    }

    private boolean checkModuleType(Module module)
    {
        return module.getModuleType().equals(ModuleType.OBJECT_MODULE)
            || module.getModuleType().equals(ModuleType.RECORDSET_MODULE);
    }

    private boolean hasReturnStatement(IfStatement statement, IProgressMonitor monitor)
    {
        Conditional conditional = statement.getIfPart();

        List<Statement> statementsIf = conditional.getStatements();
        for (Statement statementControlIf : statementsIf)
        {
            if (monitor.isCanceled())
            {
                return false;
            }

            if (statementControlIf instanceof ReturnStatement)
            {
                return true;
            }
        }

        return false;
    }

    private Set<String> getCheckCalls(ICheckParameters parameters)
    {
        Set<String> checkCalls = new HashSet<>();
        checkCalls.addAll(СALL_FOR_CHECK);
        String functionList = parameters.getString(PARAM_FINCTION_LIST);
        if (functionList != null && !functionList.isBlank())
        {
            String[] functions = functionList.replace("(", "").replace(")", "").replace(" ", "").split(","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            checkCalls.addAll(Set.of(functions));
        }

        return Set.copyOf(checkCalls);
    }

    private Map<Expression, BinaryOperation> getMapOperandOperator(BinaryExpression binaryExpression,
        BinaryOperation operation)
    {
        Map<Expression, BinaryOperation> mapOperatorOperand = new HashMap<>();

        Expression binaryLeft = binaryExpression.getLeft();
        BinaryOperation tempOperation = binaryExpression.getOperation();
        Expression temp = binaryExpression.getRight();

        mapOperatorOperand.put(temp, tempOperation);

        if (binaryLeft instanceof BinaryExpression)
            binaryExpression = (BinaryExpression)binaryLeft;
        else
        {
            mapOperatorOperand.put(binaryLeft, tempOperation);
            binaryExpression = null;
        }

        while (binaryExpression != null && binaryExpression.getOperation().equals(operation))
        {
            binaryLeft = binaryExpression.getLeft();
            temp = binaryExpression.getRight();
            tempOperation = binaryExpression.getOperation();
            mapOperatorOperand.put(temp, tempOperation);
            if (binaryLeft instanceof BinaryExpression)
                binaryExpression = (BinaryExpression)binaryLeft;
            else
                binaryExpression = null;
        }

        mapOperatorOperand.put(binaryLeft, tempOperation);
        return mapOperatorOperand;

    }

    private boolean checkDynamicFeatureAccess(DynamicFeatureAccess dynamicFeatureAccess, Set<String> checkCalls)
    {
        String methodForCheck = getMethodForCheck(dynamicFeatureAccess);
        return methodForCheck != null && checkCalls.contains(methodForCheck);
    }

    private String getMethodForCheck(DynamicFeatureAccess dynamicFeatureAccess)
    {
        StringBuilder builder = new StringBuilder(dynamicFeatureAccess.getName());
        builder.insert(0, DOT);
        Expression expression = dynamicFeatureAccess.getSource();
        while (expression instanceof DynamicFeatureAccess)
        {
            dynamicFeatureAccess = (DynamicFeatureAccess)expression;
            builder.insert(0, dynamicFeatureAccess.getName());
            builder.insert(0, DOT);
            expression = dynamicFeatureAccess.getSource();
        }
        if (expression instanceof StaticFeatureAccess)
        {
            StaticFeatureAccess staticFeatureAccess = (StaticFeatureAccess)expression;
            builder.insert(0, staticFeatureAccess.getName());
            return builder.toString();
        }
        return null;
    }

}
