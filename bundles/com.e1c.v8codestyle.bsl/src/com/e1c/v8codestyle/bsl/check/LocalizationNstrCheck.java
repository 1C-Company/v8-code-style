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

import com._1c.g5.v8.dt.bsl.model.BinaryExpression;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
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

    private static final String MESSAGE_STR_INVOCATION = "Str invocation name"; //$NON-NLS-1$

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

    private static final Set<String> STR_INVOCATION =
        Set.of("СтрСоединить", "StrConcat", "СтрРазделить", "StrSplit", "СтрШаблон", "StrTemplate"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_MESSAGES = String.join(DELIMITER, IMMUTABLE_MAP_MESSAGES);

    private static final String DEFAULT_MESSAGES_NUMBER_ONE = String.join(DELIMITER, IMMUTABLE_MAP_NUMBER_ONE_MESSAGES);

    private static final String DEFAULT_MESSAGES_NUMBER_ZERO =
        String.join(DELIMITER, IMMUTABLE_MAP_NUMBER_ZERO_MESSAGES);

    private static final String DEFAULT_STR_INVOCATION = String.join(DELIMITER, STR_INVOCATION);

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
            .parameter(MESSAGE_STR_INVOCATION, String.class, DEFAULT_STR_INVOCATION,
                Messages.LocalizationNstrCheck_Parameter_Invocation_Name)
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = (Invocation)object;
        if (!parameters.getString(MESSAGE_NAME)
            .toLowerCase()
            .contains(invocation.getMethodAccess().getName().toLowerCase()) || invocation.getParams().isEmpty())
        {
            return;
        }
        String invNames = parameters.getString(MESSAGE_STR_INVOCATION);
        String[] invNamesList = invNames.split(DELIMITER);
        List<Expression> params = invocation.getParams();
        String nameInvocation = invocation.getMethodAccess().getName();
        int numberParam = numberParameter(nameInvocation, parameters);
        if (numberParam != -1 && numberParam < params.size())
        {
            Expression expression = params.get(numberParam);
            if (expression instanceof StringLiteral)
            {
                resultAcceptor.addIssue(Messages.LocalizationNstrCheck_Issue, invocation);
            }
            else if (expression instanceof StaticFeatureAccess sfa)
            {
                String name = sfa.getName();
                Method method = EcoreUtil2.getContainerOfType(invocation, Method.class);
                if (method == null)
                {
                    return;
                }
                if (!checkSfa(name, method, invNamesList))
                {
                    resultAcceptor.addIssue(Messages.LocalizationNstrCheck_Issue, invocation);
                }
            }
            else if (expression instanceof Invocation invocationParam)
            {
                String name = invocationParam.getMethodAccess().getName();
                if (!(checkNstr(name)))
                {
                    if (!invocationParam.getParams().isEmpty())
                    {
                        List<Expression> parametersInvocation = invocationParam.getParams();
                        Expression param = parametersInvocation.get(0);
                        if (param instanceof Invocation inv)
                        {
                            String invName = inv.getMethodAccess().getName();
                            if (!(checkNstr(invName)))
                            {
                                resultAcceptor.addIssue(Messages.LocalizationNstrCheck_Issue, invocation);
                            }
                        }
                    }
                }
            }
        }
    }

    private int numberParameter(String name, ICheckParameters parameters)
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

    private boolean checkSfa(String name, Method method, String[] invNames)
    {
        Set<String> assignedNames =
            nstrAssignedNamesCache.computeIfAbsent(method, m -> collectNstrAssignedNames(m.allStatements(), invNames));
        return assignedNames.contains(name.toLowerCase());
    }

    private Set<String> collectNstrAssignedNames(List<Statement> statements, String[] invNames)
    {
        Set<String> names = new HashSet<>();
        collectNstrAssignedNamesRec(statements, names, invNames);
        return names;
    }

    private void collectNstrAssignedNamesRec(List<Statement> statements, Set<String> names, String[] invNames)
    {
        for (Statement statement : statements)
        {
            if (statement instanceof SimpleStatement simpState)
            {
                collectFromSimpleState(simpState, names, statements, invNames);
            }
            else if (statement instanceof IfStatement ifStatement)
            {
                collectNstrAssignedNamesRec(ifStatement.getIfPart().getStatements(), names, invNames);
                collectNstrAssignedNamesRec(ifStatement.getElseStatements(), names, invNames);
                for (Conditional conditional : ifStatement.getElsIfParts())
                {
                    collectNstrAssignedNamesRec(conditional.getStatements(), names, invNames);
                }
            }
            else if (statement instanceof LoopStatement loopStatement)
            {
                collectNstrAssignedNamesRec(loopStatement.getStatements(), names, invNames);
            }
            else if (statement instanceof TryExceptStatement tryStatement)
            {
                collectNstrAssignedNamesRec(tryStatement.getTryStatements(), names, invNames);
                collectNstrAssignedNamesRec(tryStatement.getExceptStatements(), names, invNames);
            }
        }
    }

    private void collectFromSimpleState(SimpleStatement statement, Set<String> names, List<Statement> statements,
        String[] invNames)
    {
        Module module = EcoreUtil2.getContainerOfType(statement, Module.class);
        if (module == null)
        {
            return;
        }
        if (statement.getLeft() instanceof StaticFeatureAccess left
            && statement.getRight() instanceof Invocation invocation)
        {
            String nameInv = invocation.getMethodAccess().getName();
            boolean isNstr = checkNstr(nameInv);
            if (!isNstr && !invocation.getParams().isEmpty())
            {
                for (String string : invNames)
                {
                    if (string.equalsIgnoreCase(nameInv))
                    {
                        if (invocation.getParams().get(0) instanceof StaticFeatureAccess sfa)
                        {
                            for (Statement statementInlist : statements)
                            {
                                if (statementInlist instanceof SimpleStatement simpState)
                                {
                                    if (simpState.getLeft() instanceof StaticFeatureAccess staticFeature
                                        && staticFeature.getName().equalsIgnoreCase(sfa.getName()))
                                    {
                                        if (simpState.getRight() instanceof Invocation invocationFind
                                            && (checkNstr(invocationFind.getMethodAccess().getName())))
                                        {
                                            names.add(left.getName().toLowerCase());
                                            break;
                                        }
                                        else if (simpState.getRight() instanceof StaticFeatureAccess rightSfa)
                                        {
                                            if (isForm(module))
                                            {
                                                if (isAttribute(module, rightSfa.getName()))
                                                {
                                                    names.add(left.getName().toLowerCase());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                for (Expression param : invocation.getParams())
                {
                    if (param instanceof Invocation inv)
                    {
                        String invName = inv.getMethodAccess().getName();
                        if (checkNstr(invName))
                        {
                            names.add(left.getName().toLowerCase());
                            break;
                        }
                    }
                    else if (param instanceof BinaryExpression bin)
                    {
                        if (bin.getLeft() instanceof Invocation binInvocation)
                        {
                            String nameBinInv = binInvocation.getMethodAccess().getName();
                            if (checkNstr(nameBinInv))
                            {
                                names.add(left.getName().toLowerCase());
                                break;
                            }
                        }
                        else if (bin.getRight() instanceof Invocation binInvocation)
                        {
                            String nameBinInv = binInvocation.getMethodAccess().getName();
                            if (checkNstr(nameBinInv))
                            {
                                names.add(left.getName().toLowerCase());
                                break;
                            }
                        }
                    }
                    else if (param instanceof StaticFeatureAccess paramSfa)
                    {
                        if (inParam(statement, paramSfa.getName()))
                        {
                            names.add(left.getName().toLowerCase());
                            break;
                        }
                    }
                }
            }
            else
            {
                List<Method> methods = module.allMethods();
                for (Method method : methods)
                {
                    if (method.getName().equalsIgnoreCase(invocation.getMethodAccess().getName()))
                    {
                        if (method instanceof Function)
                        {
                            names.add(left.getName().toLowerCase());
                        }
                    }
                }
            }
            if (invocation.getMethodAccess() instanceof DynamicFeatureAccess)
            {
                names.add(left.getName().toLowerCase());
            }
            if (isNstr)
            {
                names.add(left.getName().toLowerCase());
            }
        }
        else if (statement.getLeft() instanceof StaticFeatureAccess left
            && statement.getRight() instanceof StaticFeatureAccess right)
        {
            if (isForm(module))
            {
                if (isAttribute(module, right.getName()))
                {
                    names.add(left.getName().toLowerCase());
                }
            }
        }
        else if (statement.getLeft() instanceof StaticFeatureAccess left
            && statement.getRight() instanceof BinaryExpression bin)
        {
            if (bin.getLeft() instanceof Invocation binInvocation)
            {
                String nameBinInv = binInvocation.getMethodAccess().getName();
                if (checkNstr(nameBinInv))
                {
                    names.add(left.getName().toLowerCase());
                }
            }
            else if (bin.getRight() instanceof Invocation binInvocation)
            {
                String nameBinInv = binInvocation.getMethodAccess().getName();
                if (checkNstr(nameBinInv))
                {
                    names.add(left.getName().toLowerCase());
                }
            }
        }
        else if (statement.getLeft() instanceof Invocation leftInv)
        {
            List<Expression> expressions = leftInv.getParams();
            for (Expression expression : expressions)
            {
                if (expression instanceof StaticFeatureAccess paramSfa)
                {
                    if (isForm(module))
                    {
                        if (isAttribute(module, paramSfa.getName()))
                        {
                            names.add(paramSfa.getName().toLowerCase());
                        }
                    }
                    if (inParam(statement, paramSfa.getName()))
                    {
                        names.add(paramSfa.getName().toLowerCase());
                    }
                }
            }
        }
    }

    private boolean inParam(Statement statement, String name)
    {
        Method method = EcoreUtil2.getContainerOfType(statement, Method.class);
        if (method == null)
        {
            return false;
        }
        List<FormalParam> params = method.getFormalParams();
        for (FormalParam methodParam : params)
        {
            if (methodParam.getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkNstr(String name)
    {
        return NSTR_RU.equalsIgnoreCase(name) || NSTR.equalsIgnoreCase(name);
    }

    private boolean isForm(Module module)
    {
        return ModuleType.FORM_MODULE == module.getModuleType();
    }

    private boolean isAttribute(Module module, String name)
    {
        if (module.getOwner() instanceof Form form)
        {
            List<FormAttribute> formAttributes = form.getAttributes();
            for (FormAttribute formAttribute : formAttributes)
            {
                if (name.equalsIgnoreCase(formAttribute.getName()))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
