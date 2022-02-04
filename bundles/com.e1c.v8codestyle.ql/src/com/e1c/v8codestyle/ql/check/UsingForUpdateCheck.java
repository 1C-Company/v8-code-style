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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.core.platform.IDependentProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.DefaultDataLockControlMode;
import com._1c.g5.v8.dt.ql.model.QuerySchemaOperator;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.ql.CorePlugin;
import com.google.inject.Inject;

/**
 * The check of query using FOR UPDATE
 *
 * @author Gukov Viktor
 */
public class UsingForUpdateCheck
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-using-for-update"; //$NON-NLS-1$

    private final IConfigurationProvider configurationProvider;

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public UsingForUpdateCheck(IConfigurationProvider configurationProvider, IV8ProjectManager v8ProjectManager)
    {
        this.configurationProvider = configurationProvider;
        this.v8ProjectManager = v8ProjectManager;
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
        IV8Project v8Project = v8ProjectManager.getProject(object);
        IProject project = v8Project.getProject();
        if (v8Project instanceof IDependentProject)
        {
            project = ((IDependentProject)v8Project).getParentProject();
        }

        if (project == null || monitor.isCanceled())
        {
            return;
        }

        Configuration configuration = configurationProvider.getConfiguration(project);
        if (configuration != null && configuration.getDataLockControlMode() != DefaultDataLockControlMode.MANAGED)
        {
            return;
        }

        QuerySchemaOperator schemaOperator = (QuerySchemaOperator)object;
        if (schemaOperator.isSelectForUpdate())
        {
            acceptor.addIssue(Messages.UsingForUpdateCheck_title, object, QUERY_SCHEMA_OPERATOR__TABLES_FOR_UPDATE);
        }
    }

    @Override
    protected void configureCheck(CheckConfigurer configurer)
    {
        configurer.title(Messages.UsingForUpdateCheck_title)
            .description(Messages.UsingForUpdateCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .delegate(QuerySchemaOperator.class);

    }

}
