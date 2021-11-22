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
package com.e1c.v8codestyle.ql.check;

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.ABSTRACT_QUERY_SCHEMA_TABLE__TABLE_NAME;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.ql.model.AbstractQuerySchemaTable;
import com._1c.g5.v8.dt.ql.model.QuerySchemaSelectQuery;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;

/**
 * The check of QL query that selection put into new temporary table should have some indexes.
 * Usually it needs to index join fields with some other table, order fields, filter fields, grouping by fields.
 * This check may be enhanced in the future.
 *
 * @author Dmitriy Marmyshev
 */
public class TempTableHasIndex
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-temp-table-index"; //$NON-NLS-1$

    private static final String PARAMETER_EXCLUDE_TABLE_NAME_PATTERN = "excludeObjectNamePattern"; //$NON-NLS-1$

    private static final int MAX_TOP = 1000;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.TempTableHasIndex_title)
            .description(Messages.TempTableHasIndex_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .delegate(QuerySchemaSelectQuery.class);
        builder.parameter(PARAMETER_EXCLUDE_TABLE_NAME_PATTERN, String.class, StringUtils.EMPTY,
            Messages.TempTableHasIndex_Exclude_table_name_pattern);
    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor resultAceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {
        QuerySchemaSelectQuery selectQuery = (QuerySchemaSelectQuery)object;
        if (selectQuery.getPlacementTable() == null || isTopLessThenThousand(selectQuery))
        {
            return;
        }

        if (selectQuery.getIndexes() == null || selectQuery.getIndexes().isEmpty())
        {
            AbstractQuerySchemaTable table = selectQuery.getPlacementTable();
            String excludeTableNamePattern = parameters.getString(PARAMETER_EXCLUDE_TABLE_NAME_PATTERN);
            if (excludeTableNamePattern != null && !excludeTableNamePattern.isBlank()
                && table.getFullTableName().matches(excludeTableNamePattern))
            {
                return;
            }
            resultAceptor.addIssue(Messages.TempTableHasIndex_New_temporary_table_should_have_indexes, table,
                ABSTRACT_QUERY_SCHEMA_TABLE__TABLE_NAME);
        }
    }

    private boolean isTopLessThenThousand(QuerySchemaSelectQuery selectQuery)
    {
        if (!selectQuery.getOperators().isEmpty() && selectQuery.getOperators().get(0).getGetRecordsCount() != null)
        {
            String count = selectQuery.getOperators().get(0).getGetRecordsCount();
            try
            {
                int top = Integer.parseInt(count);
                return top < MAX_TOP;
            }
            catch (NumberFormatException e)
            {
                // do nothing
            }
        }
        return false;
    }

}
