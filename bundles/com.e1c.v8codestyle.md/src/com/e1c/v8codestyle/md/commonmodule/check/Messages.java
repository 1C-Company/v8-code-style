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
package com.e1c.v8codestyle.md.commonmodule.check;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
    public static String CommonModuleNameClient_description;
    public static String CommonModuleNameClient_message;
    public static String CommonModuleNameClient_title;
    public static String CommonModuleNameClientServer_description;
    public static String CommonModuleNameClientServer_message;
    public static String CommonModuleNameClientServer_title;
    public static String CommonModuleNamePrivilegedCheck_Description;
    public static String CommonModuleNamePrivilegedCheck_Issue;
    public static String CommonModuleNamePrivilegedCheck_Title;
    public static String CommonModuleNameGlobal_Description;
    public static String CommonModuleNameGlobal_Message;
    public static String CommonModuleNameGlobal_Title;
    public static String CommonModuleNameGlobalClientCheck_Description;
    public static String CommonModuleNameGlobalClientCheck_Message;
    public static String CommonModuleNameGlobalClientCheck_Title;
    public static String CommonModuleType_description;
    public static String CommonModuleType_message;
    public static String CommonModuleType_title;
    public static String CommonModuleNameServerCallPostfixCheck_0;
    public static String CommonModuleNameServerCallPostfixCheck_Common_module_name_description;
    public static String CommonModuleNameServerCallPostfixCheck_Common_module_postfix_title;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
