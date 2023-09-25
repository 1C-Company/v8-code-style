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
package com.e1c.v8codestyle.internal.right.itests;

import org.osgi.framework.FrameworkUtil;

import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IDerivedDataManagerProvider;
import com._1c.g5.v8.dt.core.platform.IDtProjectManager;
import com._1c.g5.v8.dt.core.platform.IWorkspaceOrchestrator;
import com._1c.g5.v8.dt.md.refactoring.core.IMdRefactoringService;
import com._1c.g5.v8.dt.rights.IRightInfosService;
import com._1c.g5.v8.dt.validation.marker.IMarkerManager;
import com._1c.g5.wiring.AbstractServiceAwareModule;
import com.e1c.g5.v8.dt.check.settings.ICheckRepository;

/**
 * External dependencies model for the Check testing support bundle
 *
 * @author Alexander Tretyakevich
 */
public class CheckExternalDependenciesModule
    extends AbstractServiceAwareModule
{
    /**
     * Constructor of {@link CheckExternalDependenciesModule}.
     */
    public CheckExternalDependenciesModule()
    {
        super(FrameworkUtil.getBundle(CheckExternalDependenciesModule.class).getBundleContext());
    }

    @Override
    protected void doConfigure()
    {
        bind(IBmModelManager.class).toService();
        bind(ICheckRepository.class).toService();
        bind(IDerivedDataManagerProvider.class).toService();
        bind(IDtProjectManager.class).toService();
        bind(IMarkerManager.class).toService();
        bind(IMdRefactoringService.class).toService();
        bind(IWorkspaceOrchestrator.class).toService();
        bind(IRightInfosService.class).toService();
    }
}
