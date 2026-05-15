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

import java.util.Collection;
import java.util.Set;

import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com.google.common.collect.ImmutableSet;

/**
 * Default module structure list of sections by the module type.
 *
 * @author Dmitriy Marmyshev
 */
public enum ModuleStructure
{

    BOT_MODULE(ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.PRIVATE),
    WEB_SOCKET_CLIENT_MODULE(ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.PRIVATE),
    COMMAND_MODULE(ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.PRIVATE),
    COMMON_MODULE(ModuleStructureSection.PUBLIC, ModuleStructureSection.INTERNAL, ModuleStructureSection.PRIVATE),
    EXTERNAL_CONN_MODULE(ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.PRIVATE),
    FORM_MODULE(ModuleStructureSection.VARIABLES, ModuleStructureSection.FORM_EVENT_HANDLERS,
        ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS, ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS,
        ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS, ModuleStructureSection.PRIVATE,
        ModuleStructureSection.INITIALIZE),
    HTTP_SERVICE_MODULE(ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.PRIVATE),
    INTEGRATION_SERVICE_MODULE(ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.PRIVATE),
    MANAGED_APP_MODULE(ModuleStructureSection.VARIABLES, ModuleStructureSection.EVENT_HANDLERS,
        ModuleStructureSection.PRIVATE, ModuleStructureSection.INITIALIZE),
    MANAGER_MODULE(ModuleStructureSection.PUBLIC, ModuleStructureSection.EVENT_HANDLERS,
        ModuleStructureSection.INTERNAL, ModuleStructureSection.PRIVATE),
    OBJECT_MODULE(ModuleStructureSection.VARIABLES, ModuleStructureSection.PUBLIC,
        ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.INTERNAL, ModuleStructureSection.PRIVATE,
        ModuleStructureSection.INITIALIZE),
    ORDINARY_APP_MODULE(ModuleStructureSection.VARIABLES, ModuleStructureSection.EVENT_HANDLERS,
        ModuleStructureSection.PRIVATE, ModuleStructureSection.INITIALIZE),
    RECORDSET_MODULE(ModuleStructureSection.VARIABLES, ModuleStructureSection.PUBLIC,
        ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.INTERNAL, ModuleStructureSection.PRIVATE,
        ModuleStructureSection.INITIALIZE),
    SESSION_MODULE(ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.PRIVATE),
    VALUE_MANAGER_MODULE(ModuleStructureSection.VARIABLES, ModuleStructureSection.PUBLIC,
        ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.INTERNAL, ModuleStructureSection.PRIVATE,
        ModuleStructureSection.INITIALIZE),
    WEB_SERVICE_MODULE(ModuleStructureSection.EVENT_HANDLERS, ModuleStructureSection.PRIVATE);

    /**
     * Gets the module structure regions with order how they should be placed in module.
     *
     * @param moduleType the module type, cannot be {@code null}.
     * @return the module structure regions, cannot return {@code null}.
     */
    public static Collection<ModuleStructureSection> getStructureByType(ModuleType moduleType)
    {
        switch (moduleType)
        {
        case BOT_MODULE:
            return BOT_MODULE.getSections();
        case COMMAND_MODULE:
            return COMMAND_MODULE.getSections();
        case COMMON_MODULE:
            return COMMON_MODULE.getSections();
        case EXTERNAL_CONN_MODULE:
            return EXTERNAL_CONN_MODULE.getSections();
        case FORM_MODULE:
            return FORM_MODULE.getSections();
        case HTTP_SERVICE_MODULE:
            return HTTP_SERVICE_MODULE.getSections();
        case INTEGRATION_SERVICE_MODULE:
            return INTEGRATION_SERVICE_MODULE.getSections();
        case MANAGED_APP_MODULE:
            return MANAGED_APP_MODULE.getSections();
        case MANAGER_MODULE:
            return MANAGER_MODULE.getSections();
        case OBJECT_MODULE:
            return OBJECT_MODULE.getSections();
        case ORDINARY_APP_MODULE:
            return ORDINARY_APP_MODULE.getSections();
        case RECORDSET_MODULE:
            return RECORDSET_MODULE.getSections();
        case SESSION_MODULE:
            return SESSION_MODULE.getSections();
        case VALUE_MANAGER_MODULE:
            return VALUE_MANAGER_MODULE.getSections();
        case WEB_SERVICE_MODULE:
            return WEB_SERVICE_MODULE.getSections();
        case WEB_SOCKET_CLIENT_MODULE:
            return WEB_SOCKET_CLIENT_MODULE.getSections();

        default:
            break;
        }
        return Set.of();
    }

    private final ImmutableSet<ModuleStructureSection> sections;

    ModuleStructure(ModuleStructureSection... sections)
    {
        this.sections = ImmutableSet.copyOf(sections);
    }

    /**
     * Gets the module structure regions with order how they should be placed in module.
     *
     * @return the sections, cannot return {@code null}.
     */
    public Collection<ModuleStructureSection> getSections()
    {
        return sections;
    }

}
