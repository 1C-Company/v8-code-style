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
package com.e1c.v8codestyle.ql.check;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.ql.check.messages"; //$NON-NLS-1$
    public static String CamelCaseStringLiteral_description;
    public static String CamelCaseStringLiteral_Regular_expression_to_skip_literal_content;
    public static String CamelCaseStringLiteral_String_literal_contains_non_CamelCase_symbols__0;
    public static String CamelCaseStringLiteral_title;
    public static String CastToMaxNumber_description;
    public static String CastToMaxNumber_Maximum_cast_number_length;
    public static String CastToMaxNumber_Maximum_cast_number_precision_or_N_to_skip_check;
    public static String CastToMaxNumber_Query_cast_to_number_with_lenth__0__and_max_allowed__1;
    public static String CastToMaxNumber_Query_cast_to_number_with_precision__0__and_max_allowed__1;
    public static String CastToMaxNumber_title;
    public static String JoinToSubQuery_description;
    public static String JoinToSubQuery_Query_join_to_sub_query_not_allowed;
    public static String JoinToSubQuery_title;
    public static String TempTableHasIndex_description;
    public static String TempTableHasIndex_Exclude_table_name_pattern;
    public static String TempTableHasIndex_New_temporary_table_should_have_indexes;
    public static String TempTableHasIndex_title;
    public static String UsingForUpdateCheck_description;
    public static String UsingForUpdateCheck_title;
    public static String VirtualTableFiltersCheck_description;
    public static String VirtualTableFiltersCheck_Filter__0_for_virtual_table__1__should_be_in_parameters;
    public static String VirtualTableFiltersCheck_title;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
