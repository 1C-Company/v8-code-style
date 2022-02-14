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
package com.e1c.v8codestyle.bsl.strict.check;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.bsl.strict.check.messages"; //$NON-NLS-1$
    public static String AbstractDynamicFeatureAccessTypeCheck_Skip_source_object_types;
    public static String DocCommentFieldTypeCheck_description;
    public static String DocCommentFieldTypeCheck_Field__N__has_no_type_definition;
    public static String DocCommentFieldTypeCheck_title;
    public static String DynamicFeatureAccessMethodNotFoundCheck_description;
    public static String DynamicFeatureAccessMethodNotFoundCheck_title;
    public static String DynamicFeatureAccessTypeCheck_description;
    public static String DynamicFeatureAccessTypeCheck_title;
    public static String DynamicFeatureAccessTypeCheck_Feature_access_M_has_no_return_type;
    public static String DynamicFeatureAccessTypeCheck_Method_M_not_found_in_accessed_object;
    public static String FunctionCtorReturnSectionCheck_description;
    public static String FunctionCtorReturnSectionCheck_title;
    public static String FunctionCtorReturnSectionCheck_User_extandable_Data_type_list_comma_separated;
    public static String FunctionCtorReturnSectionCheck_Declared_property__N__with_type__T__missing_returning_types__M;
    public static String FunctionCtorReturnSectionCheck_Declared_property__N__with_type__T__not_returning;
    public static String FunctionCtorReturnSectionCheck_Return_non_declared_propertes__N;
    public static String FunctionCtorReturnSectionCheck_Return_non_declared_type__T;
    public static String FunctionReturnTypeCheck_description;
    public static String FunctionReturnTypeCheck_title;
    public static String FunctionReturnTypeCheck_Function_has_no_return_value_type;
    public static String InvocationParamIntersectionCheck_Allow_dynamic_types_check_for_local_method_call;
    public static String InvocationParamIntersectionCheck_description;
    public static String InvocationParamIntersectionCheck_title;
    public static String MethodParamTypeCheck_description;
    public static String MethodParamTypeCheck_title;
    public static String MethodParamTypeCheck_Method_param_N_has_no_value_type;
    public static String SimpleStatementTypeCheck_Allow_local_Variable_reset_to_Undefined_type;
    public static String SimpleStatementTypeCheck_description;
    public static String SimpleStatementTypeCheck_title;
    public static String SimpleStatementTypeCheck_Value_type_N_changed_to_M;
    public static String StrictModuleInvocationCheck_Type_of_N_parameter_not_intersect_with_invocation_type;
    public static String StrictTypeAnnotationCheckExtension_Check__strict_types_annotation_in_module_desctioption;
    public static String StructureCtorValueTypeCheck_description;
    public static String StructureCtorValueTypeCheck_Structure_key__N__K__has_no_default_value_initializer;
    public static String StructureCtorValueTypeCheck_Structure_key__N__K__value_initialized_with_empty_types;
    public static String StructureCtorValueTypeCheck_title;
    public static String VariableTypeCheck_description;
    public static String VariableTypeCheck_title;
    public static String VariableTypeCheck_Variable_M_has_no_value_type;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
