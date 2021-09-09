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

import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com.google.inject.Inject;

/**
 * Checks that only standard role has right: {@code ACTIVE_USERS} for configuration root.
 *
 * @author Aleksandr Kapralov
 *
 */
public class RightActiveUsers
    extends RoleRightSetCheck
{

    private static final List<String> NAMES_DEFAULT = List.of("Администрирование", "Administration", "ПолныеПрава", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        "FullAccess", "АдминистраторСистемы", "SystemAdministrator"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final String CHECK_ID = "right-active-users"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Inject
    public RightActiveUsers(IV8ProjectManager v8ProjectManager, IBmModelManager bmModelManager)
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
        builder.extension(new ExcludeRoleByNameListExtension(NAMES_DEFAULT, bmModelManager))
            .title(Messages.RightActiveUsers_title)
            .description(Messages.RightActiveUsers_description);
    }

    @Override
    protected RightName getRightName()
    {
        return RightName.ACTIVE_USERS;
    }

    @Override
    protected boolean needCheckObjectRight()
    {
        return false;
    }

}
