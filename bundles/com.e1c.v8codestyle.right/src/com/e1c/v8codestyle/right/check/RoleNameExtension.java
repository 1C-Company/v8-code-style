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
 *     Aleksandr Kapralov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.right.check;

import java.util.List;

import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.metadata.mdclass.Role;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com.e1c.g5.v8.dt.check.CheckParameterDefinition;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.ext.ITopObjectFilter;

/**
 * Check extension: exclude roles from check based on role name
 *
 * @author Aleksandr Kapralov
 *
 */
public class RoleNameExtension
    implements IBasicCheckExtension
{
    public static final String ROLE_NAMES_LIST_PARAMETER_NAME = "roleNamesList"; //$NON-NLS-1$

    private final String parameterName;
    private final String defaultValue;
    private final String parameterTitle;

    private final IBmModelManager bmModelManager;

    public RoleNameExtension(final String defaultValue, final IBmModelManager bmModelManager)
    {
        this(ROLE_NAMES_LIST_PARAMETER_NAME, Messages.RoleNameExtension_Role_names_list, defaultValue, bmModelManager);
    }

    public RoleNameExtension(final String parameterName, final String parameterTitle, final String defaultValue,
        final IBmModelManager bmModelManager)
    {
        this.parameterName = parameterName;
        this.parameterTitle = parameterTitle;
        this.defaultValue = defaultValue;
        this.bmModelManager = bmModelManager;
    }

    @Override
    public void configureContextCollector(final ICheckDefinition definition)
    {
        final CheckParameterDefinition parameterDefinition =
            new CheckParameterDefinition(this.parameterName, String.class, this.defaultValue, this.parameterTitle);
        definition.addParameterDefinition(parameterDefinition);
    }

    @Override
    public ITopObjectFilter contributeTopObjectFilter()
    {
        return (IBmObject objectRight, ICheckParameters parameters) -> {
            final String excludeRoleNamePattern = parameters.getString(ROLE_NAMES_LIST_PARAMETER_NAME);
            if (excludeRoleNamePattern == null || excludeRoleNamePattern.isBlank())
            {
                return true;
            }

            IBmModel model = bmModelManager.getModel(objectRight);
            RoleDescription description = EcoreUtil2.getContainerOfType(objectRight, RoleDescription.class);
            Role role = RightsModelUtil.getOwner(description, model);
            if (role == null)
            {
                return true;
            }

            List<String> roleNames = List.of(excludeRoleNamePattern.replace(" ", "").split(",")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            return roleNames.stream().noneMatch(s -> role.getName().equalsIgnoreCase(s));
        };
    }
}
