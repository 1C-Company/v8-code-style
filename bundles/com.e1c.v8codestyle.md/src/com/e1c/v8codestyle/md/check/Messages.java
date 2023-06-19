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
package com.e1c.v8codestyle.md.check;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.md.check.messages"; //$NON-NLS-1$
    public static String CommonModuleNameClient_description;
    public static String CommonModuleNameClient_message;
    public static String DbObjectRefNonRefTypesCheck_Description;
    public static String DbObjectRefNonRefTypesCheck_Ref_and_other;
    public static String DbObjectRefNonRefTypesCheck_Title;
    public static String MdObjectAttributeCommentCheck_Attribute_list;
    public static String MdObjectAttributeCommentCheck_Check_catalogs_param;
    public static String MdObjectAttributeCommentCheck_Check_documents_param;
    public static String MdObjectAttributeCommentCheck_Is_compound_type;
    public static String MdObjectAttributeCommentCheck_Multiline_edit_is_not_enabled;
    public static String MdObjectAttributeCommentCheck_Not_a_String;
    public static String MdObjectAttributeCommentCheck_String_is_not_unlimited;
    public static String MdObjectAttributeCommentCheck_description;
    public static String MdObjectAttributeCommentCheck_message;
    public static String MdObjectAttributeCommentCheck_title;
    public static String MdObjectAttributeCommentNotExist_description;
    public static String MdObjectAttributeCommentNotExist_Md_Object_attribute_Comment_does_not_exist;
    public static String MdObjectAttributeCommentNotExist_Param_Check_Catalogs;
    public static String MdObjectAttributeCommentNotExist_Param_Check_Documents;
    public static String MdObjectAttributeCommentNotExist_Param_Attribute_name_list;
    public static String MdObjectAttributeCommentNotExist_title;
    public static String MdObjectNameWithoutSuffix_Name_suffix_list_title;
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
    public static String ConfigurationDataLock_description;
    public static String ConfigurationDataLock_message;
    public static String ConfigurationDataLock_title;
    public static String CommonModuleNameServerCallPostfixCheck_0;
    public static String CommonModuleNameServerCallPostfixCheck_Common_module_name_description;
    public static String CommonModuleNameServerCallPostfixCheck_Common_module_postfix_title;
    public static String DbObjectAnyRefCheck_AnyRef;
    public static String DbObjectAnyRefCheck_Description;
    public static String DbObjectAnyRefCheck_Title;
    public static String DocumentPostInPrivilegedModeCheck_description;
    public static String DocumentPostInPrivilegedModeCheck_message_Post_in_privileged_mode;
    public static String DocumentPostInPrivilegedModeCheck_message_Unpost_in_privileged_mode;
    public static String DocumentPostInPrivilegedModeCheck_title;
    public static String ExtensionMdObjectNamePrefixCheck_Description;
    public static String ExtensionMdObjectNamePrefixCheck_Object_0_should_have_1_prefix;
    public static String ExtensionMdObjectNamePrefixCheck_Title;
    public static String FunctionalOptionPrivilegedGetModeCheck_description;
    public static String FunctionalOptionPrivilegedGetModeCheck_message;
    public static String FunctionalOptionPrivilegedGetModeCheck_title;
    public static String MdObjectNameLength_description;
    public static String MdObjectNameLength_Maximum_name_length_description;
    public static String MdObjectNameLength_message;
    public static String MdObjectNameLength_title;
    public static String MdObjectNameUnallowedLetterCheck_description;
    public static String MdObjectNameUnallowedLetterCheck_Ru_locale_unallowed_letter_used_for_name_synonym_or_comment;
    public static String MdObjectNameUnallowedLetterCheck_title;
    public static String MdListObjectPresentationCheck_decription;
    public static String MdListObjectPresentationCheck_Neither_Object_presentation_nor_List_presentation_is_not_filled;
    public static String MdListObjectPresentationCheck_title;
    public static String MdOwnerAttributeSynonymEmpty_Title;
    public static String MdOwnerAttributeSynonymEmpty_Description;
    public static String MdOwnerAttributeSynonymEmpty_owner_ErrorMessage;
    public static String MdOwnerAttributeSynonymEmpty_parent_ErrorMessage;
    public static String MdScheduledJobDescriptionCheck_title;
    public static String MdScheduledJobDescriptionCheck_description;
    public static String MdScheduledJobDescriptionCheck_message;
    public static String MdScheduledJobPeriodicityCheck_description;
    public static String MdScheduledJobPeriodicityCheck_The_minimum_job_interval_is_less_then_minute;
    public static String MdScheduledJobPeriodicityCheck_title;
    public static String MdScheduledJobPeriodicityCheck_Minimum_job_interval_description;
    public static String UnsafePasswordStorageCheck_Avoid_storing_password_in_infobase;
    public static String UnsafePasswordStorageCheck_Avoid_storing_password_in_infobase_description;
    public static String UnsafePasswordStorageCheck_Avoid_storing_password_in_infobase_error;
    public static String RegisterResourcePrecisionCheck_description;
    public static String RegisterResourcePrecisionCheck_message;
    public static String RegisterResourcePrecisionCheck_title;
    public static String SubsystemSynonymTooLongCheck_description;
    public static String SubsystemSynonymTooLongCheck_Exclude_languages_comma_separated;
    public static String SubsystemSynonymTooLongCheck_Length_of_section_name_more_than_symbols_for_language;
    public static String SubsystemSynonymTooLongCheck_Maximum_section_name_length;
    public static String SubsystemSynonymTooLongCheck_title;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
