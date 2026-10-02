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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
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
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DataPathReferredObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormAttributeColumn;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.FormVisualEntity;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.form.service.item.FormItemVisitor;
import com._1c.g5.v8.dt.form.service.item.IFormItemCommand;
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
        List<Method> methods = formModule.allMethods();

        List<FormAttribute> allAttribute = form.getAttributes();

        List<FormAttribute> displayedAttribute = new ArrayList<>();

        final List<FormField> visibleFields = new ArrayList<>();
        IFormItemCommand collectCommand = new IFormItemCommand()
        {
            @Override
            public void execute(FormVisualEntity fve)
            {
                if (fve instanceof FormField formField)
                {
                    if (formField.isVisible())
                    {
                        formField.getType();
                        AbstractDataPath data = formField.getDataPath();
                        if (data == null)
                        {
                            return;
                        }
                        List<DataPathReferredObject> refObjects = data.getObjects();
                        if (refObjects == null)
                        {
                            return;
                        }
                        visibleFields.add(formField);
                    }
                }
                else if (fve instanceof Table table)
                {
                    AbstractDataPath data = table.getDataPath();
                    if (data == null)
                    {
                        return;
                    }
                    List<FormItem> tableItems = table.getItems();
                    for (FormItem formItemTable : tableItems)
                    {
                        if (formItemTable instanceof FormField formFieldTable)
                        {
                            if (formFieldTable.isVisible())
                            {
                                visibleFields.add(formFieldTable);
                            }
                        }
                    }
                }
            }
        };

        FormItemVisitor visitor = new FormItemVisitor(collectCommand);
        visitor.visit(form);

        for (FormField formField : visibleFields)
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

        Map<Method, Map<String, Statement>> assignmentsByMethod = new HashMap<>();
        Set<Statement> reportedStatements = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Method method : methods)
        {
            assignmentsByMethod.put(method, collectAssignments(method.allStatements()));
        }

        for (FormAttribute attribute : displayedAttribute)
        {
            visitor.visit(attribute);
            List<TypeItem> types = attribute.getValueType().getTypes();
            for (TypeItem type : types)
            {
                if (STRING_TEXT.equalsIgnoreCase(McoreUtil.getTypeName(type)))
                {
                    checkAttributeName(attribute.getName(), methods, assignmentsByMethod, allAttribute,
                        resultAceptor, reportedStatements);
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
                                checkAttributeName(column.getName(), methods, assignmentsByMethod, allAttribute,
                                    resultAceptor, reportedStatements);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkAttributeName(String name, List<Method> methods,
        Map<Method, Map<String, Statement>> assignmentsByMethod, List<FormAttribute> allAttribute,
        ResultAcceptor resultAceptor, Set<Statement> reportedStatements)
    {
        String key = name.toLowerCase();
        for (Method method : methods)
        {
            Map<String, Statement> assignments = assignmentsByMethod.get(method);
            Statement statement = assignments.get(key);
            if (statement != null && checkStatement(statement, assignments, allAttribute))
            {
                if (reportedStatements.add(statement))
                {
                    resultAceptor.addIssue(Messages.LocalizationNstrCheck_Issue, statement);
                }
            }
        }
    }

    private boolean checkStatement(Statement statement, Map<String, Statement> methodAssignments,
        List<FormAttribute> allAttribute)
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
                    for (FormAttribute attribute : allAttribute)
                    {
                        if (attribute.getName().equalsIgnoreCase(sfa.getName()))
                        {
                            return false;
                        }
                    }
                    Method method = EcoreUtil2.getContainerOfType(statement, Method.class);
                    List<FormalParam> params = method.getFormalParams();
                    for (FormalParam formalParam : params)
                    {
                        if (formalParam.getName().equalsIgnoreCase(sfa.getName()))
                        {
                            return false;
                        }
                    }
                    List<Statement> statemetsMethod = method.getStatements();
                    Map<String, Statement> assignmentsByMethod = new HashMap<>();
                    assignmentsByMethod.putAll(collectAssignments(statemetsMethod));
                    if (assignmentsByMethod.keySet().contains(sfa.getName().toLowerCase()))
                    {
                        Statement findStatement = assignmentsByMethod.get(sfa.getName().toLowerCase());
                        if (findStatement instanceof SimpleStatement simState)
                        {
                            return checkStatement(simState, methodAssignments, allAttribute);
                        }
                    }
                    return true;
                }
            }
            else if (simpleStat.getRight() instanceof Invocation invocationRight)
            {
                NodeModelUtils.findActualNodeFor(statement).getText();
                if (!invocationRight.getParams().isEmpty()
                    && invocationRight.getParams().get(0) instanceof Invocation invocationParam)
                {
                    return false;
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
            else if (statement instanceof LoopStatement loopStatement)
            {
                collectAssignments(loopStatement.getStatements(), names);
            }
            else if (statement instanceof TryExceptStatement tryStatement)
            {
                collectAssignments(tryStatement.getTryStatements(), names);
            }
        }
    }

    private boolean checkSfa(String name, Map<String, Statement> methodAssignments)
    {
        Statement statement = methodAssignments.get(name.toLowerCase());
        if (statement instanceof SimpleStatement simpState && simpState.getRight() instanceof Invocation invocation)
        {
            return true;
        }
        return false;
    }
}
