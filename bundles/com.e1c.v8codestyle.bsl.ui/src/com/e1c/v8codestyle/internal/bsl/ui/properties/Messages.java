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
package com.e1c.v8codestyle.internal.bsl.ui.properties;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
    public static String ModuleStructurePropertyPage_Automatically_create_module_structure;
    public static String ModuleStructurePropertyPage_Automatically_create_strict_types_module;
    public static String ModuleStructurePropertyPage_Open_template;
    public static String ModuleStructurePropertyPage_Open_template_tooltip;
    public static String ModuleStructurePropertyPage_Save_custom_template_to_project_settings;
    public static String ModuleStructurePropertyPage_Save_settings;
    public static String ModuleStructurePropertyPage_Select_module_type_to_create_custom_structure_templates;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
