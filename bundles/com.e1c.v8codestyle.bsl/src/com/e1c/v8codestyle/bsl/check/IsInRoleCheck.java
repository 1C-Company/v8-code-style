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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STATIC_FEATURE_ACCESS;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checking the use of the IsInRole method that is not recommended.
 *
 * @author Artem Iliukhin
 */
public class IsInRoleCheck
    extends BasicCheck
{

    private static final String EXCEPTION_ROLES_PARAM = "exceptionRoles"; //$NON-NLS-1$
    private static final String NAME = "IsInRole"; //$NON-NLS-1$
    private static final String NAME_RU = "РольДоступна"; //$NON-NLS-1$
    private static final String CHECK_ID = "using-isinrole"; //$NON-NLS-1$
    private static final String DEFAULT_EXCEPTION_ROLES_PARAM = ""; //$NON-NLS-1$

    public IsInRoleCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.IsInRoleCheck_Using_IsInRole)
            .description(Messages.IsInRoleCheck_Use_AccessRight)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(689, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(STATIC_FEATURE_ACCESS)
            .parameter(EXCEPTION_ROLES_PARAM, String.class, DEFAULT_EXCEPTION_ROLES_PARAM,
                Messages.IsInRoleCheck_Exception_Roles);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = BslUtil.getInvocation((FeatureAccess)object);
        if (invocation == null)
        {
            return;
        }

        String name = ((StaticFeatureAccess)object).getName();
        if (name.equalsIgnoreCase(NAME_RU) || name.equalsIgnoreCase(NAME))
        {
            final String exRoles = parameters.getString(EXCEPTION_ROLES_PARAM).trim();
            List<String> roles = List.of(exRoles.split("[\\s,]+")); //$NON-NLS-1$

            List<Expression> params = invocation.getParams();
            if (!params.isEmpty() && params.get(0) instanceof StringLiteral)
            {
                StringLiteral param = (StringLiteral)params.get(0);
                if (!param.getLines().isEmpty() && roles.contains(param.getLines().get(0).replace("\"", ""))) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    return;
                }
            }
            resultAceptor.addIssue(Messages.IsInRoleCheck_Use_AccessRight_instead_IsInRole, object);
        }
    }
}
