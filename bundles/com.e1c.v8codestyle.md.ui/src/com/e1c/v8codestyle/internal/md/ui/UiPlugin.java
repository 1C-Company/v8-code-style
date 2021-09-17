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
package com.e1c.v8codestyle.internal.md.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class UiPlugin
    extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "com.e1c.v8codestyle.md.ui"; //$NON-NLS-1$

    // The shared instance
    private static UiPlugin plugin;

    private Injector injector;

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static UiPlugin getDefault()
    {
        return plugin;
    }

    /**
     * Writes a status to the plugin log.
     *
     * @param status status to log, cannot be <code>null</code>
     */
    public static void log(IStatus status)
    {
        plugin.getLog().log(status);
    }

    /**
     * Writes a throwable to the plugin log as error status.
     *
     * @param throwable throwable, cannot be <code>null</code>
     */
    public static void logError(Throwable throwable)
    {
        log(createErrorStatus(throwable.getMessage(), throwable));
    }

    /**
     * Creates error status by a given message and cause throwable.
     *
     * @param message status message, cannot be <code>null</code>
     * @param throwable throwable, can be <code>null</code> if not applicable
     * @return status created error status, never <code>null</code>
     */
    public static IStatus createErrorStatus(String message, Throwable throwable)
    {
        return new Status(IStatus.ERROR, PLUGIN_ID, 0, message, throwable);
    }

    /**
     * Creates warning status by a given message.
     *
     * @param message status message, cannot be <code>null</code>
     * @return status created warning status, never <code>null</code>
     */
    public static IStatus createWarningStatus(String message)
    {
        return new Status(IStatus.WARNING, PLUGIN_ID, 0, message, null);
    }

    /**
     * Creates warning status by a given message and cause throwable.
     *
     * @param message status message, cannot be <code>null</code>
     * @param throwable throwable, can be <code>null</code> if not applicable
     * @return status created warning status, never <code>null</code>
     */
    public static IStatus createWarningStatus(final String message, Exception throwable)
    {
        return new Status(IStatus.WARNING, PLUGIN_ID, 0, message, throwable);
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        super.start(bundleContext);

        plugin = this;

    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        plugin = null;
        injector = null;

        super.stop(bundleContext);
    }

    /**
     * Returns Guice injector for this plugin.
     *
     * @return Guice injector for this plugin, never <code>null</code>
     */
    /* package */ Injector getInjector()
    {
        Injector localInstance = injector;
        if (localInstance == null)
        {
            synchronized (UiPlugin.class)
            {
                localInstance = injector;
                if (localInstance == null)
                {
                    localInstance = createInjector();
                    injector = localInstance;
                }
            }
        }
        return localInstance;
    }

    private Injector createInjector()
    {
        try
        {
            return Guice.createInjector(new ExternalDependenciesModule(this));
        }
        catch (Exception e)
        {
            log(createErrorStatus("Failed to create injector for " //$NON-NLS-1$
                + getBundle().getSymbolicName(), e));
            throw new RuntimeException("Failed to create injector for " //$NON-NLS-1$
                + getBundle().getSymbolicName(), e);
        }
    }

}
