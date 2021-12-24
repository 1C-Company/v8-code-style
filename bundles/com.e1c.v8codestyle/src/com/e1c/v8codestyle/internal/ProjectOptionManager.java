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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import com.e1c.v8codestyle.IProjectOptionManager;
import com.e1c.v8codestyle.IProjectOptionProvider;
import com.e1c.v8codestyle.ProjectOption;
import com.google.inject.Singleton;

/**
 * Default implementation of {@link IProjectOptionManager} service, that loads {@link IProjectOptionProvider providers}
 * form extension point.
 *
 * @author Dmitriy Marmyshev
 */
@Singleton
public class ProjectOptionManager
    implements IProjectOptionManager
{

    private static final String EXTENSION_POINT_ID = "projectOptions"; //$NON-NLS-1$

    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    private Map<String, IProjectOptionProvider> providers;

    private List<ProjectOption> availableOptions;

    @Override
    public List<ProjectOption> getAvailableOptions()
    {
        if (availableOptions == null)
        {
            synchronized (this)
            {
                if (availableOptions != null)
                {
                    return availableOptions;
                }

                availableOptions = loadAvailableOptions();
            }
        }
        return availableOptions;
    }

    @Override
    public boolean getOption(IProject project, String optionId)
    {
        IProjectOptionProvider provider = getProviders().get(optionId);
        if (provider != null)
        {
            return provider.getOption(project);
        }
        return false;
    }

    @Override
    public boolean getOption(IProject project, ProjectOption option)
    {
        IProjectOptionProvider provider = getProviders().get(option.getOptionId());
        if (provider != null)
        {
            return provider.getOption(project);
        }
        return false;
    }

    @Override
    public void save(IProject project, ProjectOption option, boolean value, IProgressMonitor monitor)
    {
        IProjectOptionProvider provider = getProviders().get(option.getOptionId());
        if (provider != null)
        {
            provider.saveOption(project, value, monitor);
        }
    }

    private Map<String, IProjectOptionProvider> getProviders()
    {
        if (providers == null)
        {
            synchronized (this)
            {
                if (providers != null)
                {
                    return providers;
                }

                providers = loadProviders();
            }
        }
        return providers;
    }

    private Map<String, IProjectOptionProvider> loadProviders()
    {
        Map<String, IProjectOptionProvider> result = new HashMap<>();

        IConfigurationElement[] elements = Platform.getExtensionRegistry()
            .getExtensionPoint(CorePlugin.PLUGIN_ID, EXTENSION_POINT_ID)
            .getConfigurationElements();

        for (int i = 0; i < elements.length; i++)
        {
            IConfigurationElement element = elements[i];
            try
            {
                IProjectOptionProvider provider = (IProjectOptionProvider)element.createExecutableExtension(ATTR_CLASS);
                if (result.containsKey(provider.getId()))
                {
                    IProjectOptionProvider other = result.get(provider.getId());
                    String message = MessageFormat.format(
                        "Contribution project option provider with ID \"{0}\" duplicated:" //$NON-NLS-1$
                            + " class 1; {1} and class 2: {2}", //$NON-NLS-1$
                        provider.getId(), provider.getClass().getName(), other.getClass().getName());
                    IStatus status = CorePlugin.createErrorStatus(message, null);
                    CorePlugin.log(status);
                }
                else
                {
                    result.put(provider.getId(), provider);
                }
            }
            catch (Exception e)
            {
                CorePlugin.logError(e);
            }
        }
        return result;
    }

    private List<ProjectOption> loadAvailableOptions()
    {
        List<ProjectOption> result = new ArrayList<>();

        for (Entry<String, IProjectOptionProvider> entry : getProviders().entrySet())
        {

            IProjectOptionProvider provider = entry.getValue();
            ProjectOption option = new ProjectOption(provider.getId(), provider.getOrder(), provider.getPresentation(),
                provider.getDescription(), provider.getDefault());
            result.add(option);
        }
        Collections.sort(result, (o1, o2) -> o1.getOrder() - o2.getOrder());
        return result;
    }

}
