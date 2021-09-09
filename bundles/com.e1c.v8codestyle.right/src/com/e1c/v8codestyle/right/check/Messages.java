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

    public static String RightActiveUsers_description;
    public static String RightActiveUsers_title;
    public static String RightAdministration_description;
    public static String RightAdministration_title;
    public static String RightAllFunctionsMode_description;
    public static String RightAllFunctionsMode_title;
    public static String RightConfigurationExtensionsAdministration_description;
    public static String RightConfigurationExtensionsAdministration_title;
    public static String RightDataAdministration_description;
    public static String RightDataAdministration_title;
    public static String RightExclusiveMode_description;
    public static String RightExclusiveMode_title;
    public static String RightInteracitveDelete_description;
    public static String RightInteracitveDelete_title;
    public static String RightInteracitveDeleteMarkedPredefinedData_description;
    public static String RightInteracitveDeleteMarkedPredefinedData_title;
    public static String RightInteracitveDeletePredefinedData_description;
    public static String RightInteracitveDeletePredefinedData_title;
    public static String RightInteractiveClearDeletionMarkPredefinedData_description;
    public static String RightInteractiveClearDeletionMarkPredefinedData_title;
    public static String RightInteractiveOpenExternalDataProcessors_description;
    public static String RightInteractiveOpenExternalDataProcessors_title;
    public static String RightInteractiveOpenExternalReports_description;
    public static String RightInteractiveOpenExternalReports_title;
    public static String RightInteractiveSetDeletionMarkPredefinedData_description;
    public static String RightInteractiveSetDeletionMarkPredefinedData_title;
    public static String RightOutputToPrinterFileClipboard_description;
    public static String RightOutputToPrinterFileClipboard_title;
    public static String RightSaveUserData_description;
    public static String RightSaveUserData_title;
    public static String RightStartAutomation_description;
    public static String RightStartAutomation_title;
    public static String RightStartExternalConnection_description;
    public static String RightStartExternalConnection_title;
    public static String RightStartThickClient_description;
    public static String RightStartThickClient_title;
    public static String RightStartThinClient_description;
    public static String RightStartThinClient_title;
    public static String RightStartWebClient_description;
    public static String RightStartWebClient_title;
    public static String RightUpdateDatabaseConfiguration_description;
    public static String RightUpdateDatabaseConfiguration_title;
    public static String RightViewEventLog_description;
    public static String RightViewEventLog_title;

    public static String RoleRightHasRls_description;
    public static String RoleRightHasRls_Exclude_Right_Object_name_pattern;
    public static String RoleRightHasRls_Role_name_pattern;
    public static String RoleRightHasRls_Role_Right__0__for__1__has_RLS;
    public static String RoleRightHasRls_title;

    public static String RoleRightSetCheck_Exclude_object_name_pattern;
    public static String RoleRightSetCheck_Role_right__0__set_for__1;

    public static String ExcludeRoleByPatternExtension_Exclude_role_name_pattern;
    public static String ExcludeRoleByNameListExtension_Exclude_role_names;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
