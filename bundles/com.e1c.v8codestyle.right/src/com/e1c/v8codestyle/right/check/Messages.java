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
    public static String RightInteracitveDelete_description;
    public static String RightInteracitveDelete_title;
    public static String RightInteracitveDeleteMarkedPredefinedData_description;
    public static String RightInteracitveDeleteMarkedPredefinedData_title;
    public static String RightInteracitveDeletePredefinedData_description;
    public static String RightInteracitveDeletePredefinedData_title;
    public static String RightInteractiveClearDeletionMarkPredefinedData_description;
    public static String RightInteractiveClearDeletionMarkPredefinedData_title;
    public static String RightInteractiveSetDeletionMarkPredefinedData_description;
    public static String RightInteractiveSetDeletionMarkPredefinedData_title;
    public static String RoleRightHasRls_description;
    public static String RoleRightHasRls_Exclude_Right_Object_name_pattern;
    public static String RoleRightHasRls_Role_name_pattern;
    public static String RoleRightHasRls_Role_Right__0__for__1__has_RLS;
    public static String RoleRightHasRls_title;

    public static String RoleRightSetCheck_Exclude_object_name_pattern;
    public static String RoleRightSetCheck_Role_right__0__set_for__1;

    public static String ExcludeRoleByPatternExtension_Exclude_role_name_pattern;
    public static String ExcludeRoleByNameListExtension_Exclude_role_names;

    public static String ActiveUsersRight_title;
    public static String ActiveUsersRight_description;
    public static String AdministrationRight_title;
    public static String AdministrationRight_description;
    public static String AllFunctionsModeRight_title;
    public static String AllFunctionsModeRight_description;
    public static String ConfigurationExtensionsAdministrationRight_title;
    public static String ConfigurationExtensionsAdministrationRight_description;
    public static String DataAdministrationRight_title;
    public static String DataAdministrationRight_description;
    public static String ExclusiveModeRight_title;
    public static String ExclusiveModeRight_description;
    public static String InteractiveOpenExternalDataProcessorsRight_title;
    public static String InteractiveOpenExternalDataProcessorsRight_description;
    public static String InteractiveOpenExternalReportsRight_title;
    public static String InteractiveOpenExternalReportsRight_description;
    public static String OutputToPrinterFileClipboardRight_title;
    public static String OutputToPrinterFileClipboardRight_description;
    public static String SaveUserDataRight_title;
    public static String SaveUserDataRight_description;
    public static String StartAutomationRight_title;
    public static String StartAutomationRight_description;
    public static String StartExternalConnectionRight_title;
    public static String StartExternalConnectionRight_description;
    public static String StartThickClientRight_title;
    public static String StartThickClientRight_description;
    public static String StartThinClientRight_title;
    public static String StartThinClientRight_description;
    public static String StartWebClientRight_title;
    public static String StartWebClientRight_description;
    public static String UpdateDatabaseConfigurationRight_title;
    public static String UpdateDatabaseConfigurationRight_description;
    public static String ViewEventLogRight_title;
    public static String ViewEventLogRight_description;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
