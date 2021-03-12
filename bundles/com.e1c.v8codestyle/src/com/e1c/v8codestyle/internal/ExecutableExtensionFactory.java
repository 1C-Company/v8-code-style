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
package com.e1c.v8codestyle.internal;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

/**
 * Guice module aware executable extension factory for plugin.
 *
 * @author Dmitriy Marmyshev
 */
public class ExecutableExtensionFactory
    extends AbstractGuiceAwareExecutableExtensionFactory
{
    @Override
    protected Bundle getBundle()
    {
        return CorePlugin.getDefault().getBundle();
    }

    @Override
    protected Injector getInjector()
    {
        return CorePlugin.getDefault().getInjector();
    }
}
