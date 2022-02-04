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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckRepository;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.v8codestyle.IProjectOptionProvider;
import com.e1c.v8codestyle.check.CheckUtils;
import com.e1c.v8codestyle.check.StandardCheckRegistry;
import com.google.inject.Inject;

/**
 * The provider of standard check project option.
 *
 * @author Dmitriy Marmyshev
 */
public class StandardChecksProjectOptionProvider
    implements IProjectOptionProvider
{
    private final ICheckRepository checkRepository;

    private final StandardCheckRegistry standardCheckRegistry;

    /**
     * Instantiates a new standard checks project option provider.
     *
     * @param checkRepository the check repository service, cannot be {@code null}.
     * @param standardCheckRegistry the standard check registry service, cannot be {@code null}.
     */
    @Inject
    public StandardChecksProjectOptionProvider(ICheckRepository checkRepository,
        StandardCheckRegistry standardCheckRegistry)
    {
        this.checkRepository = checkRepository;
        this.standardCheckRegistry = standardCheckRegistry;
    }

    @Override
    public String getId()
    {
        return "standard-checks"; //$NON-NLS-1$
    }

    @Override
    public String getPresentation()
    {
        return Messages.StandardChecksProjectOptionProvider_presentation;
    }

    @Override
    public String getDescription()
    {
        return Messages.StandardChecksProjectOptionProvider_description;
    }

    @Override
    public int getOrder()
    {
        return 10;
    }

    @Override
    public boolean isEnabled(IProject project)
    {
        return CheckUtils.isStandardCheckEnable(project);
    }

    @Override
    public boolean getDefault()
    {
        return CheckUtils.isStandardCheckDefaultEnable();
    }

    @Override
    public void saveEnable(IProject project, boolean value, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }
        CheckUtils.setStandardCheckEnable(project, value);

        // pre-load all checks to ensure fill-up registry
        String profile = checkRepository.getActiveSettingsProfile(project);
        checkRepository.getDefaultSettingsForProfile(profile, project);

        Collection<ICheckSettings> changed = new ArrayList<>();
        for (CheckUid checkUid : standardCheckRegistry.getChecks())
        {
            if (monitor.isCanceled())
            {
                return;
            }

            ICheckSettings settings = checkRepository.getSettings(checkUid, project);
            if (settings.isEnabled() != value)
            {
                settings.setEnabled(value);
                changed.add(settings);
            }
        }

        if (!changed.isEmpty())
        {
            checkRepository.applyChanges(changed, project);
        }
    }

}
