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

import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com.google.inject.Inject;

/**
 * @author Aleksandr Kapralov
 *
 */
public class ConfigurationExtensionsAdministrationRight
    extends RoleRightSetCheck
{

    private static final String NAMES_DEFAULT =
        "Администрирование,Administration,АдминистраторСистемы,SystemAdministrator"; //$NON-NLS-1$

    private static final String CHECK_ID = "configuration-extensions-administration-right"; //$NON-NLS-1$

    @Inject
    public ConfigurationExtensionsAdministrationRight(IV8ProjectManager v8ProjectManager,
        IBmModelManager bmModelManager)
    {
        super(v8ProjectManager, bmModelManager);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        super.configureCheck(builder);
        builder.extension(new RoleNameExtension(NAMES_DEFAULT, bmModelManager))
            .title(Messages.ConfigurationExtensionsAdministrationRight_title)
            .description(Messages.ConfigurationExtensionsAdministrationRight_description);
    }

    @Override
    protected RightName getRightName()
    {
        return RightName.CONFIGURATION_EXTENSIONS_ADMINISTRATION;
    }

}