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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.strict.check.AbstractTypeCheck;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Restriction execute external code check.
 *
 *  @author Ivan Sergeev
 */
public class RestrictionExecuteExternalCodeCheck
    extends AbstractTypeCheck
{
    private static final String CHECK_ID = "restriction-execute-external-code"; //$NON-NLS-1$

    @Inject
    public RestrictionExecuteExternalCodeCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IV8ProjectManager v8ProjectManager, IQualifiedNameConverter qualifiedNameConverter,
        INamingService namingService, IBmModelManager bmModelManager, IConfigurationProvider configurationProvider)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RestrictionExecuteExternalCodeCheck_Title)
            .description(Messages.RestrictionExecuteExternalCodeCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.SECURITY)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(SIMPLE_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        if (object instanceof SimpleStatement statement)
        {
            if (statement.getRight() instanceof OperatorStyleCreator right)
            {
                if ("opensslsecureconnection".equalsIgnoreCase(McoreUtil.getTypeName(right.getType()))) //$NON-NLS-1$
                {
                    IBmObject bmObject = bmTransaction.getTopObjectByFqn("Subsystem.СтандартныеПодсистемы"); //$NON-NLS-1$
                    IBmObject bmObjectEn = bmTransaction.getTopObjectByFqn("Subsystem.StandardSubsystems");//$NON-NLS-1$
                    if (bmObject != null || bmObjectEn != null)
                    {
                        resultAceptor.addIssue(Messages.RestrictionExecuteExternalCodeCheck_Issue, right);
                    }
                }
                else if ("HTTPConnection".equalsIgnoreCase(McoreUtil.getTypeName(right.getType()))) //$NON-NLS-1$
                {
                    List<Expression> params = right.getParams();
                    for (Expression expression : params)
                    {
                        if (expression instanceof OperatorStyleCreator rightParam)
                        {
                            if ("opensslsecureconnection".equalsIgnoreCase(McoreUtil.getTypeName(rightParam.getType()))) //$NON-NLS-1$
                            {
                                IBmObject bmObject = bmTransaction.getTopObjectByFqn("Subsystem.СтандартныеПодсистемы"); //$NON-NLS-1$
                                IBmObject bmObjectEn = bmTransaction.getTopObjectByFqn("Subsystem.StandardSubsystems");//$NON-NLS-1$
                                if (bmObject != null || bmObjectEn != null)
                                {
                                    resultAceptor.addIssue(Messages.RestrictionExecuteExternalCodeCheck_Issue,
                                        rightParam);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
