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
package com.e1c.v8codestyle.internal.bsl.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;

/**
 * The preference page of module structure settings.
 * Allows to disable auto-creating module structure.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructurePreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

    /**
     * Instantiates a new module structure preference page.
     */
    public ModuleStructurePreferencePage()
    {
        super(GRID);
        IPreferenceStore preferenceStore =
            new ScopedPreferenceStore(InstanceScope.INSTANCE, IModuleStructureProvider.PREF_QUALIFIER);
        setPreferenceStore(preferenceStore);
    }

    @Override
    public void createFieldEditors()
    {
        addField(new BooleanFieldEditor(StrictTypeUtil.PREF_KEY_CREATE_STRICT_TYPES,
            Messages.ModuleStructurePreferencePage_Automatically_create_strict_types_module, getFieldEditorParent()));

        addField(new BooleanFieldEditor(IModuleStructureProvider.PREF_KEY_CREATE_STRUCTURE,
            Messages.ModuleStructurePreferencePage_Automatically_create_module_structure, getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench)
    {
        // Do nothing
    }

}
