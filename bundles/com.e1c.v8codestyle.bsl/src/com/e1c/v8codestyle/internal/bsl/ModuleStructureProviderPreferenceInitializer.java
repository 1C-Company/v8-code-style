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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;

import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;

/**
 * Initializer of default values for module structure service.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructureProviderPreferenceInitializer
    extends AbstractPreferenceInitializer
{

    @Override
    public void initializeDefaultPreferences()
    {
        DefaultScope.INSTANCE.getNode(IModuleStructureProvider.PREF_QUALIFIER)
            .putBoolean(IModuleStructureProvider.PREF_KEY_CREATE_STRUCTURE,
                IModuleStructureProvider.PREF_DEFAULT_CREATE_STRUCTURE);

        DefaultScope.INSTANCE.getNode(StrictTypeUtil.PREF_QUALIFIER)
            .putBoolean(StrictTypeUtil.PREF_KEY_CREATE_STRICT_TYPES, StrictTypeUtil.PREF_DEFAULT_CREATE_STRICT_TYPES);

    }

}
