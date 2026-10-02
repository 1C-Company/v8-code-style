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
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.NumberLiteral;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
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
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks money strings localization.
 *
 *  @author Ivan Sergeev
 */
public class MoneyStringLocalizationCheck
    extends AbstractModuleStructureCheck
{
    private static final String MONEYFIELD_TYPE_DESCKRIPRION = "ОписаниеТипаДенежногоПоля"; //$NON-NLS-1$

    private static final String CHECK_ID = "money-string-localization"; //$NON-NLS-1$

    private static final String MONEY_STRING_NAME = "Money string name"; //$NON-NLS-1$

    private static final Set<String> IMMUTABLE_MAP_MONEY_STRING = Set.of("Сумма", "Цена", "Себестоимость", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        "СуммаИзлишков", "Amount", "Price", "Cost", "SurplusAmount", "DescriptionTypesAmount"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_NAMES = String.join(DELIMITER, IMMUTABLE_MAP_MONEY_STRING);

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
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;
        String rawNames = parameters.getString(MONEY_STRING_NAME);
        List<String> moneyNames = List.of(rawNames.split(DELIMITER));
        String rawNamesLower = rawNames.toLowerCase();
        if (ModuleType.FORM_MODULE == module.getModuleType())
        {
            Form form = (Form)module.getOwner();
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
                    displayedAttribute.addAll(findAttribute(refObjects, moneyNames));
                }
            }
            List<Method> methods = BslUtil.allMethods(module);

            Map<Method, List<Statement>> methodStatements = new HashMap<>();
            for (Method method : methods)
            {
                methodStatements.put(method, method.allStatements());
            }

            for (FormAttribute attribute : displayedAttribute)
            {
                List<TypeItem> types = attribute.getValueType().getTypes();
                for (TypeItem type : types)
                {
                    if ("Number".equalsIgnoreCase(McoreUtil.getTypeName(type))) //$NON-NLS-1$
                    {
                        checkStatements(methods, methodStatements, attribute.getName(), moneyNames, resultAceptor);
                    }
                    else if ("ValueTable".equalsIgnoreCase(McoreUtil.getTypeName(type))) //$NON-NLS-1$
                    {
                        List<FormAttributeColumn> columns = attribute.getColumns();
                        for (FormAttributeColumn column : columns)
                        {
                            List<TypeItem> typesColumn = column.getValueType().getTypes();
                            String colName = column.getName();
                            for (TypeItem typeColumn : typesColumn)
                            {
                                if ("Number".equalsIgnoreCase(McoreUtil.getTypeName(typeColumn))) //$NON-NLS-1$
                                {
                                    checkStatements(methods, methodStatements, colName, moneyNames, resultAceptor);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<FormAttribute> findAttribute(List<DataPathReferredObject> refObjects, List<String> moneyNames)
    {
        List<FormAttribute> formAttributes = new ArrayList<>();
        for (DataPathReferredObject dataPathReferredObject : refObjects)
        {
            if (dataPathReferredObject.getObject() instanceof FormAttribute formAttribute)
            {
                for (String moneyStringName : moneyNames)
                {
                    if (moneyStringName.equalsIgnoreCase(formAttribute.getName()))
                    {
                        formAttributes.add(formAttribute);
                    }
                }
            }
        }
        return formAttributes;
    }

    private void checkStatements(List<Method> methods, Map<Method, List<Statement>> methodStatements, String name,
        List<String> moneyNames, ResultAcceptor resultAceptor)
    {
        for (Method method : methods)
        {
            List<Statement> statements = methodStatements.get(method);
            if (statements == null || statements.isEmpty())
            {
                continue;
            }
            Statement statement = searchStatements(statements, name);
            if (statement instanceof SimpleStatement simpleState)
            {
                if (simpleState.getRight() instanceof Invocation right)
                {
                    String invocationName = right.getMethodAccess().getName();
                    //NodeModelUtils.findActualNodeFor(statement).getText()
                    if (!MONEYFIELD_TYPE_DESCKRIPRION.equalsIgnoreCase(invocationName))
                    {
                        resultAceptor.addIssue(Messages.MoneyStringLocalizationCheck_Issue, statement);
                    }
                }
                else if (simpleState.getRight() instanceof OperatorStyleCreator)
                {
                    resultAceptor.addIssue(Messages.MoneyStringLocalizationCheck_Issue, statement);
                }
                else if (simpleState.getRight() instanceof NumberLiteral)
                {
                    resultAceptor.addIssue(Messages.MoneyStringLocalizationCheck_Issue, statement);
                }
            }
        }
    }

    private Statement searchStatements(List<Statement> statements, String name)
    {
        for (Statement statement : statements)
        {
            if (statement instanceof SimpleStatement simp)
            {
                if (simp.getLeft() instanceof Invocation invocation)
                {
                    if ("Добавить".equalsIgnoreCase(invocation.getMethodAccess().getName()) //$NON-NLS-1$
                        || "Вставить".equalsIgnoreCase(invocation.getMethodAccess().getName())) //$NON-NLS-1$
                    {
                        if (!invocation.getParams().isEmpty())
                        {
                            if (invocation.getParams().get(0) instanceof StringLiteral strLit)
                            {
                                String checkName = strLit.getLines().get(0).replace("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
                                if (invocation.getParams().size() <= 1)
                                {
                                    continue;
                                }
                                if (checkName.equalsIgnoreCase(name)
                                        && invocation.getParams().get(1) instanceof StaticFeatureAccess sfa
                                        && !MONEYFIELD_TYPE_DESCKRIPRION.equalsIgnoreCase(sfa.getName())
                                        && !name.equalsIgnoreCase(sfa.getName()))
                                    {
                                        return searchStatements(statements, sfa.getName());
                                    }

                            }
                        }
                    }
                }
                else if (simp.getLeft() instanceof StaticFeatureAccess findSfa)
                {
                    if (name.equalsIgnoreCase(findSfa.getName()))
                        {
                            return statement;
                        }

                }
            }
            else if (statement instanceof IfStatement ifStatement)
            {
                List<Statement> ifStatements = ifStatement.getIfPart().getStatements();
                Statement stat = searchStatements(ifStatements, name);
                if (stat != null)
                {
                    return stat;
                }
                List<Statement> elseStatements = ifStatement.getElseStatements();
                stat = searchStatements(elseStatements, name);
                if (stat != null)
                {
                    return stat;
                }
                List<Conditional> elseIfParts = ifStatement.getElsIfParts();
                for (Conditional conditional : elseIfParts)
                {
                    List<Statement> statementsElsIf = conditional.getStatements();
                    stat = searchStatements(statementsElsIf, name);
                    if (stat != null)
                    {
                        return stat;
                    }
                }
            }
            else if (statement instanceof LoopStatement forStatement)
            {
                List<Statement> forStatements = forStatement.getStatements();
                Statement stat = searchStatements(forStatements, name);
                if (stat != null)
                {
                    return stat;
                }
            }
            else if (statement instanceof TryExceptStatement tryExceptStatement)
            {
                List<Statement> tryStatements = tryExceptStatement.getTryStatements();
                Statement stat = searchStatements(tryStatements, name);
                if (stat != null)
                {
                    return stat;
                }
            }
        }
        return null;
    }
}
