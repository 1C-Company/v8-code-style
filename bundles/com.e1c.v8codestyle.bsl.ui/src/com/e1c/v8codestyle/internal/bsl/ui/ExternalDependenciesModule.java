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
package com.e1c.v8codestyle.internal.bsl.ui;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.core.filesystem.IQualifiedNameFilePathConverter;
import com._1c.g5.v8.dt.core.model.IModelEditingSupport;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.wiring.AbstractServiceAwareModule;
import com.e1c.g5.v8.dt.check.qfix.IFixRepository;
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;

/**
 * The external dependencies for plugin
 *
 * @author Dmitriy Marmyshev
 *
 */
class ExternalDependenciesModule
    extends AbstractServiceAwareModule
{

    ExternalDependenciesModule(Plugin plugin)
    {
        super(plugin);
    }

    @Override
    protected void doConfigure()
    {
        // V8 services
        bind(IResourceLookup.class).toService();
        bind(IBslPreferences.class).toService();
        bind(IQualifiedNameFilePathConverter.class).toService();
        bind(IV8ProjectManager.class).toService();
        bind(IModelEditingSupport.class).toService();
        bind(IFixRepository.class).toService();

        // CodeStyle Services
        bind(IModuleStructureProvider.class).toService();
    }

}
