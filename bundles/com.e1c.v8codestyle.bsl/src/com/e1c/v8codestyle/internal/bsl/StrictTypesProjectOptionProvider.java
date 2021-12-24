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
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;

/**
 * The provider of project functional option allows to add strict-types annotation to module header.
 *
 * @author Dmitriy Marmyshev
 */
public class StrictTypesProjectOptionProvider
    implements IProjectOptionProvider
{

    @Override
    public String getId()
    {
        return "module-strict-types"; //$NON-NLS-1$
    }

    @Override
    public String getPresentation()
    {
        return Messages.StrictTypesProjectOptionProvider_presentation;
    }

    @Override
    public String getDescription()
    {
        return Messages.StrictTypesProjectOptionProvider_description;
    }

    @Override
    public int getOrder()
    {
        return 50;
    }

    @Override
    public boolean getOption(IProject project)
    {
        return StrictTypeUtil.canAddModuleStrictTypesAnnotation(project);
    }

    @Override
    public boolean getDefault()
    {
        IScopeContext[] contexts =
            new IScopeContext[] { InstanceScope.INSTANCE, ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(StrictTypeUtil.PREF_QUALIFIER, StrictTypeUtil.PREF_KEY_CREATE_STRICT_TYPES,
                StrictTypeUtil.PREF_DEFAULT_CREATE_STRICT_TYPES, contexts);
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
        node.putBoolean(StrictTypeUtil.PREF_KEY_CREATE_STRICT_TYPES, value);
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
