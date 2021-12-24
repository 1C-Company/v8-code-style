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
package com.e1c.v8codestyle.check;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.e1c.v8codestyle.internal.CorePlugin;

/**
 * @author Dmitriy Marmyshev
 *
 */
public final class CheckUtils
{

    /** The preference root qualifier. */
    public static final String PREF_QUALIFIER = CorePlugin.PLUGIN_ID;

    /** The key for preferences store the state of enable checks by 1C:Standards. */
    public static final String PREF_KEY_STANDARD_CHECKS = "standardChecks"; //$NON-NLS-1$

    /** The default value of enable all check by 1C:Standards. */
    public static final boolean PREF_DEFAULT_STANDARD_CHECKS = true;

    /** The key for preferences store the state of enable common sense checks not regulated by 1C:Standards. */
    public static final String PREF_KEY_COMMON_CHECKS = "commonChecks"; //$NON-NLS-1$

    /** The default value of enable all common sense check. */
    public static final boolean PREF_DEFAULT_COMMON_CHECKS = true;

    /**
     * Checks if the standard checks enable by default for the installation.
     *
     * @return true, if is standard check default enable
     */
    public static boolean isStandardCheckDefaultEnable()
    {
        IScopeContext[] contexts =
            new IScopeContext[] { InstanceScope.INSTANCE, ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(PREF_QUALIFIER, PREF_KEY_STANDARD_CHECKS, PREF_DEFAULT_STANDARD_CHECKS, contexts);
    }

    /**
     * Checks if the standard checks enabled for the project.
     *
     * @param project the project, cannot be {@code null}.
     * @return true, if the standard check enable
     */
    public static boolean isStandardCheckEnable(IProject project)
    {
        ProjectScope scope = new ProjectScope(project);
        IScopeContext[] contexts =
            new IScopeContext[] { scope, InstanceScope.INSTANCE, ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(PREF_QUALIFIER, PREF_KEY_STANDARD_CHECKS, PREF_DEFAULT_STANDARD_CHECKS, contexts);
    }

    /**
     * Sets the standard check enable.
     *
     * @param project the project, cannot be {@code null}.
     * @param value the value
     */
    public static void setStandardCheckEnable(IProject project, boolean value)
    {
        ProjectScope projectScope = new ProjectScope(project);
        IEclipsePreferences prefs = projectScope.getNode(PREF_QUALIFIER);

        prefs.putBoolean(PREF_KEY_STANDARD_CHECKS, value);
        try
        {
            prefs.flush();
        }
        catch (BackingStoreException e)
        {
            CorePlugin.logError(e);
        }
    }

    /**
     * Checks if is common check enable.
     *
     * @param project the project, cannot be {@code null}.
     * @return true, if is common check enable
     */
    public static boolean isCommonCheckEnable(IProject project)
    {
        ProjectScope scope = new ProjectScope(project);
        IScopeContext[] contexts =
            new IScopeContext[] { scope, InstanceScope.INSTANCE, ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(PREF_QUALIFIER, PREF_KEY_COMMON_CHECKS, PREF_DEFAULT_COMMON_CHECKS, contexts);
    }

    /**
     * Checks if is common check default enable.
     *
     * @return true, if is common check default enable
     */
    public static boolean isCommonCheckDefaultEnable()
    {
        IScopeContext[] contexts =
            new IScopeContext[] { InstanceScope.INSTANCE, ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(PREF_QUALIFIER, PREF_KEY_COMMON_CHECKS, PREF_DEFAULT_COMMON_CHECKS, contexts);
    }

    /**
     * Sets the common check enable.
     *
     * @param project the project, cannot be {@code null}.
     * @param value the value
     */
    public static void setCommonCheckEnable(IProject project, boolean value)
    {
        ProjectScope projectScope = new ProjectScope(project);
        IEclipsePreferences prefs = projectScope.getNode(PREF_QUALIFIER);

        prefs.putBoolean(PREF_KEY_COMMON_CHECKS, value);
        try
        {
            prefs.flush();
        }
        catch (BackingStoreException e)
        {
            CorePlugin.logError(e);
        }

    }

    private CheckUtils()
    {
        throw new IllegalAccessError("Utilty class"); //$NON-NLS-1$
    }

}
