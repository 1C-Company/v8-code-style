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

import com.e1c.v8codestyle.IProjectOptionManager;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Guice Module to register services for public and internal use.
 *
 * @author Dmitriy Marmyshev
 */
public class ServiceModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(IProjectOptionManager.class).to(ProjectOptionManager.class).in(Singleton.class);
    }

}
