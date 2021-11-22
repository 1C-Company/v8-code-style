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

import static com._1c.g5.v8.dt.bsl.common.Symbols.ANNOTATION_SYMBOLS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.CONDITIONAL__PREDICATE;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PROCEDURE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

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
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.bsl.model.Procedure;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
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

    private static final String PARAM_FUNCTION_LIST = "dataExchangeLoadFunctionList"; //$NON-NLS-1$

    private static final String DEFAULT_FUNCTION_LIST = ""; //$NON-NLS-1$

    private static final String PARAM_CHECK_AT_BEGINNING = "checkAtBeginning"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_AT_BEGINNING = Boolean.toString(Boolean.FALSE);

    private static final String DOT = "."; //$NON-NLS-1$

    private static final Collection<String> DEFAULT_NAMES;

    static
    {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("ПриЗаписи"); //$NON-NLS-1$
        names.add("OnWrite"); //$NON-NLS-1$
        names.add("ПередЗаписью"); //$NON-NLS-1$
        names.add("BeforeWrite"); //$NON-NLS-1$
        names.add("ПередУдалением"); //$NON-NLS-1$
        names.add("BeforeDelete"); //$NON-NLS-1$
        DEFAULT_NAMES = names;
    }
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
            .parameter(PARAM_FUNCTION_LIST, String.class, DEFAULT_FUNCTION_LIST,
                Messages.EventDataExchangeLoadCheck_Function_list_that_checks_DataExchange_Load);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Procedure procedure = (Procedure)object;

        if (!isNecessaryEventHandler(procedure))
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(procedure, Module.class);

        if (!checkModuleType(module))
        {
            return;
        }

        List<Statement> statements = procedure.allStatements();
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

            if (methodStatement instanceof IfStatement
                && isDataExchangeLoadChecking((IfStatement)methodStatement, checkCalls))
            {
                if (hasReturnStatement((IfStatement)methodStatement, monitor))
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
            Messages.EventDataExchangeLoadCheck_Mandatory_checking_of_DataExchangeLoad_is_absent_in_event_handler_0,
            procedure.getName());

        resultAceptor.addIssue(message, NAMED_ELEMENT__NAME);

    }

    private boolean isNecessaryEventHandler(Procedure procedure)
    {
        if (!procedure.isEvent())
        {
            return false;
        }

        for (Pragma pragma : procedure.getPragmas())
        {
            if (ANNOTATION_SYMBOLS.contains(new CaseInsensitiveString(pragma.getSymbol()))
                && StringUtils.isNotEmpty(pragma.getValue())
                && DEFAULT_NAMES.contains(pragma.getValue().replace("\"", ""))) //$NON-NLS-1$ //$NON-NLS-2$
            {
                return true;
            }
        }
        return DEFAULT_NAMES.contains(procedure.getName());
    }

    private boolean isDataExchangeLoadChecking(IfStatement statementMethod, Set<String> checkCalls)
    {
        Conditional conditional = statementMethod.getIfPart();
        Expression predicate = conditional.getPredicate();
        if (predicate instanceof DynamicFeatureAccess)
        {
            return checkDynamicFeatureAccess((DynamicFeatureAccess)predicate, checkCalls);
        }
        else if (predicate instanceof Invocation)
        {
            FeatureAccess expression = ((Invocation)predicate).getMethodAccess();
            if (expression instanceof DynamicFeatureAccess
                && checkDynamicFeatureAccess((DynamicFeatureAccess)expression, checkCalls))
            {
                return true;
            }
        }
        else if (predicate instanceof BinaryExpression)
        {
            Map<Expression, BinaryOperation> operand =
                getMapOperandOperator((BinaryExpression)predicate, BinaryOperation.OR);
            for (Entry<Expression, BinaryOperation> temp : operand.entrySet())
            {
                Expression expression = temp.getKey();
                if (expression instanceof Invocation)
                {
                    expression = ((Invocation)expression).getMethodAccess();
                }
                if (expression instanceof DynamicFeatureAccess
                    && checkDynamicFeatureAccess((DynamicFeatureAccess)expression, checkCalls))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkModuleType(Module module)
    {
        return module.getModuleType() == ModuleType.OBJECT_MODULE
            || module.getModuleType() == ModuleType.RECORDSET_MODULE;
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
        Set<String> checkCalls = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        checkCalls.addAll(СALL_FOR_CHECK);
        String functionList = parameters.getString(PARAM_FUNCTION_LIST);
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

        BinaryExpression currentExpression = null;
        if (binaryLeft instanceof BinaryExpression)
        {
            currentExpression = (BinaryExpression)binaryLeft;
        }
        else
        {
            mapOperatorOperand.put(binaryLeft, tempOperation);
            currentExpression = null;
        }

        while (currentExpression != null && currentExpression.getOperation().equals(operation))
        {
            binaryLeft = currentExpression.getLeft();
            temp = currentExpression.getRight();
            tempOperation = currentExpression.getOperation();
            mapOperatorOperand.put(temp, tempOperation);
            if (binaryLeft instanceof BinaryExpression)
            {
                currentExpression = (BinaryExpression)binaryLeft;
            }
            else
            {
                currentExpression = null;
            }
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
            DynamicFeatureAccess current = (DynamicFeatureAccess)expression;
            builder.insert(0, current.getName());
            builder.insert(0, DOT);
            expression = current.getSource();
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
