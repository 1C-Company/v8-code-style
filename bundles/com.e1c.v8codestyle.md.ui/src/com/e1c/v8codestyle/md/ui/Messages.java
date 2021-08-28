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
/**
 *
 */
package com.e1c.v8codestyle.md.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.md.ui.messages"; //$NON-NLS-1$
    public static String CommonModuleTypeDtNewWizardPage_Choose_valid_common_module_type_from_list;
    public static String CommonModuleTypeDtNewWizardPage_Select_common_module_type;
    public static String CommonModuleTypeDtNewWizardPage_Select_type;
    public static String CommonModuleTypeDtNewWizardPage_Select_type_from_list;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
