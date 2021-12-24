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

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.QUERY_SCHEMA_QUERY_SOURCE_JOIN__SOURCE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.ql.model.QuerySchemaNestedQuery;
import com._1c.g5.v8.dt.ql.model.QuerySchemaQuerySourceJoin;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.ql.CorePlugin;

/**
 * The check of QL query that join with sub query, which lead to performance decrease.
 *
 * @author Dmitriy Marmyshev
 */
public class JoinToSubQuery
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-join-to-sub-query"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.JoinToSubQuery_title)
            .description(Messages.JoinToSubQuery_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .delegate(QuerySchemaQuerySourceJoin.class);
    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor resultAceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {
        QuerySchemaQuerySourceJoin join = (QuerySchemaQuerySourceJoin)object;

        if (join.getSource() != null && join.getSource().getSource() instanceof QuerySchemaNestedQuery)
        {
            resultAceptor.addIssue(Messages.JoinToSubQuery_Query_join_to_sub_query_not_allowed, object,
                QUERY_SCHEMA_QUERY_SOURCE_JOIN__SOURCE);
        }
    }

}
