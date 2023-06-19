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
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.internal.autosort;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.v8.activitytracking.core.ISystemIdleService;
import com._1c.g5.v8.dt.core.model.IModelEditingSupport;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.core.platform.IDerivedDataManagerProvider;
import com.e1c.g5.v8.dt.cli.api.components.BaseCliCommandExternalDependencyModule;

/**
 * The external dependencies for AutoSort plugin
 *
 * @author Dmitriy Marmyshev
 */
class ExternalDependenciesModule
    extends BaseCliCommandExternalDependencyModule
{

    ExternalDependenciesModule(Plugin plugin)
    {
        super(plugin);
    }

    @Override
    protected void doConfigure()
    {
        super.doConfigure();
        // V8 DT
        bind(IConfigurationProvider.class).toService();
        bind(IModelEditingSupport.class).toService();
        bind(ISystemIdleService.class).toService();
        bind(IDerivedDataManagerProvider.class).toService();
    }

}
