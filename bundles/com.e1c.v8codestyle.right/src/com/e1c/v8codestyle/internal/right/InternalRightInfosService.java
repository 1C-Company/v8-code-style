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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.v8.dt.platform.version.Version;
import com._1c.g5.v8.dt.rights.model.Right;
import com._1c.g5.v8.dt.rights.ui.editors.controllers.IRightInfosService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * This service is a correct wrapper of {@link IRightInfosService} to get correct data corresponding
 * project runtime version.
 * <br>
 * FIXME Replace this service after refactoring of {@link IRightInfosService}.
 *
 * @author Dmitriy Marmyshev
 */
@Singleton
public class InternalRightInfosService
{

    private final IRuntimeVersionSupport runtimeVersionSupport;

    private final IConfigurationProvider configurationProvider;

    private final Provider<IRightInfosService> rightInfosServiceProvider;

    private final Map<Version, IRightInfosService> infos = new ConcurrentHashMap<>();

    @Inject
    public InternalRightInfosService(IRuntimeVersionSupport runtimeVersionSupport,
        IConfigurationProvider configurationProvider, Provider<IRightInfosService> rightInfosServiceProvider)
    {
        this.runtimeVersionSupport = runtimeVersionSupport;
        this.configurationProvider = configurationProvider;
        this.rightInfosServiceProvider = rightInfosServiceProvider;
    }

    /**
     * Gets the rights of some {@link EClass} of configuration metadata object that may has some rights.
     *
     * @param eClass the {@link EClass} of configuration metadata object that has some rights, cannot be {@code null}.
     * @param context the context object of the configuration to determine platform version, cannot be {@code null}.
     * @return the rights of the {@link EClass}
     */
    public Set<Right> getEClassRights(EClass eClass, EObject context)
    {

        Version version = runtimeVersionSupport.getRuntimeVersionOrDefault(context, Version.LATEST);

        IRightInfosService info = infos.computeIfAbsent(version, v -> {

            IRightInfosService service = rightInfosServiceProvider.get();
            Configuration configuration = configurationProvider.getConfiguration(context);
            service.init(configuration);

            return service;
        });

        if (info == null)
        {
            return Collections.emptySet();
        }
        return info.getEClassRights(eClass);
    }
}
