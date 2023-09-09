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
import com._1c.g5.v8.dt.rights.IRightInfosService;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.right.CorePlugin;
import com.google.inject.Inject;

/**
 * Checks that only standard role has right: {@code INTERACTIVE_OPEN_EXT_DATA_PROCESSORS} for configuration root.
 *
 * @author Aleksandr Kapralov
 *
 */
public class RightInteractiveOpenExternalDataProcessors
    extends RoleRightSetCheck
{

    private static final List<String> NAMES_DEFAULT =
        List.of("ИнтерактивноеОткрытиеВнешнихОтчетовИОбработок", "InteractiveOpenExternalReportsAndDataProcessors"); //$NON-NLS-1$ //$NON-NLS-2$

    private static final String CHECK_ID = "right-interactive-open-external-data-processors"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Inject
    public RightInteractiveOpenExternalDataProcessors(IResourceLookup resourceLookup,
        IV8ProjectManager v8ProjectManager, IBmModelManager bmModelManager, IBmRightsIndexManager bmRightsIndexManager,
        IBmEmfIndexManager bmEmfIndexManager, IRightInfosService rightInfosService)
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
            .title(Messages.RightInteractiveOpenExternalDataProcessors_title)
            .description(Messages.RightInteractiveOpenExternalDataProcessors_description);
    }

    @Override
    protected RightName getRightName()
    {
        return RightName.INTERACTIVE_OPEN_EXT_DATA_PROCESSORS;
    }

    @Override
    protected boolean needCheckObjectRight()
    {
        return false;
    }

}
