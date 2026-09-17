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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

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
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks string localization use NStr.
 *
 *  @author Ivan Sergeev
 */
public class LocalizationNstrCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "nstr-localization"; //$NON-NLS-1$

    private static final String MESSAGE_NAME = "Message name"; //$NON-NLS-1$

    private static final String MESSAGE_NAME_ONE = "Message name parameter number one"; //$NON-NLS-1$

    private static final String MESSAGE_NAME_ZERO = "Message name parameter number zero"; //$NON-NLS-1$

    private static final String NSTR = "NStr"; //$NON-NLS-1$

    private static final String NSTR_RU = "НСтр"; //$NON-NLS-1$

    private static final Set<String> IMMUTABLE_MAP_MESSAGES =
        Set.of("ПоказатьПредупреждение", "ShowMessagebox", "Сообщение", "Message", "Сообщить", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
            "ПоказатьОповещениеПользователя", "ShowUserNotification", "ПоказатьВопрос", "ShowQueryBox", "Состояние", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$
            "Status"); //$NON-NLS-1$
    private static final Set<String> IMMUTABLE_MAP_NUMBER_ONE_MESSAGES =
        Set.of("ПоказатьПредупреждение", "ShowMessageBox", "ПоказатьВопрос", "ShowQueryBox"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private static final Set<String> IMMUTABLE_MAP_NUMBER_ZERO_MESSAGES = Set.of("сообщение", "Сообщить", "Message", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "ПоказатьОповещениеПользователя", "ShowUsernotification", "Состояние", "Status"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_MESSAGES = String.join(DELIMITER, IMMUTABLE_MAP_MESSAGES);

    private static final String DEFAULT_MESSAGES_NUMBER_ONE = String.join(DELIMITER, IMMUTABLE_MAP_NUMBER_ONE_MESSAGES);

    private static final String DEFAULT_MESSAGES_NUMBER_ZERO =
        String.join(DELIMITER, IMMUTABLE_MAP_NUMBER_ZERO_MESSAGES);
    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.LocalizationNstrCheck_Title)
            .description(Messages.LocalizationNstrCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(761, getCheckId(), BslPlugin.PLUGIN_ID))
            .parameter(MESSAGE_NAME, String.class, DEFAULT_MESSAGES, Messages.LocalizationNstrCheck_Parameter_Title)
            .parameter(MESSAGE_NAME_ONE, String.class, DEFAULT_MESSAGES_NUMBER_ONE,
                Messages.LocalizationNstrCheck_Parameter_Title_One)
            .parameter(MESSAGE_NAME_ZERO, String.class, DEFAULT_MESSAGES_NUMBER_ZERO,
                Messages.LocalizationNstrCheck_Parameter_Title_Zero)
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = (Invocation)object;
        if (!parameters.getString(MESSAGE_NAME)
            .toLowerCase()
            .contains(invocation.getMethodAccess().getName().toLowerCase())
            || invocation.getParams().isEmpty())
        {
            return;
        }
        List<Expression> params = invocation.getParams();
        String nameInvocation = invocation.getMethodAccess().getName();
        int numberParam = numberParametr(nameInvocation, parameters);
        if (numberParam != -1)
        {
            Expression expression = params.get(numberParam);
            if (expression instanceof StringLiteral)
            {
                resultAceptor.addIssue(Messages.LocalizationNstrCheck_Issue, invocation);
            }
            else if (expression instanceof StaticFeatureAccess sfa)
            {
                String name = sfa.getName();
                Method method = EcoreUtil2.getContainerOfType(invocation, Method.class);
                if (!checkSfa(name, method))
                {
                    resultAceptor.addIssue(Messages.LocalizationNstrCheck_Issue, invocation);
                }
            }
            else if (expression instanceof Invocation invocationParam)
            {
                String name = invocationParam.getMethodAccess().getName();
                if (!(NSTR_RU.equalsIgnoreCase(name) || NSTR.equalsIgnoreCase(name)))
                {
                    if (!invocationParam.getParams().isEmpty())
                    {
                        List<Expression> parametrs = invocationParam.getParams();
                        Expression param = parametrs.get(0);
                        if (param instanceof Invocation inv)
                        {
                            String invName = inv.getMethodAccess().getName();
                            if (!(NSTR_RU.equalsIgnoreCase(invName) || NSTR.equalsIgnoreCase(invName)))
                            {
                                resultAceptor.addIssue(Messages.LocalizationNstrCheck_Issue, invocation);
                            }
                        }
                    }
                }
            }
        }
    }

    private int numberParametr(String name, ICheckParameters parameters)
    {
        String paramOne = parameters.getString(MESSAGE_NAME_ONE);
        String paramZero = parameters.getString(MESSAGE_NAME_ZERO);
        String[] arrayOne = paramOne.split(DELIMITER);
        String[] arrayZero = paramZero.split(DELIMITER);
        for (String string : arrayOne)
        {
            if (string.equalsIgnoreCase(name))
            {
                return 1;
            }
        }
        for (String string : arrayZero)
        {
            if (string.equalsIgnoreCase(name))
            {
                return 0;
            }
        }
        return -1;
    }

    private final Map<Method, Set<String>> nstrAssignedNamesCache = Collections.synchronizedMap(new WeakHashMap<>());

    private boolean checkSfa(String name, Method method)
    {
        Set<String> assignedNames =
            nstrAssignedNamesCache.computeIfAbsent(method, m -> collectNstrAssignedNames(m.allStatements()));
        return assignedNames.contains(name.toLowerCase());
    }

    private Set<String> collectNstrAssignedNames(List<Statement> statements)
    {
        Set<String> names = new HashSet<>();
        collectNstrAssignedNamesRec(statements, names);
        return names;
    }

    private void collectNstrAssignedNamesRec(List<Statement> statements, Set<String> names)
    {
        for (Statement statement : statements)
        {
            if (statement instanceof SimpleStatement simpState)
            {
                collectFromSimpleState(simpState, names);
            }
            else if (statement instanceof IfStatement ifStatement)
            {
                collectNstrAssignedNamesRec(ifStatement.getIfPart().getStatements(), names);
                collectNstrAssignedNamesRec(ifStatement.getElseStatements(), names);
                for (Conditional conditional : ifStatement.getElsIfParts())
                {
                    collectNstrAssignedNamesRec(conditional.getStatements(), names);
                }
            }
            else if (statement instanceof ForStatement forStatement)
            {
                collectNstrAssignedNamesRec(forStatement.getStatements(), names);
            }
        }
    }

    private void collectFromSimpleState(SimpleStatement statement, Set<String> names)
    {
        if (statement.getLeft() instanceof StaticFeatureAccess left
            && statement.getRight() instanceof Invocation invocation)
        {
            String nameInv = invocation.getMethodAccess().getName();
            boolean isNstr = NSTR_RU.equalsIgnoreCase(nameInv) || NSTR.equalsIgnoreCase(nameInv);

            if (!isNstr && !invocation.getParams().isEmpty())
            {
                for (Expression param : invocation.getParams())
                {
                    if (param instanceof Invocation inv)
                    {
                        String invName = inv.getMethodAccess().getName();
                        if (NSTR_RU.equalsIgnoreCase(invName) || NSTR.equalsIgnoreCase(invName))
                        {
                            isNstr = true;
                            break;
                        }
                    }
                }
            }

            if (isNstr)
            {
                names.add(left.getName().toLowerCase());
            }
        }
    }
}
