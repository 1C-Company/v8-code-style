/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.ui.qfix;

import org.eclipse.osgi.util.NLS;

/**
 * NLS messages
 *
 * @author Vadim Geraskin
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

    public static String ConvertFunctionToProcedureFix_description;

    public static String ConvertFunctionToProcedureFix_details;

    public static String CommonModuleNamedSelfReferenceFix_description;

    public static String CommonModuleNamedSelfReferenceFix_details;

    public static String FormSelfReferenceOutdatedFix_description;

    public static String FormSelfReferenceOutdatedFix_details;

    public static String ManagerModuleNamedSelfReferenceFix_description;

    public static String ManagerModuleNamedSelfReferenceFix_details;

    public static String OpenBslDocCommentViewFix_Description;

    public static String OpenBslDocCommentViewFix_Details;

    public static String SelfReferenceFix_description;

    public static String SelfReferenceFix_details;

    public static String ServerExecutionSafeModeFix_description;

    public static String ServerExecutionSafeModeFix_details;

    public static String UndefinedMethodFix_func_title;
    public static String UndefinedMethodFix_func_desc;
    public static String UndefinedMethodFix_proc_title;
    public static String UndefinedMethodFix_proc_desc;
    public static String UndefinedVariableFix_title;
    public static String UndefinedVariableFix_desc;

    public static String SelfAssignFix_Description;
    public static String SelfAssignFix_Details;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // N/A
    }
}
