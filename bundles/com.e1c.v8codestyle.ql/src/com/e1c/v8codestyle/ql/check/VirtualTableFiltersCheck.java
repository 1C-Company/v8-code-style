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

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.MULTI_PART_COMMON_EXPRESSION__SOURCE_TABLE;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.dt.metadata.dbview.DbViewElement;
import com._1c.g5.v8.dt.metadata.dbview.DbViewSelectDef;
import com._1c.g5.v8.dt.metadata.mdclass.RegisterDimension;
import com._1c.g5.v8.dt.ql.model.AbstractQuerySchemaTable;
import com._1c.g5.v8.dt.ql.model.CommonExpression;
import com._1c.g5.v8.dt.ql.model.MultiPartCommonExpression;
import com._1c.g5.v8.dt.ql.model.QuerySchemaExpression;
import com._1c.g5.v8.dt.ql.model.QuerySchemaOperator;
import com._1c.g5.v8.dt.ql.model.QuerySchemaTable;
import com._1c.g5.v8.dt.ql.typesystem.IDynamicDbViewFieldComputer;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.v8codestyle.internal.ql.CorePlugin;

/**
 * Checks that all dimensions of virtual table placed in parameters instead of filter.
 *
 * @author Dmitriy Marmyshev
 */
public class VirtualTableFiltersCheck
    extends QlBasicDelegateCheck
{
    private static final String CHECK_ID = "ql-virtual-table-filters"; //$NON-NLS-1$

    private final IDynamicDbViewFieldComputer dynamicDbViewFieldComputer;

    public VirtualTableFiltersCheck()
    {
        super();
        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.qldcs")); //$NON-NLS-1$

        this.dynamicDbViewFieldComputer = rsp.get(IDynamicDbViewFieldComputer.class);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.VirtualTableFiltersCheck_title)
            .description(Messages.VirtualTableFiltersCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PERFORMANCE)
            .delegate(QuerySchemaTable.class);

    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor resultAceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {
        QuerySchemaTable sourceTable = (QuerySchemaTable)object;

        if (!isVirtualTableWithParameters(sourceTable.getTable()))
        {
            return;
        }
        QuerySchemaOperator operator = EcoreUtil2.getContainerOfType(sourceTable, QuerySchemaOperator.class);
        if (monitor.isCanceled() || operator == null || operator.getFilters() == null)
        {
            return;
        }

        QuerySchemaExpression filters = operator.getFilters();
        String alias = sourceTable.getAlias();
        alias = alias + "."; //$NON-NLS-1$

        List<MultiPartCommonExpression> filterItems = new ArrayList<>();
        for (TreeIterator<EObject> iterator = filters.eAllContents(); iterator.hasNext();)
        {
            EObject filter = iterator.next();
            if (monitor.isCanceled())
            {
                return;
            }
            if (filter instanceof MultiPartCommonExpression
                && ((MultiPartCommonExpression)filter).getFullContent().startsWith(alias)
                && isDimension((MultiPartCommonExpression)filter))
            {
                filterItems.add((MultiPartCommonExpression)filter);
            }
        }

        for (MultiPartCommonExpression item : filterItems)
        {
            String message = MessageFormat.format(
                Messages.VirtualTableFiltersCheck_Filter__0_for_virtual_table__1__should_be_in_parameters,
                item.getFullContent(), sourceTable.getAlias());
            resultAceptor.addIssue(message, item, MULTI_PART_COMMON_EXPRESSION__SOURCE_TABLE);
        }
    }

    private boolean isVirtualTableWithParameters(AbstractQuerySchemaTable table)
    {
        try
        {
            DbViewElement dbView = dynamicDbViewFieldComputer.computeDbView(table);
            if (dbView instanceof DbViewSelectDef)
            {
                DbViewSelectDef virtualTable = (DbViewSelectDef)dbView;
                return !virtualTable.getParams().isEmpty();
            }
        }
        catch (Exception e)
        {
            // Log in case model is damaged
            CorePlugin.logError(e);
        }

        return false;
    }

    private boolean isDimension(CommonExpression object)
    {
        try
        {
            DbViewElement dbView = dynamicDbViewFieldComputer.computeDbView(object);
            return dbView.getMdObject() instanceof RegisterDimension;
        }
        catch (Exception e)
        {
            // Log in case model is damaged
            CorePlugin.logError(e);
        }

        return false;
    }

}
