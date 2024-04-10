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
package com.e1c.v8codestyle.bsl;

import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;

/**
 * Standard module structure dually-named section;
 * <br>Sorted by priority of module regions standard.
 *
 * @author Dmitriy Marmyshev
 */
public enum ModuleStructureSection
{
    VARIABLES("Variables", "ОписаниеПеременных"), //$NON-NLS-1$ //$NON-NLS-2$
    PUBLIC("Public", "ПрограммныйИнтерфейс"), //$NON-NLS-1$ //$NON-NLS-2$
    EVENT_HANDLERS("EventHandlers", "ОбработчикиСобытий"), //$NON-NLS-1$ //$NON-NLS-2$
    INTERNAL("Internal", "СлужебныйПрограммныйИнтерфейс"), //$NON-NLS-1$ //$NON-NLS-2$
    FORM_EVENT_HANDLERS("FormEventHandlers", "ОбработчикиСобытийФормы"), //$NON-NLS-1$ //$NON-NLS-2$
    FORM_HEADER_ITEMS_EVENT_HANDLERS("FormHeaderItemsEventHandlers", "ОбработчикиСобытийЭлементовШапкиФормы"), //$NON-NLS-1$ //$NON-NLS-2$
    FORM_TABLE_ITEMS_EVENT_HANDLERS("FormTableItemsEventHandlers", "ОбработчикиСобытийЭлементовТаблицыФормы", true), //$NON-NLS-1$ //$NON-NLS-2$
    FORM_COMMAND_EVENT_HANDLERS("FormCommandsEventHandlers", "ОбработчикиКомандФормы"), //$NON-NLS-1$ //$NON-NLS-2$
    PRIVATE("Private", "СлужебныеПроцедурыИФункции"), //$NON-NLS-1$ //$NON-NLS-2$
    INITIALIZE("Initialize", "Инициализация"), //$NON-NLS-1$ //$NON-NLS-2$
    DEPRECATED_REGION("Deprecated", "УстаревшиеПроцедурыИФункции"); //$NON-NLS-1$ //$NON-NLS-2$

    private final String[] names;
    private final boolean isSuffixed;

    ModuleStructureSection(String name, String nameRu, boolean isSuffixed)
    {
        this.names = new String[] { name, nameRu };
        this.isSuffixed = isSuffixed;
    }

    ModuleStructureSection(String name, String nameRu)
    {
        this(name, nameRu, false);
    }

    /**
     * Gets the dually-named name where first is English script variant and second is Russian script variant.
     *
     * @return the names, cannot return {@code null}.
     */
    public String[] getNames()
    {
        return names;
    }

    /**
     * Gets the name for the specific script variant of the project.
     *
     * @param scriptVariant the script variant, cannot be {@code null}.
     * @return the name, cannot return {@code null}.
     */
    public String getName(ScriptVariant scriptVariant)
    {
        return names[scriptVariant.getValue()];
    }

    /**
     * Is module region name used only with suffix
     *
     * @return <code>true</code> if region name must be suffixed, <code>false</code> otherwise
     */
    public boolean isSuffixed()
    {
        return isSuffixed;
    }
}
