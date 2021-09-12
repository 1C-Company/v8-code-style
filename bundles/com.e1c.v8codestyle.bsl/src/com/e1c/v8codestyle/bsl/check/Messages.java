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
 *     Aleksandr Kapralov - issue #17
 *******************************************************************************/
/**
 *
 */
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

    public static String EventDataExchangeLoadCheck_Check_at_the_beginning_of_event_handler;

    public static String EventDataExchangeLoadCheck_description;

    public static String EventDataExchangeLoadCheck_Function_list_that_checks_DataExchange_Load;

    public static String EventDataExchangeLoadCheck_Mandatory_checking_of_DataExchangeLoad_is_absent_in_event_handler_0;

    public static String EventDataExchangeLoadCheck_No_return_in__DataExchange_Load__checking;

    public static String EventDataExchangeLoadCheck_title;

    public static String QueryInLoop_check_query_in_infinite_loop;
    public static String QueryInLoop_description;
    public static String QueryInLoop_Loop_has_query;
    public static String QueryInLoop_Loop_has_method_with_query__0;
    public static String QueryInLoop_title;

    public static String StructureCtorTooManyKeysCheck_description;
    public static String StructureCtorTooManyKeysCheck_Maximum_structure_constructor_keys;
    public static String StructureCtorTooManyKeysCheck_Structure_constructor_has_more_than__0__keys;
    public static String StructureCtorTooManyKeysCheck_title;
    
    
    public static String EmptyExceptStatementCheck_title;
    public static String EmptyExceptStatementCheck_description;
    

    public static String MethodTooManyPramsCheck_description;

    public static String MethodTooManyPramsCheck_Max_parameters;

    public static String MethodTooManyPramsCheck_Max_parameters_with_default_value;

    public static String MethodTooManyPramsCheck_Method_has_more_than__N__params;

    public static String MethodTooManyPramsCheck_Method_has_more_than__N__params_with_default_value;

    public static String MethodTooManyPramsCheck_title;


    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
