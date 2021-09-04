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
 * Checks that only standard role has right: {@code ALL_FUNCTIONS_MODE} for configuration root.
 *
 * @author Aleksandr Kapralov
 *
 */
public class AllFunctionsModeRight
    extends RoleRightSetCheck
{

    private static final String NAMES_DEFAULT =
        "РежимВсеФункции,AllFunctionsMode,АдминистраторСистемы,SystemAdministrator"; //$NON-NLS-1$

    private static final String CHECK_ID = "all-functions-mode-right"; //$NON-NLS-1$

    @Inject
    public AllFunctionsModeRight(IV8ProjectManager v8ProjectManager, IBmModelManager bmModelManager)
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
            .title(Messages.AllFunctionsModeRight_title)
            .description(Messages.AllFunctionsModeRight_description);
    }

    @Override
    protected RightName getRightName()
    {
        return RightName.ALL_FUNCTIONS_MODE;
    }

}
