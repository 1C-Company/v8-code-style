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
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.google.inject.Inject;

/**
 * @author Aleksandr Kapralov
 *
 */
public class InteractiveOpenExternalReportsRight
    extends RoleRightSetCheck
{

    private static final String NAMES_DEFAULT =
        "ИнтерактивноеОткрытиеВнешнихОтчетовИОбработок,InteractiveOpenExternalReportsAndDataProcessors"; //$NON-NLS-1$

    private static final String CHECK_ID = "interactive-open-external-reports-right"; //$NON-NLS-1$

    private static final String STANDART_ROLE = RightName.INTERACTIVE_OPEN_EXT_REPORTS.getName();

    @Inject
    public InteractiveOpenExternalReportsRight(IV8ProjectManager v8ProjectManager, IBmModelManager bmModelManager)
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
        builder.severity(IssueSeverity.MAJOR)
            .title(Messages.InteractiveOpenExternalReportsRight_title)
            .description(Messages.InteractiveOpenExternalReportsRight_description);
    }

    @Override
    protected String getStandartRoleNames()
    {
        return NAMES_DEFAULT;
    }

    @Override
    protected String getAllowedRightName()
    {
        return STANDART_ROLE;
    }

}
