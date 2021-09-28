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
 *     Gukov Viktor - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.ql.check;

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.QUERY_SCHEMA_OPERATOR__TABLES_FOR_UPDATE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.DefaultDataLockControlMode;
import com._1c.g5.v8.dt.ql.model.QuerySchemaOperator;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.google.inject.Inject;

/**
 * The check of query FOR UPDATE using
 * 
 * @author Gukov Viktor
 */
public class ForUpdateUsingCheck
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-for-update-using"; //$NON-NLS-1$
    private final IConfigurationProvider configurationProvider;

    @Inject
    public ForUpdateUsingCheck(IConfigurationProvider provider)
    {
        this.configurationProvider = provider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor acceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }

        Configuration configuration = configurationProvider.getConfiguration(object);
        if (configuration.getDataLockControlMode() != DefaultDataLockControlMode.MANAGED)
        {
            return;
        }

        QuerySchemaOperator schemaOperator = (QuerySchemaOperator)object;
        if (schemaOperator.isSelectForUpdate())
        {
            acceptor.addIssue(Messages.ForUpdateUsingCheck_title, object, QUERY_SCHEMA_OPERATOR__TABLES_FOR_UPDATE);
        }
    }

    @Override
    protected void configureCheck(CheckConfigurer configurer)
    {
        configurer.title(Messages.ForUpdateUsingCheck_title)
            .description(Messages.ForUpdateUsingCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .delegate(QuerySchemaOperator.class);

    }

}
