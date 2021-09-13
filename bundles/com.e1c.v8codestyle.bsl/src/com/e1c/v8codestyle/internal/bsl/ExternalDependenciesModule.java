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

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.wiring.AbstractServiceAwareModule;

/**
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
        bind(IRuntimeVersionSupport.class).toService();
        bind(IBslPreferences.class).toService();
    }

}
