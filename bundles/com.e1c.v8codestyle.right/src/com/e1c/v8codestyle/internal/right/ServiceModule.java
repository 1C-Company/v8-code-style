/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.internal.right;

import com._1c.g5.v8.dt.rights.ui.editors.controllers.IRightInfosService;
import com.google.inject.AbstractModule;

/**
 * The internal service module.
 *
 * @author Dmitriy Marmyshev
 */
public class ServiceModule
    extends AbstractModule
{
    private static final String SERVICE_CLASS = "com._1c.g5.v8.dt.internal.rights.ui.editors.RightsInfoService"; //$NON-NLS-1$

    @SuppressWarnings("unchecked")
    @Override
    protected void configure()
    {

        // XXX remove this when the IRightInfosService become OSGi service
        Class<? extends IRightInfosService> clazz = null;
        try
        {
            clazz = (Class<? extends IRightInfosService>)Class.forName(SERVICE_CLASS, true,
                IRightInfosService.class.getClassLoader());
        }
        catch (ClassNotFoundException e)
        {
            CorePlugin.logError(e);
        }
        // Here create new instance every time - this allows to bind it to specific platform Version
        // See com.e1c.v8codestyle.internal.right.InternalRightInfosService for details.
        bind(IRightInfosService.class).to(clazz);
    }
}
