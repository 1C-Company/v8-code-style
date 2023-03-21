/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STRING_LITERAL__LINES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION__ROLES;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.common.StringUtils;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check the method IsInRole, that first param contains exists roles.
 * @author Vadim Goncharov
 */
public class IsInRoleMethodRoleExistCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "method-isinrole-role-exist"; //$NON-NLS-1$

    private static final String METHOD_ISINROLE_NAME = "IsInRole"; //$NON-NLS-1$

    private static final String METHOD_ISINROLE_NAME_RU = "РольДоступна"; //$NON-NLS-1$
    
    private static final String ROLE_FQN_FIRST_SEGMENT = "Role.";

    private final IScopeProvider scopeProvider;

    private final IQualifiedNameConverter qualifiedNameConverter;

    /**
     * Instantiates a new invocation role check access exist role check.
     *
     * @param scopeProvider the scope provider
     * @param qualifiedNameConverter the qualified name converter
     */
    @Inject
    public IsInRoleMethodRoleExistCheck(IScopeProvider scopeProvider, IQualifiedNameConverter qualifiedNameConverter)
    {
        super();
        this.scopeProvider = scopeProvider;
        this.qualifiedNameConverter = qualifiedNameConverter;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.IsInRoleMethodRoleExistCheck_title)
            .description(Messages.IsInRoleMethodRoleExistCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.WARNING)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        Invocation inv = (Invocation)object;
        if (monitor.isCanceled() || !isValidInvocation(inv))
        {
            return;
        }

        EList<Expression> params = inv.getParams();
        if (monitor.isCanceled() || params.isEmpty() || !(params.get(0) instanceof StringLiteral))
        {
            return;
        }

        StringLiteral literal = (StringLiteral)params.get(0);
        String roleName = literal.lines(true).get(0);

        if (StringUtils.isEmpty(roleName))
        {
            return;
        }

        IEObjectDescription roleDesc = getRoleDescFromScope(inv, roleName);
        if (!monitor.isCanceled() && roleDesc == null)
        {
            String message = MessageFormat.format(
                Messages.IsInRoleMethodRoleExistCheck_Role_named_not_exists_in_configuration, roleName);
            resultAcceptor.addIssue(message, literal, STRING_LITERAL__LINES);
        }

    }

    private boolean isValidInvocation(Invocation invocation)
    {

        if (invocation.getMethodAccess() instanceof StaticFeatureAccess)
        {
            StaticFeatureAccess sfa = (StaticFeatureAccess)invocation.getMethodAccess();
            if (sfa.getName().equalsIgnoreCase(METHOD_ISINROLE_NAME)
                || sfa.getName().equalsIgnoreCase(METHOD_ISINROLE_NAME_RU))
            {
                return true;
            }
        }

        return false;

    }

    private IEObjectDescription getRoleDescFromScope(Invocation inv, String roleName)
    {
        IScope scope = scopeProvider.getScope(inv, CONFIGURATION__ROLES);
        return scope.getSingleElement(qualifiedNameConverter.toQualifiedName(ROLE_FQN_FIRST_SEGMENT + roleName));
    }

}
