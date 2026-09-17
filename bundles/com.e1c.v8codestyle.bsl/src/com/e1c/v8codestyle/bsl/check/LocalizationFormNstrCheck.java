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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DataPathReferredObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormAttributeColumn;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
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
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "nstr-form-localization"; //$NON-NLS-1$

    private static final String NSTR = "NStr"; //$NON-NLS-1$

    private static final String NSTR_RU = "НСтр"; //$NON-NLS-1$

    private static final String STRING_TEXT = "String"; //$NON-NLS-1$

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
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module formModule = (Module)object;
        if (ModuleType.FORM_MODULE != formModule.getModuleType())
        {
            return;
        }
        Form form = (Form)formModule.getOwner();
        List<FormAttribute> attributes = form.getAttributes();
        List<Method> methods = formModule.allMethods();
        List<FormItem> fields = form.getItems();
        List<FormAttribute> displayedAttribute = new ArrayList<>();
        for (FormItem formItem : fields)
        {
            if (formItem instanceof FormField formField)
            {
                AbstractDataPath data = formField.getDataPath();
                if (data == null)
                {
                    continue;
                }
                List<DataPathReferredObject> refObjects = data.getObjects();
                if (refObjects == null)
                {
                    continue;
                }
                displayedAttribute.addAll(findAttribute(refObjects));
            }
        }
        Map<Method, Map<String, Statement>> assignmentsByMethod = new HashMap<>();
        for (Method method : methods)
        {
            assignmentsByMethod.put(method, collectAssignments(method.allStatements()));
        }

        for (FormAttribute attribute : displayedAttribute)
        {
            List<TypeItem> types = attribute.getValueType().getTypes();
            for (TypeItem type : types)
            {
                if (STRING_TEXT.equalsIgnoreCase(McoreUtil.getTypeName(type)))
                {
                    checkAttributeName(attribute.getName(), methods, assignmentsByMethod, resultAceptor);
                }
                else if ("ValueTable".equalsIgnoreCase(McoreUtil.getTypeName(type))) //$NON-NLS-1$
                {
                    List<FormAttributeColumn> columns = attribute.getColumns();
                    for (FormAttributeColumn column : columns)
                    {
                        List<TypeItem> typesColumn = column.getValueType().getTypes();
                        for (TypeItem typeColumn : typesColumn)
                        {
                            if (STRING_TEXT.equalsIgnoreCase(McoreUtil.getTypeName(typeColumn)))
                            {
                                checkAttributeName(column.getName(), methods, assignmentsByMethod, resultAceptor);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkAttributeName(String name, List<Method> methods,
        Map<Method, Map<String, Statement>> assignmentsByMethod, ResultAcceptor resultAceptor)
    {
        String key = name.toLowerCase();
        for (Method method : methods)
        {
            Map<String, Statement> assignments = assignmentsByMethod.get(method);
            Statement statement = assignments.get(key);
            if (statement != null && checkStatement(statement, assignments))
            {
                SimpleStatement simp = (SimpleStatement)statement;
                resultAceptor.addIssue(Messages.LocalizationNstrCheck_Issue, statement);
            }
        }
    }

    private boolean checkStatement(Statement statement, Map<String, Statement> methodAssignments)
    {
        if (statement instanceof SimpleStatement simpleStat)
        {
            if (simpleStat.getRight() instanceof StringLiteral stringLiteral)
            {
                String text = stringLiteral.getLines().get(0);
                String clean = text.replace("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
                if (!clean.isEmpty())
                {
                    return true;
                }
            }
            else if (simpleStat.getRight() instanceof StaticFeatureAccess sfa)
            {
                if (!checkSfa(sfa.getName(), methodAssignments))
                {
                    return true;
                }
            }
            else if (simpleStat.getRight() instanceof Invocation invocationRight)
            {
                if (!invocationRight.getParams().isEmpty()
                    && invocationRight.getParams().get(0) instanceof Invocation invocationParam)
                {
                    String name = invocationParam.getMethodAccess().getName();
                    if (!(NSTR_RU.equalsIgnoreCase(name) || NSTR.equalsIgnoreCase(name)))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<FormAttribute> findAttribute(List<DataPathReferredObject> refObjects)
    {
        List<FormAttribute> formAttributes = new ArrayList<>();
        for (DataPathReferredObject dataPathReferredObject : refObjects)
        {
            if (dataPathReferredObject.getObject() instanceof FormAttribute formAttribute)
            {
                formAttributes.add(formAttribute);
            }
        }
        return formAttributes;
    }

    private Map<String, Statement> collectAssignments(List<Statement> statements)
    {
        Map<String, Statement> result = new HashMap<>();
        collectAssignments(statements, result);
        return result;
    }

    private void collectAssignments(List<Statement> statements, Map<String, Statement> names)
    {
        for (Statement statement : statements)
        {
            if (statement instanceof EmptyStatement)
            {
                continue;
            }
            else if (statement instanceof SimpleStatement simp)
            {
                if (simp.getLeft() instanceof StaticFeatureAccess left)
                {
                    names.putIfAbsent(left.getName().toLowerCase(), simp);
                }
            }
            else if (statement instanceof IfStatement ifStatement)
            {
                collectAssignments(ifStatement.getIfPart().getStatements(), names);
                collectAssignments(ifStatement.getElseStatements(), names);
                for (Conditional conditional : ifStatement.getElsIfParts())
                {
                    collectAssignments(conditional.getStatements(), names);
                }
            }
            else if (statement instanceof ForStatement forStatement)
            {
                collectAssignments(forStatement.getStatements(), names);
            }
        }
    }

    private boolean checkSfa(String name, Map<String, Statement> methodAssignments)
    {
        Statement statement = methodAssignments.get(name.toLowerCase());
        if (statement instanceof SimpleStatement simpState && simpState.getRight() instanceof Invocation invocation)
        {
            String nameInv = invocation.getMethodAccess().getName();
            return NSTR_RU.equalsIgnoreCase(nameInv) || NSTR.equalsIgnoreCase(nameInv);
        }
        return false;
    }
}
