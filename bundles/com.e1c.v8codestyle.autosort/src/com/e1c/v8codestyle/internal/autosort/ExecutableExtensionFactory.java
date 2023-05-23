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

import org.osgi.framework.Bundle;

import com.e1c.g5.v8.dt.cli.api.components.BaseCliCommandExtensionFactory;
import com.google.inject.Injector;

/**
 * Guice module aware executable extension factory for AutoSort plugin.
 *
 * @author Dmitriy Marmyshev
 */
public class ExecutableExtensionFactory
    extends BaseCliCommandExtensionFactory
{
    @Override
    protected Bundle getBundle()
    {
        return AutoSortPlugin.getDefault().getBundle();
    }

    @Override
    protected Injector getInjector()
    {
        return AutoSortPlugin.getDefault().getInjector();
    }
}
