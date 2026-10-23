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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.NumberLiteral;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DataPathReferredObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormAttributeColumn;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.form.service.item.FormItemVisitor;
import com._1c.g5.v8.dt.form.service.item.IFormItemCommand;
import com._1c.g5.v8.dt.mcore.TypeDescription;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks that numeric money fields displayed on a form (attributes and value table columns
 * with names like "Сумма", "Цена") get their type description from the money field
 * type description method instead of being assigned numbers, number creators or arbitrary calls.
 *
 * @author Ivan Sergeev
 */
public class MoneyStringLocalizationCheck
    extends AbstractModuleStructureCheck
{
    private static final String MONEYFIELD_TYPE_DESCRIPTION = "ОписаниеТипаДенежногоПоля"; //$NON-NLS-1$

    private static final String CHECK_ID = "money-string-localization"; //$NON-NLS-1$

    private static final String MONEY_STRING_NAME = "Money string name"; //$NON-NLS-1$

    private static final String TYPE_NUMBER = "Number"; //$NON-NLS-1$

    private static final String TYPE_VALUE_TABLE = "ValueTable"; //$NON-NLS-1$

    private static final String METHOD_ADD = "Добавить"; //$NON-NLS-1$

    private static final String METHOD_INSERT = "Вставить"; //$NON-NLS-1$

    private static final Set<String> DEFAULT_MONEY_STRING_NAMES = Set.of("Сумма", "Цена", "Себестоимость", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        "СуммаИзлишков", "Amount", "Price", "Cost", "SurplusAmount", "DescriptionTypesAmount"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_NAMES = String.join(DELIMITER, DEFAULT_MONEY_STRING_NAMES);

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MoneyStringLocalizationCheck_Title)
            .description(Messages.MoneyStringLocalizationCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(778, getCheckId(), BslPlugin.PLUGIN_ID))
            .parameter(MONEY_STRING_NAME, String.class, DEFAULT_NAMES, Messages.MoneyStringLocalizationCheck_Parameter)
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;
        if (ModuleType.FORM_MODULE != module.getModuleType() || !(module.getOwner() instanceof Form form))
        {
            return;
        }

        Set<String> moneyNames = parseNames(parameters.getString(MONEY_STRING_NAME));
        Set<String> fieldNames = collectNumericMoneyFieldNames(form, moneyNames);
        if (fieldNames.isEmpty())
        {
            return;
        }
        List<SimpleStatement> statements = EcoreUtil2.getAllContentsOfType(module, SimpleStatement.class);

        Set<SimpleStatement> reported = new HashSet<>();
        for (String name : fieldNames)
        {
            if (monitor.isCanceled())
            {
                return;
            }
            for (SimpleStatement statement : findAssignments(statements, name))
            {
                if (isWrongAssignment(statement) && reported.add(statement))
                {
                    resultAcceptor.addIssue(Messages.MoneyStringLocalizationCheck_Issue, statement);
                }
            }
        }
    }

    private static Set<String> parseNames(String rawNames)
    {
        if (rawNames == null)
        {
            return Set.of();
        }
        return Arrays.stream(rawNames.split(DELIMITER))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> s.toLowerCase())
            .collect(Collectors.toSet());
    }

    private static Set<FormField> collectFields(Form form)
    {
        Set<FormField> result = new LinkedHashSet<>();
        IFormItemCommand command = entity -> {
            if (entity instanceof FormField field)
            {
                result.add(field);
            }
            else if (entity instanceof Table table)
            {
                result.addAll(EcoreUtil2.getAllContentsOfType(table, FormField.class));
            }
        };
        new FormItemVisitor(command).visit(form);
        return result;
    }

    private static Set<String> collectNumericMoneyFieldNames(Form form, Set<String> moneyNames)
    {
        Set<FormAttribute> attributes = new LinkedHashSet<>();
        for (FormField field : collectFields(form))
        {
            AbstractDataPath path = field.getDataPath();
            if (path == null || path.getObjects() == null)
            {
                continue;
            }
            for (DataPathReferredObject ref : path.getObjects())
            {
                if (ref.getObject() instanceof FormAttribute attribute)
                {
                    attributes.add(attribute);
                }
            }
        }

        Set<String> result = new LinkedHashSet<>();
        for (FormAttribute attribute : attributes)
        {
            TypeDescription type = attribute.getValueType();
            if (hasType(type, TYPE_VALUE_TABLE))
            {
                for (FormAttributeColumn column : attribute.getColumns())
                {
                    if (column.getName() != null && moneyNames.contains(column.getName().toLowerCase())
                        && hasType(column.getValueType(), TYPE_NUMBER))
                    {
                        result.add(column.getName());
                    }
                }
            }
            else if (attribute.getName() != null && moneyNames.contains(attribute.getName().toLowerCase())
                && hasType(type, TYPE_NUMBER))
            {
                result.add(attribute.getName());
            }
        }
        return result;
    }

    private static boolean hasType(TypeDescription description, String typeName)
    {
        return description != null
            && description.getTypes().stream().anyMatch(type -> typeName.equalsIgnoreCase(McoreUtil.getTypeName(type)));
    }

    private static List<SimpleStatement> findAssignments(List<SimpleStatement> statements, String name)
    {
        List<SimpleStatement> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(name);

        while (!queue.isEmpty())
        {
            String current = queue.poll();
            if (!visited.add(current.toLowerCase()))
            {
                continue;
            }
            for (SimpleStatement statement : statements)
            {
                Expression left = statement.getLeft();
                if (isFieldAccess(left, current))
                {
                    result.add(statement);
                }
                else if (left instanceof Invocation invocation)
                {
                    Optional<Expression> value = getFieldValueExpression(invocation, current);
                    if (value.isPresent())
                    {
                        String valueName = getTrackableVariable(value.get(), current);
                        if (valueName != null)
                        {
                            queue.add(valueName);
                        }
                        else
                        {
                            result.add(statement);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean isFieldAccess(Expression expression, String name)
    {
        if (expression instanceof StaticFeatureAccess staticAccess)
        {
            return name.equalsIgnoreCase(staticAccess.getName());
        }
        if (expression instanceof DynamicFeatureAccess dynamicAccess)
        {
            return name.equalsIgnoreCase(dynamicAccess.getName());
        }
        return false;
    }

    private static Optional<Expression> getFieldValueExpression(Invocation invocation, String name)
    {
        if (invocation.getMethodAccess() == null)
        {
            return Optional.empty();
        }
        String method = invocation.getMethodAccess().getName();
        if (!METHOD_ADD.equalsIgnoreCase(method) && !METHOD_INSERT.equalsIgnoreCase(method))
        {
            return Optional.empty();
        }
        List<Expression> params = invocation.getParams();
        if (params.size() < 2 || !(params.get(0) instanceof StringLiteral key))
        {
            return Optional.empty();
        }
        List<String> lines = key.getLines();
        if (lines.isEmpty() || !name.equalsIgnoreCase(lines.get(0).replace("\"", ""))) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return Optional.empty();
        }
        return Optional.of(params.get(1));
    }

    private static String getTrackableVariable(Expression expression, String name)
    {
        if (!(expression instanceof StaticFeatureAccess value))
        {
            return null;
        }
        String valueName = value.getName();
        if (valueName == null || MONEYFIELD_TYPE_DESCRIPTION.equalsIgnoreCase(valueName)
            || name.equalsIgnoreCase(valueName))
        {
            return null;
        }
        return valueName;
    }

    private static boolean isWrongAssignment(SimpleStatement statement)
    {
        Expression assigned = getAssignedValue(statement);
        if (assigned instanceof Invocation invocation)
        {
            return invocation.getMethodAccess() != null
                && !MONEYFIELD_TYPE_DESCRIPTION.equalsIgnoreCase(invocation.getMethodAccess().getName());
        }
        return assigned instanceof OperatorStyleCreator || assigned instanceof NumberLiteral;
    }

    private static Expression getAssignedValue(SimpleStatement statement)
    {
        Expression left = statement.getLeft();
        if (left instanceof Invocation invocation)
        {
            List<Expression> params = invocation.getParams();
            if (params.size() >= 2)
            {
                return params.get(1);
            }
            return null;
        }
        return statement.getRight();
    }
}