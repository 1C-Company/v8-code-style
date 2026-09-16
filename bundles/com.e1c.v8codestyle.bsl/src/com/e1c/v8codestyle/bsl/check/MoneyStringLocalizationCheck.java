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

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.NumberLiteral;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormAttributeColumn;
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
 * Checks that variable is self assign.
 *
 *  @author Ivan Sergeev
 */
public class MoneyStringLocalizationCheck
    extends AbstractModuleStructureCheck
{
    private static final String MONEYFIELD_TYPE_DESCKRIPRION = "ОписаниеТипаДенежногоПоля"; //$NON-NLS-1$

    private static final String CHECK_ID = "money-string-localization"; //$NON-NLS-1$

    private static final String MONEY_STRING_NAME = "Money string name"; //$NON-NLS-1$

    private static final Set<String> IMMUTABLE_MAP_MONEY_STRING =
        Set.of("Сумма", "Цена", "Себестоимость", "ОписаниеТиповСумма", "СуммаИзлишков"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

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
        if (ModuleType.FORM_MODULE == module.getModuleType())
        {
            Form form = (Form)module.getOwner();
            List<FormAttribute> attributes = form.getAttributes();
            List<Method> methods = BslUtil.allMethods(module);
            for (FormAttribute attribute : attributes)
            {
                List<TypeItem> types = attribute.getValueType().getTypes();
                for (TypeItem type : types)
                {
                    if ("Number".equalsIgnoreCase(McoreUtil.getTypeName(type))) //$NON-NLS-1$
                    {
                        for (Method method : methods)
                        {
                            List<Statement> statements = method.allStatements();
                            if (!statements.isEmpty())
                            {
                                Statement statement = searchStatements(statements, attribute.getName(), parameters);
                                if (statement instanceof SimpleStatement simpleState)
                                {
                                    if (simpleState.getRight() instanceof Invocation right)
                                    {
                                        String name = right.getMethodAccess().getName();
                                        if (!MONEYFIELD_TYPE_DESCKRIPRION.equalsIgnoreCase(name))
                                        {
                                            resultAceptor.addIssue(Messages.MoneyStringLocalizationCheck_Issue,
                                                statement);
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
                                    for (Method method : methods)
                                    {
                                        List<Statement> statements = method.allStatements();
                                        Statement statement = searchStatements(statements, colName, parameters);
                                        if (statement instanceof SimpleStatement simpleState)
                                        {
                                            if (simpleState.getRight() instanceof Invocation right)
                                            {
                                                String name = right.getMethodAccess().getName();
                                                if (!MONEYFIELD_TYPE_DESCKRIPRION.equalsIgnoreCase(name))
                                                {
                                                    resultAceptor.addIssue(Messages.MoneyStringLocalizationCheck_Issue,
                                                        statement);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            List<Method> methods = module.allMethods();
            String names = parameters.getString(MONEY_STRING_NAME);
            String[] namesList = names.split(DELIMITER);
            for (Method method : methods)
            {
                List<Statement> statements = method.allStatements();
                if (!statements.isEmpty())
                {
                    for (String name : namesList)
                    {
                        Statement statement = searchStatements(statements, name, parameters);
                        if (statement instanceof SimpleStatement simpleState)
                        {
                            if (simpleState.getRight() instanceof NumberLiteral)
                            {
                                resultAceptor.addIssue(Messages.MoneyStringLocalizationCheck_Issue, statement);
                            }
                        }
                    }
                }
            }
        }
    }

    private Statement searchStatements(List<Statement> statements, String name, ICheckParameters parameters)
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
                                if (parameters.getString(MONEY_STRING_NAME)
                                    .toLowerCase()
                                    .contains(strLit.getLines().get(0).toLowerCase())
                                    && invocation.getParams().get(1) instanceof StaticFeatureAccess sfa
                                    && !MONEYFIELD_TYPE_DESCKRIPRION.equalsIgnoreCase(sfa.getName()))
                                {
                                    return searchStatements(statements, sfa.getName(), parameters);
                                }
                            }
                        }
                    }
                }
                else if (simp.getLeft() instanceof StaticFeatureAccess findSfa)
                {
                    if (parameters.getString(MONEY_STRING_NAME).toLowerCase().contains(findSfa.getName().toLowerCase())
                        && findSfa.getName().equalsIgnoreCase(name))
                    {
                        return statement;
                    }
                }
            }
            else if (statement instanceof IfStatement ifStatement)
            {
                List<Statement> ifStatements = ifStatement.getIfPart().getStatements();
                Statement stat = searchStatements(ifStatements, name, parameters);
                if (stat != null)
                {
                    return stat;
                }
                List<Statement> elseStatements = ifStatement.getElseStatements();
                stat = searchStatements(elseStatements, name, parameters);
                if (stat != null)
                {
                    return stat;
                }
                List<Conditional> elseIfParts = ifStatement.getElsIfParts();
                for (Conditional conditional : elseIfParts)
                {
                    List<Statement> statementsElsIf = conditional.getStatements();
                    stat = searchStatements(statementsElsIf, name, parameters);
                    if (stat != null)
                    {
                        return stat;
                    }
                }
            }
            else if (statement instanceof ForStatement forStatement)
            {
                List<Statement> forStatements = forStatement.getStatements();
                Statement stat = searchStatements(forStatements, name, parameters);
                if (stat != null)
                {
                    return stat;
                }
            }

        }
        return null;
    }
}
