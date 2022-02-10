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
 *     Aleksandr Kapralov - issue #17
 *******************************************************************************/
package com.e1c.v8codestyle.internal.bsl;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.contextdef.IBslModuleContextDefService;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.wiring.AbstractServiceAwareModule;
import com.e1c.g5.v8.dt.check.qfix.IFixRepository;
import com.e1c.g5.v8.dt.check.settings.ICheckRepository;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextBslModuleFixProvider;
import com.e1c.v8codestyle.bsl.qfix.external.XtextBslModuleFixProvider;

/**
 * The external dependencies for plugin
 *
 * @author Dmitriy Marmyshev
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
        bind(IResourceLookup.class).toService();
        bind(IRuntimeVersionSupport.class).toService();
        bind(IV8ProjectManager.class).toService();
        bind(IBslPreferences.class).toService();
        bind(IQualifiedNameConverter.class).toService();
        bind(IBslModuleContextDefService.class).toService();
        bind(IQualifiedNameProvider.class).toService();

        bind(ICheckRepository.class).toService();
        bind(IFixRepository.class).toService();
        bind(IXtextBslModuleFixProvider.class).to(XtextBslModuleFixProvider.class);
    }
}
