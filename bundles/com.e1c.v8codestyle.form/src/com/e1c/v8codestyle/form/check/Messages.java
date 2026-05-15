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
 *     Manaev Konstantin - issue #855
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
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
    public static String DataCompositionConditionalAppearanceUseCheck_description;
    public static String DataCompositionConditionalAppearanceUseCheck_Form;
    public static String DataCompositionConditionalAppearanceUseCheck_Form_attribute;
    public static String DataCompositionConditionalAppearanceUseCheck_title;
    public static String DataCompositionNameVariantDefault_Description;
    public static String DataCompositionNameVariantDefault_Issue;
    public static String DataCompositionNameVariantDefault_Title;
    public static String DynamicListItemTitleCheck_Description;
    public static String DynamicListItemTitleCheck_message;
    public static String DynamicListItemTitleCheck_title;
    public static String FormCommandsSingleEventHandlerCheck_Description;
    public static String FormCommandsSingleEventHandlerCheck_Handler__0__command__1__assigned_to_command__2;
    public static String FormCommandsSingleEventHandlerCheck_Title;
    public static String FormItemsSingleEventHandlerCheck_description;
    public static String FormItemsSingleEventHandlerCheck_itemName_dot_eventName;
    public static String FormItemsSingleEventHandlerCheck_the_handler_is_already_assigned_to_event;
    public static String FormItemsSingleEventHandlerCheck_title;
    public static String FormItemVisibleSettingsByRoles_description;
    public static String FormItemVisibleSettingsByRoles_Message_template;
    public static String FormItemVisibleSettingsByRoles_Property_name_edit;
    public static String FormItemVisibleSettingsByRoles_Property_name_use;
    public static String FormItemVisibleSettingsByRoles_Property_name_visible;
    public static String FormItemVisibleSettingsByRoles_title;
    public static String FormListFieldRefNotAddedCheck_description;
    public static String FormListFieldRefNotAddedCheck_The_Ref_field_is_not_added_to_dynamic_list;
    public static String FormListFieldRefNotAddedCheck_title;
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
