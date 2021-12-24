package com.e1c.v8codestyle.internal.autosort;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.e1c.v8codestyle.IProjectOptionProvider;
import com.e1c.v8codestyle.autosort.AutoSortPreferences;

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
        return "Enable auto-sort configuration top metadata object";
    }

    @Override
    public String getDescription()
    {
        return "Allows automaticatlly to sort top metadata objects of configuration";
    }

    @Override
    public int getOrder()
    {
        return 30;
    }

    @Override
    public boolean getOption(IProject project)
    {
        return AutoSortPreferences.isSortAllTop(project);
    }

    @Override
    public boolean getDefault()
    {
        return true;
    }

    @Override
    public void saveOption(IProject project, boolean value, IProgressMonitor monitor)
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
