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
 *     Aleksandr Kapralov - issue #17, #449, #458
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import org.eclipse.osgi.util.NLS;

/**
 * NLS messages
 *
 * @author Dmitriy Marmyshev
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

    public static String AccessibilityAtClientInObjectModuleCheck_Declared_variable_accessible_AtClient;

    public static String AccessibilityAtClientInObjectModuleCheck_description;

    public static String AccessibilityAtClientInObjectModuleCheck_Event_handler_should_be_accessible_AtClient;

    public static String AccessibilityAtClientInObjectModuleCheck_Manager_event_handlers_allows_to_be_AtClient;

    public static String AccessibilityAtClientInObjectModuleCheck_Method_accessible_AtClient;

    public static String AccessibilityAtClientInObjectModuleCheck_Methods_should_be_AtClient;

    public static String AccessibilityAtClientInObjectModuleCheck_title;

    public static String CachedPublicCheck_Description;

    public static String CachedPublicCheck_Issue;

    public static String CachedPublicCheck_Title;

    public static String AttachableEventHandlerNameCheck_Description;

    public static String AttachableEventHandlerNameCheck_Event_handler_name_pattern;

    public static String AttachableEventHandlerNameCheck_Message;

    public static String AttachableEventHandlerNameCheck_Title;

    public static String CanonicalPragmaCheck_description;
    public static String CanonicalPragmaCheck_Pragma_0_is_not_written_canonically_correct_spelling_is_1;
    public static String CanonicalPragmaCheck_title;

    public static String ChangeAndValidateInsteadOfAroundCheck_description;
    public static String ChangeAndValidateInsteadOfAroundCheck_Use_ChangeAndValidate_instead_of_Around;
    public static String ChangeAndValidateInsteadOfAroundCheck_title;

    public static String CodeAfterAsyncCallCheck_Description;

    public static String CodeAfterAsyncCallCheck_Issue;

    public static String CodeAfterAsyncCallCheck_Parameter;

    public static String CodeAfterAsyncCallCheck_Title;

    public static String CommitTransactionCheck_Commit_transaction_must_be_in_try_catch;

    public static String CommitTransactionCheck_No_begin_transaction_for_commit_transaction;

    public static String RollbackTransactionCheck_No_begin_transaction_for_rollback_transaction;

    public static String RollbackTransactionCheck_No_commit_transaction_for_begin_transaction;

    public static String CommitTransactionCheck_No_rollback_transaction_for_begin_transaction;

    public static String CommitTransactionCheck_Should_be_no_executable_code_between_commit_and_exception;

    public static String RollbackTransactionCheck_Should_be_no_executable_code_between_exception_and_rollback;

    public static String BeginTransactionCheck_Executable_code_between_begin_transaction_and_try;

    public static String CommitTransactionCheck_Transaction_contains_empty_except;

    public static String CommitTransactionCheck_Transactions_is_broken;

    public static String CommitTransactionCheck_Transactions_is_broken_des;

    public static String BeginTransactionCheck_Begin_transaction_is_incorrect;

    public static String BeginTransactionCheck_Try_must_be_after_begin;

    public static String BeginTransactionCheck_Try_was_not_found_after_calling_begin;

    public static String CommonModuleMissingApiCheck_Description;

    public static String CommonModuleMissingApiCheck_Issue;

    public static String CommonModuleMissingApiCheck_Title;

    public static String CommonModuleNamedSelfReferenceCheck_description;

    public static String CommonModuleNamedSelfReferenceCheck_issue;

    public static String CommonModuleNamedSelfReferenceCheck_title;

    public static String ConsecutiveEmptyLines_Description;

    public static String ConsecutiveEmptyLines_Parameter_title;

    public static String ConsecutiveEmptyLines_Sequence_of_empty_lines_between__0__and__1__is_greator_than__2;

    public static String ConsecutiveEmptyLines_Title;

    public static String DeprecatedProcedureOutsideDeprecatedRegionCheck_Deprecated_function_out_of_deprecated_area;

    public static String DeprecatedProcedureOutsideDeprecatedRegionCheck_description;

    public static String DeprecatedProcedureOutsideDeprecatedRegionCheck_title;

    public static String EmptyExceptStatementCheck_description;
    public static String EmptyExceptStatementCheck_title;

    public static String EventDataExchangeLoadCheck_Check_at_the_beginning_of_event_handler;
    public static String EventDataExchangeLoadCheck_description;
    public static String EventDataExchangeLoadCheck_Function_list_that_checks_DataExchange_Load;
    public static String EventDataExchangeLoadCheck_Mandatory_checking_of_DataExchangeLoad_is_absent_in_event_handler_0;
    public static String EventDataExchangeLoadCheck_No_return_in__DataExchange_Load__checking;
    public static String EventDataExchangeLoadCheck_title;

    public static String EventHandlerBooleanParamCheck_Check_only_in_event_handlers;

    public static String EventHandlerBooleanParamCheck_description;

    public static String EventHandlerBooleanParamCheck_Parameter_0_should_set_to_False;

    public static String EventHandlerBooleanParamCheck_Parameter_0_should_set_to_True;

    public static String EventHandlerBooleanParamCheck_Prams_to_set_to_False;

    public static String EventHandlerBooleanParamCheck_Prams_to_set_to_True;

    public static String EventHandlerBooleanParamCheck_title;

    public static String ReadingAttributesFromDataBaseCheck_Message;

    public static String ReadingAttributesFromDataBaseCheck_Description;

    public static String ReadingAttributesFromDataBaseCheck_Issue__0;

    public static String ReadingAttributesFromDataBaseCheck_Title;

    public static String RedundantExportCheck_Escess_title;

    public static String RedundantExportCheck_Excess_description;

    public static String RedundantExportCheck_Exclude_title;

    public static String RedundantExportCheck_Unused_export_method__0;

    public static String ExportMethodInCommandFormModuleCheck_CheckClientMethodForm;

    public static String ExportMethodInCommandFormModuleCheck_CheckServerMethodForm;

    public static String ExportMethodInCommandFormModuleCheck_ExludeMethodNamePattern;

    public static String ExportMethodInCommandFormModuleCheck_Notify_description_methods;

    public static String ExportMethodInCommandModule_Do_not_emded_export_method_in_modules_of_command_des;

    public static String ExportMethodInCommandModule_Do_not_emded_export_method_in_modules_of_command_result;

    public static String ExportMethodInCommandModule_Do_not_use_export_method_in_commands_module;

    public static String ExportProcedureMissingCommentCheck_description;

    public static String ExportProcedureMissingCommentCheck_Export_procedure_missing_comment;

    public static String ExportProcedureMissingCommentCheck_title;

    public static String ExportVariableInObjectModuleCheck_Description;

    public static String ExportVariableInObjectModuleCheck_Issue;

    public static String ExportVariableInObjectModuleCheck_Title;

    public static String ExtensionVariablePrefixCheck_Description;

    public static String ExtensionVariablePrefixCheck_Title;

    public static String ExtensionVariablePrefixCheck_Variable_0_should_have_1_prefix;

    public static String ExtensionMethodPrefixCheck_Description;

    public static String ExtensionMethodPrefixCheck_Ext_method__0__should_have__1__prefix;

    public static String ExtensionMethodPrefixCheck_Title;

    public static String ManagerModuleNamedSelfReferenceCheck_description;

    public static String ManagerModuleNamedSelfReferenceCheck_issue;

    public static String ManagerModuleNamedSelfReferenceCheck_title;

    public static String ModuleStructureTopRegionCheck_Check_duplicates_of_standard_regions;

    public static String ModuleStructureTopRegionCheck_Check_order_of_standard_regions;

    public static String ModuleStructureTopRegionCheck_description;

    public static String ModuleStructureTopRegionCheck_error_message;

    public static String ModuleStructureTopRegionCheck_Exclude_region_name;

    public static String ModuleStructureTopRegionCheck_Region_has_duplicate;

    public static String ModuleStructureTopRegionCheck_Region_has_the_wrong_order;

    public static String ModuleStructureTopRegionCheck_Region_is_not_standard_for_current_type_of_module;

    public static String ModuleStructureTopRegionCheck_title;

    public static String ModuleUnusedMethodCheck_Title;
    public static String ModuleUnusedMethodCheck_Description;
    public static String ModuleUnusedMethodCheck_Exclude_method_name_pattern_title;
    public static String ModuleUnusedMethodCheck_Unused_method__0;

    public static String ModuleEmptyMethodCheck_Title;
    public static String ModuleEmptyMethodCheck_Description;
    public static String ModuleEmptyMethodCheck_Exclude_method_name_pattern_title;
    public static String ModuleEmptyMethodCheck_Allow_method_comments_title;
    public static String ModuleEmptyMethodCheck_Empty_method__0;

    public static String ModuleUnusedLocalVariableCheck_Title;
    public static String ModuleUnusedLocalVariableCheck_Description;
    public static String ModuleUnusedLocalVariableCheck_Unused_local_variable__0;
    public static String ModuleUnusedLocalVariableCheck_Probably_variable_not_initilized_yet__0;

    public static String ModuleStructureEventFormRegionsCheck_Description;

    public static String ModuleStructureEventFormRegionsCheck_Event_method__0__can_not_be_placed_in_the_region__1;

    public static String ModuleStructureEventFormRegionsCheck_Event_method__0__should_be_placed_in_the_region__1;

    public static String ModuleStructureEventFormRegionsCheck_Excluded_method_names;

    public static String ModuleStructureEventFormRegionsCheck_Multilevel_nesting_of_regions;

    public static String ModuleStructureEventFormRegionsCheck_Title;

    public static String ModuleStructureEventRegionsCheck_Description;

    public static String ModuleStructureEventRegionsCheck_Event_handler__0__not_region__1;

    public static String ModuleStructureEventRegionsCheck_Only_event_methods__0;

    public static String ModuleStructureEventRegionsCheck_Title;

    public static String ModuleStructureInitCodeInRegion_Description;

    public static String ModuleStructureInitCodeInRegion_Issue__0;

    public static String ModuleStructureInitCodeInRegion_Title;

    public static String ModuleStructureMethodInRegionCheck_Description;

    public static String ModuleStructureMethodInRegionCheck_Method_should_be_placed_in_one_of_the_standard_regions;

    public static String ModuleStructureMethodInRegionCheck_Multilevel_nesting_of_regions;

    public static String ModuleStructureMethodInRegionCheck_Only_export;

    public static String ModuleStructureMethodInRegionCheck_Title;

    public static String ModuleStructureVariablesInRegionCheck_Description;

    public static String ModuleStructureVariablesInRegionCheck_Issue__0;

    public static String ModuleStructureVariablesInRegionCheck_Title;

    public static String QueryInLoop_check_query_in_infinite_loop;
    public static String QueryInLoop_description;
    public static String QueryInLoop_Loop_has_method_with_query__0;
    public static String QueryInLoop_Loop_has_query;
    public static String QueryInLoop_title;

    public static String SelfAssignCheck_Title;
    public static String SelfAssignCheck_Description;
    public static String SelfAssignCheck_Self_assign_issue;

    public static String SelfReferenceCheck_check_object_module;

    public static String SelfReferenceCheck_check_only_existing_form_properties;

    public static String SelfReferenceCheck_Description;

    public static String SelfReferenceCheck_Title;

    public static String SelfReferenceCheck_Issue;

    public static String ServerExecutionSafeModeCheck_description;

    public static String ServerExecutionSafeModeCheck_eval_issue;

    public static String ServerExecutionSafeModeCheck_execute_issue;

    public static String ServerExecutionSafeModeCheck_title;

    public static String StructureCtorTooManyKeysCheck_description;
    public static String StructureCtorTooManyKeysCheck_Maximum_structure_constructor_keys;
    public static String StructureCtorTooManyKeysCheck_Structure_constructor_has_more_than__0__keys;
    public static String StructureCtorTooManyKeysCheck_title;
	
	public static String NotSupportGotoOperatorWebCheck_Title;
    public static String NotSupportGotoOperatorWebCheck_Description;
    public static String NotSupportGotoOperatorWebCheck_Issue;

    public static String NewColorCheck_Use_style_elements;

    public static String NewColorCheck_Use_style_elements_not_specific_values;

    public static String NewColorCheck_Using_new_color;

    public static String NewFontCheck_Description;

    public static String NewFontCheck_Issue;

    public static String NewFontCheck_Title;

    public static String NstrStringLiteralFormatCheck_Check_empty_interface_for_each_language;

    public static String NstrStringLiteralFormatCheck_description;

    public static String NstrStringLiteralFormatCheck_NStr_contains_unknown_language_code__S;

    public static String NstrStringLiteralFormatCheck_NStr_format_is_incorrect__E;

    public static String NstrStringLiteralFormatCheck_NStr_message_for_code__S__ends_with_space;

    public static String NstrStringLiteralFormatCheck_NStr_message_for_language_code__S__is_empty;

    public static String NstrStringLiteralFormatCheck_NStr_message_is_empty;

    public static String NstrStringLiteralFormatCheck_NStr_method_should_accept_string_as_first_param;

    public static String NstrStringLiteralFormatCheck_title;

    public static String RegionEmptyCheck_description;

    public static String RegionEmptyCheck_Region_is_empty;

    public static String RegionEmptyCheck_title;

    public static String RollbackTransactionCheck_Rollback_transaction_must_be_in_try_catch;

    public static String RollbackTransactionCheck_Transactions_is_broken;

    public static String RollbackTransactionCheck_Transactions_is_broken_des;

    public static String UseGotoOperatorCheck_description;

    public static String UseGotoOperatorCheck_title;

    public static String UseGotoOperatorCheck_Use_Goto_operator;

    public static String UseGotoOperatorCheck_Use_Label_with_Goto_operator;

    public static String UnknownFormParameterAccessCheck_description;

    public static String UnknownFormParameterAccessCheck_title;

    public static String UnknownFormParameterAccessCheck_Unknown_form_parameter_access;

    public static String UseNonRecommendedMethods_description;

    public static String UseNonRecommendedMethods_message;

    public static String UseNonRecommendedMethods_parameter;

    public static String UseNonRecommendedMethods_title;

    public static String MethodOptionalParameterBeforeRequiredCheck_description;

    public static String MethodOptionalParameterBeforeRequiredCheck_Optional_parameter_before_required;

    public static String MethodOptionalParameterBeforeRequiredCheck_title;

    public static String MethodTooManyPramsCheck_description;

    public static String MethodTooManyPramsCheck_Max_parameters;

    public static String MethodTooManyPramsCheck_Max_parameters_with_default_value;

    public static String MethodTooManyPramsCheck_Method_has_more_than__N__params;

    public static String MethodTooManyPramsCheck_Method_has_more_than__N__params_with_default_value;

    public static String MethodTooManyPramsCheck_title;

    public static String MissingTemporaryFileDeletionCheck_Delete_File_Methods;

    public static String MissingTemporaryFileDeletionCheck_description;

    public static String MissingTemporaryFileDeletionCheck_Missing_Temporary_File_Deletion;

    public static String MissingTemporaryFileDeletionCheck_title;

    public static String FormDataToValueCheck_Description;

    public static String FormDataToValueCheck_Issue;

    public static String FormDataToValueCheck_Title;

    public static String FormModulePragmaCheck_description;

    public static String FormModulePragmaCheck_Form_module_compilation_pragma_used;

    public static String FormModulePragmaCheck_title;

    public static String NotifyDescriptionToServerProcedureCheck_description;

    public static String NotifyDescriptionToServerProcedureCheck_Notify_description_procedure_should_be_export;

    public static String NotifyDescriptionToServerProcedureCheck_Notify_description_to_Server_procedure;

    public static String NotifyDescriptionToServerProcedureCheck_title;

    public static String FormModuleMissingPragmaCheck_description;

    public static String FormModuleMissingPragmaCheck_Missing_compilation_directives;

    public static String FormModuleMissingPragmaCheck_title;

    public static String FormSelfReferenceOutdatedCheck_Description;

    public static String FormSelfReferenceOutdatedCheck_Issue;

    public static String FormSelfReferenceOutdatedCheck_Title;

    public static String InvocationFormEventHandlerCheck_Program_invocation_of_form_event_handler_description;

    public static String InvocationFormEventHandlerCheck_Program_invocation_of_form_event_handler_result;

    public static String InvocationFormEventHandlerCheck_Program_invocation_of_form_event_handler_title;

    public static String IsInRoleCheck_Exception_Roles;

    public static String IsInRoleCheck_Use_AccessRight;

    public static String IsInRoleCheck_Use_AccessRight_instead_IsInRole;

    public static String IsInRoleCheck_Using_IsInRole;

    public static String IsInRoleMethodRoleExistCheck_description;

    public static String IsInRoleMethodRoleExistCheck_Role_named_not_exists_in_configuration;

    public static String IsInRoleMethodRoleExistCheck_title;

    public static String ModuleUndefinedVariableCheck_Title;
    public static String ModuleUndefinedVariableCheck_Description;
    public static String ModuleUndefinedVariable_msg;
    public static String ModuleUndefinedMethodCheck_Title;
    public static String ModuleUndefinedMethodCheck_Description;
    public static String ModuleUndefinedFunctionCheck_Title;
    public static String ModuleUndefinedFunctionCheck_Description;
    public static String ModuleUndefinedFunction_msg;
    public static String ModuleUndefinedMethod_msg;

    public static String LockOutOfTry_Checks_for_init_of_the_data_lock;

    public static String LockOutOfTry_Lock_out_of_try;

    public static String LockOutOfTry_Method_lock_out_of_try;

    public static String OptionalFormParameterAccessCheck_description;

    public static String OptionalFormParameterAccessCheck_Optional_form_parameter_access;

    public static String OptionalFormParameterAccessCheck_title;

    public static String VariableNameInvalidCheck_description;
    public static String VariableNameInvalidCheck_message_variable_length_is_less_than;
    public static String VariableNameInvalidCheck_param_MIN_NAME_LENGTH_PARAM_title;
    public static String VariableNameInvalidCheck_title;
    public static String VariableNameInvalidCheck_variable_name_is_invalid;
    public static String VariableNameInvalidCheck_variable_name_must_start_with_a_capital_letter;
    public static String VariableNameInvalidCheck_variable_name_starts_with_an_underline;

    public static String StringLiteralTypeAnnotationCheck_Title;
    public static String StringLiteralTypeAnnotationCheck_Incorrect_annotation_location;

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
