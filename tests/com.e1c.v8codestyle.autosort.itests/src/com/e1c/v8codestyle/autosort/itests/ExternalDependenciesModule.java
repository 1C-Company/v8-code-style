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
package com.e1c.v8codestyle.autosort.itests;

import com._1c.g5.v8.activitytracking.core.ISystemIdleService;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IDerivedDataManagerProvider;
import com._1c.g5.v8.dt.core.platform.IDtProjectManager;
import com._1c.g5.wiring.AbstractServiceAwareModule;
import com.e1c.v8codestyle.autosort.ISortService;
import com.e1c.v8codestyle.internal.autosort.AutoSortPlugin;

/**
 * The external dependencies for AutoSort Integration tests plugin
 *
 * @author Dmitriy Marmyshev
 */
public class ExternalDependenciesModule
    extends AbstractServiceAwareModule
{

    public ExternalDependenciesModule()
    {
        super(AutoSortPlugin.getDefault());
    }

    @Override
    protected void doConfigure()
    {
        // V8 DT
        bind(ISortService.class).toService();
        bind(IBmModelManager.class).toService();
        bind(IDtProjectManager.class).toService();
        bind(ISystemIdleService.class).toService();
        bind(IDerivedDataManagerProvider.class).toService();
    }

}
