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
package com.e1c.v8codestyle.bsl.comment.check;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
    public static String CollectionTypeDefinitionCheck_Collection_type_should_have_contain_item_type;
    public static String CollectionTypeDefinitionCheck_Collection_types;
    public static String CollectionTypeDefinitionCheck_description;
    public static String CollectionTypeDefinitionCheck_title;
    public static String DocCommentUseMinusCheck_description;
    public static String DocCommentUseMinusCheck_Only_hyphen_minus_symbol_is_allowed_in_doc_comment_but_found_0;
    public static String DocCommentUseMinusCheck_title;
    public static String ExportFunctionReturnSectionCheck_description;
    public static String ExportFunctionReturnSectionCheck_Export_function_return_section_required;
    public static String ExportFunctionReturnSectionCheck_title;
    public static String ExportMethodCommentDescriptionCheck_description;
    public static String ExportMethodCommentDescriptionCheck_Missing_Description_in_export_procedure_comment;
    public static String ExportMethodCommentDescriptionCheck_title;
    public static String FieldDefinitionNameCheck_description;
    public static String FieldDefinitionNameCheck_Field_name__N__is_incorrect_name;
    public static String FieldDefinitionNameCheck_Field_name__N__is_not_unique;
    public static String FieldDefinitionNameCheck_title;
    public static String FieldDefinitionTypeCheck_description;
    public static String FieldDefinitionTypeCheck_Field_M_has_no_type_definition;
    public static String FieldDefinitionTypeCheck_title;
    public static String FieldDefinitionTypeWithLinkRefCheck_description;
    public static String FieldDefinitionTypeWithLinkRefCheck_Field__F__use_declaration_of_complex_type_instead_of_link;
    public static String FieldDefinitionTypeWithLinkRefCheck_title;
    public static String FunctionReturnSectionCheck_description;
    public static String FunctionReturnSectionCheck_Return_type_is_mandatory;
    public static String FunctionReturnSectionCheck_Return_type_unknown;
    public static String FunctionReturnSectionCheck_title;
    public static String MultilineDescriptionEndsOnDotCheck_description;
    public static String MultilineDescriptionEndsOnDotCheck_Method_comment_doesnt_ends_on_dot;
    public static String MultilineDescriptionEndsOnDotCheck_title;
    public static String MultilineDescriptionFieldSuggestionCheck_description;
    public static String MultilineDescriptionFieldSuggestionCheck_Probably_Field_is_defined_in_description;
    public static String MultilineDescriptionFieldSuggestionCheck_title;
    public static String MultilineDescriptionParameterSuggestionCheck_description;
    public static String MultilineDescriptionParameterSuggestionCheck_Probably_method_parameter_is_defined;
    public static String MultilineDescriptionParameterSuggestionCheck_title;
    public static String ParametersSectionCheck_Check_only_export_method_parameter_section;
    public static String ParametersSectionCheck_description;
    public static String ParametersSectionCheck_Parameter_definition_missed_for__N;
    public static String ParametersSectionCheck_Require_parameter_section_only_for_Export_methods;
    public static String RedundantParametersSectionCheck_description;
    public static String RedundantParametersSectionCheck_Remove_useless_parameter_section;
    public static String RedundantParametersSectionCheck_title;
    public static String ParametersSectionCheck_title;
    public static String ProcedureReturnSectionCheck_description;
    public static String ProcedureReturnSectionCheck_Procedure_should_has_no_return_section;
    public static String ProcedureReturnSectionCheck_title;
    public static String RefLinkPartCheck_Allow_See_in_description;
    public static String RefLinkPartCheck_description;
    public static String RefLinkPartCheck_Link_referenced_to_unexisting_object;
    public static String RefLinkPartCheck_title;
    public static String LinkPartSpaceCheck_Description;
    public static String LinkPartSpaceCheck_Issue;
    public static String LinkPartSpaceCheck_Title;
    public static String TypeDefinitionCheck_description;
    public static String TypeDefinitionCheck_title;
    public static String TypeDefinitionCheck_Unkown_type_M_specified;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
