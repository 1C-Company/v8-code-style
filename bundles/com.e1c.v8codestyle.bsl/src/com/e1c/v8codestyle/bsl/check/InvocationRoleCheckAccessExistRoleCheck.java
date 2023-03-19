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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.Role;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check the functions IsInRole and Users.RolesAvailable (SSL), that first param contains exists roles.
 * @author Vadim Goncharov
 */
public class InvocationRoleCheckAccessExistRoleCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "bsl-invocation-role-check-access-exist-role"; //$NON-NLS-1$

    private static final String METHOD_ISINROLE_NAME = "IsInRole"; //$NON-NLS-1$

    private static final String METHOD_ISINROLE_NAME_RU = "РольДоступна"; //$NON-NLS-1$

    private static final String COMMONMODULE_USERS_NAME = "Users"; //$NON-NLS-1$

    private static final String COMMONMODULE_USERS_NAME_RU = "Пользователи"; //$NON-NLS-1$

    private static final String METHOD_ISINROLES_NAME = "RolesAvailable"; //$NON-NLS-1$

    private static final String METHOD_ISINROLES_NAME_RU = "РолиДоступны"; //$NON-NLS-1$

    private final IConfigurationProvider configurationProvider;

    /**
     * Instantiates a new invocation role check access exist role check.
     *
     * @param configurationProvider the configuration provider
     */
    @Inject
    public InvocationRoleCheckAccessExistRoleCheck(IConfigurationProvider configurationProvider)
    {
        super();
        this.configurationProvider = configurationProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("Referring to a non-existent role")
            .description("Referring to a non-existent role")
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
        Map<String, Role> map = getMapOfRoles(literal);

        if (monitor.isCanceled() || map.isEmpty())
        {
            return;
        }

        processMap(map, configurationProvider.getConfiguration(literal), monitor);
        if (monitor.isCanceled())
        {
            return;
        }

        for (Map.Entry<String, Role> item : map.entrySet())
        {
            if (item.getValue() == null)
            {
                String message = MessageFormat.format("Role named {0} not exists in configuration", item.getKey());
                resultAcceptor.addIssue(message, literal, STRING_LITERAL__LINES);
            }
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

        if (invocation.getMethodAccess() instanceof DynamicFeatureAccess)
        {
            DynamicFeatureAccess dfa = (DynamicFeatureAccess)invocation.getMethodAccess();
            Expression source = dfa.getSource();
            if (source instanceof StaticFeatureAccess && isSSLUsersMethod((StaticFeatureAccess)source, dfa))
            {
                return true;

            }
        }

        return false;

    }

    private boolean isSSLUsersMethod(StaticFeatureAccess sfa, DynamicFeatureAccess dfa)
    {
        return ((sfa.getName().equalsIgnoreCase(COMMONMODULE_USERS_NAME)
            || sfa.getName().equalsIgnoreCase(COMMONMODULE_USERS_NAME_RU))
            && (dfa.getName().equalsIgnoreCase(METHOD_ISINROLES_NAME)
                || dfa.getName().equalsIgnoreCase(METHOD_ISINROLES_NAME_RU)));
    }

    private Map<String, Role> getMapOfRoles(StringLiteral literal)
    {
        String content = String.join("", literal.lines(true)); //$NON-NLS-1$
        String[] roles = content.replace(" ", "").split(","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Map<String, Role> map = new HashMap<>();
        for (String s : roles)
        {
            map.put(s, null);
        }

        return map;
    }

    private void processMap(Map<String, Role> map, Configuration configuration, IProgressMonitor monitor)
    {
        for (Role role : configuration.getRoles())
        {

            if (monitor.isCanceled())
            {
                return;
            }
            
            if (map.containsKey(role.getName()))
            {
                map.put(role.getName(),role);
            }

        }
    }

}
