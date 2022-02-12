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
package com.e1c.v8codestyle.internal.bsl.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.wiring.InjectorAwareServiceRegistrator;
import com._1c.g5.wiring.ServiceInitialization;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Dmitriy Marmyshev
 */
public class UiPlugin
    extends AbstractUIPlugin
    implements ISharedImages
{

    // The plug-in ID
    public static final String PLUGIN_ID = "com.e1c.v8codestyle.bsl.ui"; //$NON-NLS-1$

    // The shared instance
    private static UiPlugin plugin;

    private volatile Injector injector;

    private InjectorAwareServiceRegistrator registrator;

    private static BundleContext context;

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

    static BundleContext getContext()
    {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception
    {

        super.start(bundleContext);

        UiPlugin.context = bundleContext;
        plugin = this;

        BslPackage.eINSTANCE.eClass();
        registrator = new InjectorAwareServiceRegistrator(bundleContext, this::getInjector);
        ServiceInitialization.schedule(() -> {
            // register services from injector
            registrator.managedService(MultiCheckFixRegistrator.class).activateBeforeRegistration().registerInjected();
        });

    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {

        injector = null;
        plugin = null;
        super.stop(bundleContext);

        UiPlugin.context = null;
    }

    /**
     * Returns Guice injector of the plugin
     *
     * @return Guice injector of the plugin, never <code>null</code> if plugin is started
     */
    public Injector getInjector()
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

    @Override
    protected void initializeImageRegistry(ImageRegistry reg)
    {
        super.initializeImageRegistry(reg);
        SharedImages.initializeImageRegistry(reg);
    }

    @Override
    public Image getImage(String symbolicName)
    {
        Image image = getImageRegistry().get(symbolicName);
        if (image != null)
        {
            return image;
        }

        // if there is a descriptor for it, add the image to the registry.
        ImageDescriptor desc = getImageRegistry().getDescriptor(symbolicName);
        if (desc != null)
        {
            getImageRegistry().put(symbolicName, desc);
            return getImageRegistry().get(symbolicName);
        }
        return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor(String symbolicName)
    {
        return getImageRegistry().getDescriptor(symbolicName);
    }

}
