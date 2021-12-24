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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.e1c.v8codestyle.IProjectOptionProvider;
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.google.inject.Inject;

/**
 * The provider of project function option that allows to create module structure.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructureProjectOptionProvider
    implements IProjectOptionProvider
{
    private final IModuleStructureProvider moduleStructureProvider;

    @Inject
    public ModuleStructureProjectOptionProvider(IModuleStructureProvider moduleStructureProvider)
    {
        this.moduleStructureProvider = moduleStructureProvider;
    }

    @Override
    public String getId()
    {
        return "module-structure"; //$NON-NLS-1$
    }

    @Override
    public String getPresentation()
    {
        return Messages.ModuleStructureProjectOptionProvider_presentation;
    }

    @Override
    public String getDescription()
    {
        return Messages.ModuleStructureProjectOptionProvider_Description;
    }

    @Override
    public int getOrder()
    {
        return 40;
    }

    @Override
    public boolean getOption(IProject project)
    {
        return moduleStructureProvider.canCreateStructure(project);
    }

    @Override
    public boolean getDefault()
    {
        IScopeContext[] contexts =
            new IScopeContext[] { InstanceScope.INSTANCE, ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(IModuleStructureProvider.PREF_QUALIFIER, IModuleStructureProvider.PREF_KEY_CREATE_STRUCTURE,
                IModuleStructureProvider.PREF_DEFAULT_CREATE_STRUCTURE, contexts);
    }

    @Override
    public void saveOption(IProject project, boolean value, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }
        ProjectScope scope = new ProjectScope(project);
        IEclipsePreferences node = scope.getNode(IModuleStructureProvider.PREF_QUALIFIER);
        node.putBoolean(IModuleStructureProvider.PREF_KEY_CREATE_STRUCTURE, value);
        try
        {
            node.flush();
        }
        catch (BackingStoreException e)
        {
            BslPlugin.logError(e);
        }

    }

}
