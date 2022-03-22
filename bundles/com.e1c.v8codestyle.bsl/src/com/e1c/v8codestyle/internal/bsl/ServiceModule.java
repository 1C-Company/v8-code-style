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
package com.e1c.v8codestyle.internal.bsl;

import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextBslModuleFixProvider;
import com.e1c.v8codestyle.bsl.qfix.external.XtextBslModuleFixProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Internal services of the bundle.
 *
 * @author Dmitriy Marmyshev
 */
public class ServiceModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(IModuleStructureProvider.class).to(ModuleStructureProvider.class).in(Singleton.class);

        // Remove this after 2022.1
        bind(IXtextBslModuleFixProvider.class).to(XtextBslModuleFixProvider.class).in(Singleton.class);
    }

}
