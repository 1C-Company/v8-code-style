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
 *     Aleksandr Kapralov - issue #20
 *******************************************************************************/
package com.e1c.v8codestyle.right.check;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.right.check.messages"; //$NON-NLS-1$

    public static String RoleFilterExtension_Exclude_Role_name_pattern;

    public static String RoleNameExtension_Role_names_list;

    public static String RoleRightsSetCheck_Exclude_Right_Object_name_pattern;
    public static String RoleRightsSetCheck_Role_right__0__set_for__1;

    public static String AdministrationRight_description;
    public static String AdministrationRight_title;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
