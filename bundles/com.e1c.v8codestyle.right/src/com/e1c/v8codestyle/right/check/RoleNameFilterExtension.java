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
 * Check extension, exclude roles from check based on RegEx
 *
 * @author Aleksandr Kapralov
 *
 */
public class RoleNameFilterExtension
    implements IBasicCheckExtension
{
    public static final String EXCLUDE_ROLE_NAME_PATTERN_PARAMETER_NAME = "excludeRoleNamePattern"; //$NON-NLS-1$

    private final String parameterName;
    private final String defaultValue;
    private final String parameterTitle;

    private final IBmModelManager bmModelManager;

    /**
     * Creates new instance with default parameter
     *
     * @param bmModelManager the BM model manager, cannot be {@code null}.
     */
    public RoleNameFilterExtension(final IBmModelManager bmModelManager)
    {
        this(EXCLUDE_ROLE_NAME_PATTERN_PARAMETER_NAME, Messages.RoleFilterExtension_Exclude_Role_name_pattern, "", //$NON-NLS-1$
            bmModelManager);
    }

    /**
     * Creates new instance with custom parameter
     *
     * @param parameterName parameter name for settings file
     * @param parameterTitle parameter title for preferences dialog
     * @param defaultValue default parameter value
     * @param bmModelManager the BM model manager, cannot be {@code null}.
     */
    public RoleNameFilterExtension(final String parameterName, final String parameterTitle, final String defaultValue,
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
            final String excludeRoleNamePattern = parameters.getString(EXCLUDE_ROLE_NAME_PATTERN_PARAMETER_NAME);
            if (excludeRoleNamePattern == null || excludeRoleNamePattern.isBlank())
            {
                return true;
            }

            IBmModel model = bmModelManager.getModel(objectRight);
            RoleDescription description = EcoreUtil2.getContainerOfType(objectRight, RoleDescription.class);
            Role role = RightsModelUtil.getOwner(description, model);

            return role == null || !role.getName().matches(excludeRoleNamePattern);
        };
    }
}
