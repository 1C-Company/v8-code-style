package com.e1c.v8codestyle.internal.bsl;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;

import com.e1c.v8codestyle.bsl.IModuleStructureProvider;

public class ModuleStructureProviderPreferenceInitializer
    extends AbstractPreferenceInitializer
{

    @Override
    public void initializeDefaultPreferences()
    {
        DefaultScope.INSTANCE.getNode(IModuleStructureProvider.PREF_QUALIFIER)
            .putBoolean(IModuleStructureProvider.PREF_KEY_CREATE_STRUCTURE,
                IModuleStructureProvider.PREF_DEFAULT_CREATE_STRUCTURE);

    }

}
