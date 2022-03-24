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
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.bsl.ui.qfix.messages"; //$NON-NLS-1$

    public static String OpenBslDocCommentViewFix_Description;

    public static String OpenBslDocCommentViewFix_Details;

    public static String UndefinedMethodFix_func_title;
    public static String UndefinedMethodFix_func_desc;
    public static String UndefinedMethodFix_proc_title;
    public static String UndefinedMethodFix_proc_desc;
    public static String UndefinedVariableFix_title;
    public static String UndefinedVariableFix_desc;

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
