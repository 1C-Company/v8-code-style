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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DataItem;
import com._1c.g5.v8.dt.form.model.DataPathReferredObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormAttributeColumn;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.Group;
import com._1c.g5.v8.dt.form.model.ManagedFormFieldType;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.form.service.item.FormItemVisitor;
import com._1c.g5.v8.dt.form.service.item.IFormItemCommand;
import com._1c.g5.v8.dt.mcore.TypeDescription;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks string localization use NStr.
 *
 *  @author Ivan Sergeev
 */
public class LocalizationFormNstrCheck
    extends BasicCheck<Object>
{
    private static final String CHECK_ID = "nstr-form-localization"; //$NON-NLS-1$

    private static final String STRING_TYPE_NAME = "String"; //$NON-NLS-1$

    private record DisplayedStrings(Set<String> attributes, Map<String, Set<String>> columns) {
        boolean isEmpty()
        {
            return attributes.isEmpty() && columns.isEmpty();
        }
    }

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
            .extension(new StandardCheckExtension(761, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module formModule = (Module)object;
        if (ModuleType.FORM_MODULE != formModule.getModuleType() || !(formModule.getOwner() instanceof Form form))
        {
            return;
        }

        DisplayedStrings displayed = collectDisplayedStrings(form);
        if (displayed.isEmpty())
        {
            return;
        }

        Set<SimpleStatement> reported = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Method method : formModule.allMethods())
        {
            if (monitor.isCanceled())
            {
                return;
            }
            checkMethod(method, displayed, resultAcceptor, reported, monitor);
        }
    }

    private DisplayedStrings collectDisplayedStrings(Form form)
    {
        Set<String> attributes = new HashSet<>();
        Map<String, Set<String>> columns = new HashMap<>();

        for (FormField field : collectVisibleFields(form))
        {
            if (ManagedFormFieldType.INPUT_FIELD != field.getType()
                && ManagedFormFieldType.LABEL_FIELD != field.getType())
            {
                continue;
            }
            EObject target = getDataPathTarget(field);
            if (target instanceof FormAttribute attribute)
            {
                if (hasStringType(attribute.getValueType()))
                {
                    attributes.add(lower(attribute.getName()));
                }
            }
            else if (target instanceof FormAttributeColumn column && column.eContainer() instanceof FormAttribute table
                && hasStringType(column.getValueType()))
            {
                String tableName = lower(table.getName());
                Set<String> tableColumns = columns.get(tableName);
                if (tableColumns == null)
                {
                    tableColumns = new HashSet<>();
                    columns.put(tableName, tableColumns);
                }
                tableColumns.add(lower(column.getName()));
            }
        }
        return new DisplayedStrings(attributes, columns);
    }

    private Set<FormField> collectVisibleFields(Form form)
    {
        Set<FormField> result = new LinkedHashSet<>();
        IFormItemCommand command = entity -> {
            if (entity instanceof FormField field)
            {
                if (isEffectivelyVisible(field))
                {
                    result.add(field);
                }
            }
            else if (entity instanceof Table table && isEffectivelyVisible(table))
            {
                for (FormField field : EcoreUtil2.getAllContentsOfType(table, FormField.class))
                {
                    if (isEffectivelyVisible(field))
                    {
                        result.add(field);
                    }
                }
            }
        };
        new FormItemVisitor(command).visit(form);
        return result;
    }

    private EObject getDataPathTarget(FormField field)
    {
        AbstractDataPath path = field.getDataPath();
        if (path == null)
        {
            return null;
        }
        List<DataPathReferredObject> objects = path.getObjects();
        if (objects == null || objects.isEmpty())
        {
            return null;
        }
        return objects.get(objects.size() - 1).getObject();
    }

    private boolean hasStringType(TypeDescription description)
    {
        if (description == null)
        {
            return false;
        }
        for (TypeItem type : description.getTypes())
        {
            if (STRING_TYPE_NAME.equalsIgnoreCase(McoreUtil.getTypeName(type)))
            {
                return true;
            }
        }
        return false;
    }

    private void checkMethod(Method method, DisplayedStrings displayed, ResultAcceptor resultAcceptor,
        Set<SimpleStatement> reported, IProgressMonitor monitor)
    {
        List<SimpleStatement> statements = EcoreUtil2.getAllContentsOfType(method, SimpleStatement.class);
        Set<String> localNames = collectLocalNames(method);
        Map<String, List<SimpleStatement>> assignments = collectAssignments(statements);

        for (SimpleStatement statement : statements)
        {
            if (monitor.isCanceled())
            {
                return;
            }
            if (!isAssignmentToDisplayedString(statement, displayed, localNames))
            {
                continue;
            }
            if (isUnlocalizedString(statement.getRight(), assignments, new HashSet<>()) && reported.add(statement))
            {
                resultAcceptor.addIssue(Messages.LocalizationNstrCheck_Issue, statement);
            }
        }
    }

    private Set<String> collectLocalNames(Method method)
    {
        Set<String> names = new HashSet<>();
        for (FormalParam param : method.getFormalParams())
        {
            names.add(lower(param.getName()));
        }
        for (DeclareStatement declare : EcoreUtil2.getAllContentsOfType(method, DeclareStatement.class))
        {
            for (ExplicitVariable variable : declare.getVariables())
            {
                names.add(lower(variable.getName()));
            }
        }
        return names;
    }

    private Map<String, List<SimpleStatement>> collectAssignments(List<SimpleStatement> statements)
    {
        Map<String, List<SimpleStatement>> result = new HashMap<>();
        for (SimpleStatement statement : statements)
        {
            if (statement.getLeft() instanceof StaticFeatureAccess left)
            {
                String name = lower(left.getName());
                if (name == null)
                {
                    continue;
                }
                List<SimpleStatement> statementsForName = result.get(name);
                if (statementsForName == null)
                {
                    statementsForName = new ArrayList<>();
                    result.put(name, statementsForName);
                }
                statementsForName.add(statement);
            }
        }
        return result;
    }

    private static boolean isEffectivelyVisible(EObject entity)
    {
        for (EObject current = entity; current != null && !(current instanceof Form); current = current.eContainer())
        {
            if ((current instanceof DataItem item && !item.isVisible())
                || (current instanceof Group group && !group.isVisible()))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isAssignmentToDisplayedString(SimpleStatement statement, DisplayedStrings displayed,
        Set<String> localNames)
    {
        Expression left = statement.getLeft();
        if (left instanceof StaticFeatureAccess sfa)
        {
            String name = lower(sfa.getName());
            return displayed.attributes().contains(name) && !localNames.contains(name) && !resolvesToVariable(sfa);
        }
        if (left instanceof DynamicFeatureAccess dfa)
        {
            String table = getTableAttributeName(dfa.getSource(), localNames);
            if (table == null)
            {
                return false;
            }
            Set<String> columns = displayed.columns().get(table);
            return columns != null && columns.contains(lower(dfa.getName()));
        }
        return false;
    }

    private String getTableAttributeName(Expression source, Set<String> localNames)
    {
        if (source instanceof IndexAccess indexAccess)
        {
            return getTableAttributeName(indexAccess.getSource(), localNames);
        }
        if (source instanceof StaticFeatureAccess sfa)
        {
            String name = lower(sfa.getName());
            if (!localNames.contains(name) && !resolvesToVariable(sfa))
            {
                return name;
            }
        }
        return null;
    }

    private boolean resolvesToVariable(StaticFeatureAccess access)
    {
        for (FeatureEntry entry : access.getFeatureEntries())
        {
            if (entry.getFeature() instanceof Variable)
            {
                return true;
            }
        }
        return false;
    }

    private boolean isUnlocalizedString(Expression expression, Map<String, List<SimpleStatement>> assignments,
        Set<String> visitedNames)
    {
        if (expression instanceof StringLiteral literal)
        {
            return containsText(literal);
        }
        if (expression instanceof StaticFeatureAccess sfa)
        {
            String name = lower(sfa.getName());
            if (!visitedNames.add(name))
            {
                return false;
            }
            List<SimpleStatement> variableAssignments = assignments.get(name);
            if (variableAssignments == null)
            {
                return false;
            }
            for (SimpleStatement assignment : variableAssignments)
            {
                if (isUnlocalizedString(assignment.getRight(), assignments, visitedNames))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsText(StringLiteral literal)
    {
        for (String line : literal.getLines())
        {
            for (int i = 0; i < line.length(); i++)
            {
                if (Character.isLetter(line.charAt(i)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static String lower(String name)
    {
        return name == null ? null : name.toLowerCase();
    }
}
