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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.e1c.v8codestyle.IProjectOptionProvider;
import com.e1c.v8codestyle.autosort.AutoSortPreferences;

/**
 * The provider of auto-sort top metadata project option.
 *
 * @author Dmitriy Marmyshev
 */
public class AutoSortProjectOptionProvider
    implements IProjectOptionProvider
{

    @Override
    public String getId()
    {
        return "auto-sort"; //$NON-NLS-1$
    }

    @Override
    public String getPresentation()
    {
        return Messages.AutoSortProjectOptionProvider_presentation;
    }

    @Override
    public String getDescription()
    {
        return Messages.AutoSortProjectOptionProvider_description;
    }

    @Override
    public int getOrder()
    {
        return 30;
    }

    @Override
    public boolean isEnabled(IProject project)
    {
        return AutoSortPreferences.isSortAllTop(project);
    }

    @Override
    public boolean getDefault()
    {
        IScopeContext[] contexts =
            new IScopeContext[] { InstanceScope.INSTANCE, ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(AutoSortPlugin.PLUGIN_ID, AutoSortPreferences.KEY_ALL_TOP, AutoSortPreferences.DEFAULT_SORT,
                contexts);
    }

    @Override
    public void saveEnable(IProject project, boolean value, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }

        IEclipsePreferences prefs = AutoSortPreferences.getPreferences(project);
        prefs.putBoolean(AutoSortPreferences.KEY_ALL_TOP, value);
        try
        {
            prefs.flush();
        }
        catch (BackingStoreException e)
        {
            AutoSortPlugin.logError(e);
        }
    }

}
