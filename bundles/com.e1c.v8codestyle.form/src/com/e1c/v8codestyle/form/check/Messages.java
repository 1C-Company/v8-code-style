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
package com.e1c.v8codestyle.form.check;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.form.check.messages"; //$NON-NLS-1$
    public static String FormListRefUseAlwaysFlagDisabledCheck_description;
    public static String FormListRefUseAlwaysFlagDisabledCheck_title;
    public static String FormListRefUseAlwaysFlagDisabledCheck_UseAlways_flag_is_disabled_for_the_Ref_field;
    public static String FormListRefUserVisibilityEnabledCheck_description;
    public static String FormListRefUserVisibilityEnabledCheck_title;
    public static String FormListRefUserVisibilityEnabledCheck_User_visibility_is_not_disabled_for_the_Ref_field;
    public static String InputFieldListChoiceMode_description;
    public static String InputFieldListChoiceMode_Form_input_field_the_list_choice_mode_not_set_with_filled_choice_list;
    public static String InputFieldListChoiceMode_title;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
