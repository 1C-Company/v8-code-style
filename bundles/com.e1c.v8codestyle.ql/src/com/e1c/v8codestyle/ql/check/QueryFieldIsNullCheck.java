/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *     Denis Maslennikov  - issue #163
 *******************************************************************************/
package com.e1c.v8codestyle.ql.check;

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.COMMON_EXPRESSION__CONTENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.ql.model.AbstractExpression;
import com._1c.g5.v8.dt.ql.model.CaseBody;
import com._1c.g5.v8.dt.ql.model.CaseOperationExpression;
import com._1c.g5.v8.dt.ql.model.CommonExpression;
import com._1c.g5.v8.dt.ql.model.FunctionExpression;
import com._1c.g5.v8.dt.ql.model.FunctionInvocationExpression;
import com._1c.g5.v8.dt.ql.model.IsNullOperatorExpression;
import com._1c.g5.v8.dt.ql.model.MultiPartCommonExpression;
import com._1c.g5.v8.dt.ql.model.QuerySchemaExpression;
import com._1c.g5.v8.dt.ql.model.QuerySchemaOperator;
import com._1c.g5.v8.dt.ql.model.QuerySchemaOrderExpression;
import com._1c.g5.v8.dt.ql.model.QuerySchemaSelectQuery;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.ql.CorePlugin;

/**
 * This class checks if null is checked when sorting by query field.
 *
 * @author Denis Maslennikov
 */
public class QueryFieldIsNullCheck
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-query-field-isnull"; //$NON-NLS-1$
    private static final String METHOD_DELIMITER = ","; //$NON-NLS-1$
    private static final String ISNULL_METHODS = "ISNULL,ЕСТЬNULL"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.QueryFieldIsNullCheck_title)
            .description(Messages.QueryFieldIsNullCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.PORTABILITY)
            .extension(new StandardCheckExtension(412, getCheckId(), CorePlugin.PLUGIN_ID))
            .delegate(QuerySchemaSelectQuery.class);
    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor resultAceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {
        QuerySchemaSelectQuery sourceTable = (QuerySchemaSelectQuery)object;

        if (monitor.isCanceled())
        {
            return;
        }

        List<CommonExpression> orderFields = getOrderFields(sourceTable);
        List<QuerySchemaOperator> operators = sourceTable.getOperators();
        for (QuerySchemaOperator operator : operators)
        {
            List<QuerySchemaExpression> fields = operator.getSelectFields();
            for (QuerySchemaExpression field : fields)
            {
                AbstractExpression abstractExpression = field.getExpression();
                String fieldName = field.getAlias();
                if (abstractExpression instanceof FunctionInvocationExpression)
                {
                    removeFieldsWithIsNullMethod((FunctionInvocationExpression)abstractExpression, orderFields,
                        fieldName);
                }
                if (abstractExpression instanceof CaseOperationExpression)
                {
                    removeFieldsWithWhenExpression((CaseOperationExpression)abstractExpression, orderFields, fieldName);
                }
            }
        }
        for (CommonExpression orderField : orderFields)
        {
            String message = Messages.QueryFieldIsNullCheck_Query_missing_NULL_check_for_field_potentially_contain_NULL;
            resultAceptor.addIssue(message, orderField, COMMON_EXPRESSION__CONTENT);
        }
    }

    private void removeFieldsWithWhenExpression(CaseOperationExpression caseInvocationExpression,
        List<CommonExpression> orderFields, String fieldName)
    {
        List<CaseBody> caseBodies = caseInvocationExpression.getBody();
        for (CaseBody caseBody : caseBodies)
        {
            AbstractExpression whenExpression = caseBody.getWhen();
            if (whenExpression instanceof IsNullOperatorExpression)
            {
                IsNullOperatorExpression nullOperator = (IsNullOperatorExpression)whenExpression;
                for (EObject operatorContent : nullOperator.eContents())
                {
                    if (operatorContent instanceof CommonExpression)
                    {
                        removeFieldByName(orderFields, (CommonExpression)operatorContent, fieldName);
                    }
                }
            }
        }
    }

    private void removeFieldsWithIsNullMethod(FunctionInvocationExpression functionInvocationExpression,
        List<CommonExpression> orderFields, String fieldName)
    {
        FunctionExpression f = functionInvocationExpression.getFunctionType();
        List<String> isNullMethods = getIsNullMethods();

        if (isNullMethods.contains(f.getName()))
        {
            List<AbstractExpression> params = functionInvocationExpression.getParams();

            if (params.get(0) instanceof CommonExpression)
            {
                removeFieldByName(orderFields, (CommonExpression)params.get(0), fieldName);
            }

        }
    }

    private void removeFieldByName(List<CommonExpression> orderFields, CommonExpression commonExpression,
        String fieldName)
    {
        String paramName = commonExpression.getFullContent();
        List<CommonExpression> unique = new ArrayList<>();
        unique.addAll(orderFields);
        for (CommonExpression orderField : unique)
        {
            String fieldFullName = orderField.getFullContent();
            if (paramName.equals(fieldFullName) || fieldName.equals(fieldFullName))
            {
                orderFields.remove(orderField);
            }
        }
    }

    private List<CommonExpression> getOrderFields(QuerySchemaSelectQuery sourceTable)
    {
        List<QuerySchemaOrderExpression> orderExpressions = sourceTable.getOrderExpressions();
        List<CommonExpression> orderFields = new ArrayList<>();

        for (QuerySchemaOrderExpression orderExpression : orderExpressions)
        {
            List<CommonExpression> commonExpressions =
                EcoreUtil2.getAllContentsOfType(orderExpression, CommonExpression.class);
            orderFields.addAll(commonExpressions);
            for (CommonExpression commonExpression : commonExpressions)
            {
                if (commonExpression instanceof MultiPartCommonExpression)
                {
                    orderFields.remove(((MultiPartCommonExpression)commonExpression).getSourceTable());
                }
            }
        }
        return orderFields;
    }

    private List<String> getIsNullMethods()
    {
        List<String> isNullMethods = Arrays.asList(ISNULL_METHODS.split(METHOD_DELIMITER));
        isNullMethods.replaceAll(String::trim);
        return isNullMethods;
    }
}
