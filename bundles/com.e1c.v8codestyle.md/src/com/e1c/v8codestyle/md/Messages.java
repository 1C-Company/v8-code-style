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
package com.e1c.v8codestyle.md;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.md.messages"; //$NON-NLS-1$
    public static String CommonModuleTypes_Server_module;
    public static String CommonModuleTypes_Client_Cached_module;
    public static String CommonModuleTypes_Client_global_module;
    public static String CommonModuleTypes_Client_Localization_module;
    public static String CommonModuleTypes_Client_module;
    public static String CommonModuleTypes_Client_Overridable_module;
    public static String CommonModuleTypes_Client_Server_module;
    public static String CommonModuleTypes_Server_Cached_module;
    public static String CommonModuleTypes_Server_Full_access_module;
    public static String CommonModuleTypes_Server_global_module;
    public static String CommonModuleTypes_Server_Localization_module;
    public static String CommonModuleTypes_Server_module_for_call_form_client;
    public static String CommonModuleTypes_Server_Overridable_module;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
