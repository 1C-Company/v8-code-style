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
        return "Automatically create module structure";
    }

    @Override
    public String getDescription()
    {
        return "All standard regions will be automatically added in new module. Template for each module type my be customized with license header or other beginning content.";
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
