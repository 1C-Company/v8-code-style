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

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.bm.index.rights.IBmRightsIndexManager;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.right.CorePlugin;
import com.e1c.v8codestyle.internal.right.InternalRightInfosService;
import com.google.inject.Inject;

/**
 * Checks that only standard role has right: {@code EVENT_LOG} for configuration root.
 *
 * @author Aleksandr Kapralov
 *
 */
public class RightViewEventLog
    extends RoleRightSetCheck
{

    private static final List<String> NAMES_DEFAULT = List.of("ПросмотрЖурналаРегистрации", "ViewEventLog", //$NON-NLS-1$ //$NON-NLS-2$
        "ПолныеПрава", "FullAccess", "АдминистраторСистемы", "SystemAdministrator"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private static final String CHECK_ID = "right-view-event-log"; //$NON-NLS-1$

    /**
     * Creates new instance which helps to check role rights for configuration root.
     *
     * @param v8ProjectManager the V8 project manager, cannot be {@code null}.
     * @param bmModelManager  the BM model manager, cannot be {@code null}.
     */
    @Inject
    public RightViewEventLog(IResourceLookup resourceLookup, IV8ProjectManager v8ProjectManager,
        IBmModelManager bmModelManager, IBmRightsIndexManager bmRightsIndexManager,
        IBmEmfIndexManager bmEmfIndexManager, InternalRightInfosService rightInfosService,
        IBmModelManager bmModelManager2)
    {
        super(resourceLookup, v8ProjectManager, bmModelManager, bmRightsIndexManager, bmEmfIndexManager,
            rightInfosService);
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
        builder.extension(new ExcludeRoleByNameListExtension(NAMES_DEFAULT, getBmModelManager()))
            .extension(new StandardCheckExtension(488, getCheckId(), CorePlugin.PLUGIN_ID))
            .title(Messages.RightViewEventLog_title)
            .description(Messages.RightViewEventLog_description);
    }

    @Override
    protected RightName getRightName()
    {
        return RightName.EVENT_LOG;
    }

    @Override
    protected boolean needCheckObjectRight()
    {
        return false;
    }

}
