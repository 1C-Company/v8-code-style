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
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.bsl.check.messages"; //$NON-NLS-1$

    public static String CanonicalPragmaCheck_description;
    public static String CanonicalPragmaCheck_Pragma_0_is_not_written_canonically_correct_spelling_is_1;
    public static String CanonicalPragmaCheck_title;

    public static String ChangeAndValidateInsteadOfAroundCheck_description;
    public static String ChangeAndValidateInsteadOfAroundCheck_Use_ChangeAndValidate_instead_of_Around;
    public static String ChangeAndValidateInsteadOfAroundCheck_title;

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

    public static String ModuleStructureTopRegionCheck_description;

    public static String ModuleStructureTopRegionCheck_error_message;

    public static String ModuleStructureTopRegionCheck_title;

    public static String QueryInLoop_check_query_in_infinite_loop;
    public static String QueryInLoop_description;
    public static String QueryInLoop_Loop_has_method_with_query__0;
    public static String QueryInLoop_Loop_has_query;
    public static String QueryInLoop_title;

    public static String StructureCtorTooManyKeysCheck_description;
    public static String StructureCtorTooManyKeysCheck_Maximum_structure_constructor_keys;
    public static String StructureCtorTooManyKeysCheck_Structure_constructor_has_more_than__0__keys;
    public static String StructureCtorTooManyKeysCheck_title;

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

    public static String UseNonRecommendedMethods_description;

    public static String UseNonRecommendedMethods_message;

    public static String UseNonRecommendedMethods_parameter;

    public static String UseNonRecommendedMethods_title;

    public static String MethodTooManyPramsCheck_description;

    public static String MethodTooManyPramsCheck_Max_parameters;

    public static String MethodTooManyPramsCheck_Max_parameters_with_default_value;

    public static String MethodTooManyPramsCheck_Method_has_more_than__N__params;

    public static String MethodTooManyPramsCheck_Method_has_more_than__N__params_with_default_value;

    public static String MethodTooManyPramsCheck_title;

    public static String FormModulePragmaCheck_description;

    public static String FormModulePragmaCheck_Form_module_compilation_pragma_used;

    public static String FormModulePragmaCheck_title;

    public static String NotifyDescriptionToServerProcedureCheck_description;

    public static String NotifyDescriptionToServerProcedureCheck_Notify_description_procedure_not_found;

    public static String NotifyDescriptionToServerProcedureCheck_Notify_description_procedure_should_be_export;

    public static String NotifyDescriptionToServerProcedureCheck_Notify_description_to_Server_procedure;

    public static String NotifyDescriptionToServerProcedureCheck_title;

    public static String FormModuleMissingPragmaCheck_description;

    public static String FormModuleMissingPragmaCheck_Missing_compilation_directives;

    public static String FormModuleMissingPragmaCheck_title;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
