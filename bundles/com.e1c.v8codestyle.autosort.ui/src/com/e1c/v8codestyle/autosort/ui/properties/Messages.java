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
package com.e1c.v8codestyle.autosort.ui.properties;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "com.e1c.v8codestyle.autosort.ui.properties.messages"; //$NON-NLS-1$
    public static String AutoSortPropertyPage_All_subordinate_objects;
    public static String AutoSortPropertyPage_All_top_metadata_objects;
    public static String AutoSortPropertyPage_Ascending;
    public static String AutoSortPropertyPage_Attributes_of_object;
    public static String AutoSortPropertyPage_Automatically_sort_medata_objects_on_edit;
    public static String AutoSortPropertyPage_Commands_of_object;
    public static String AutoSortPropertyPage_Descending;
    public static String AutoSortPropertyPage_Forms_of_object;
    public static String AutoSortPropertyPage_Methods_of_URL_template;
    public static String AutoSortPropertyPage_Operations_of_Web_service;
    public static String AutoSortPropertyPage_Resources_of_registry;
    public static String AutoSortPropertyPage_Select_subordinate_objects;
    public static String AutoSortPropertyPage_Select_subordinate_objects_description;
    public static String AutoSortPropertyPage_Select_top_objects;
    public static String AutoSortPropertyPage_Select_top_objects_description;
    public static String AutoSortPropertyPage_Sort_direction;
    public static String AutoSortPropertyPage_Sort_question;
    public static String AutoSortPropertyPage_Sort_question_title;
    public static String AutoSortPropertyPage_Tabular_sections_of_object;
    public static String AutoSortPropertyPage_Templates_of_object;
    public static String AutoSortPropertyPage_URL_templates_of_HTTP_service;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
