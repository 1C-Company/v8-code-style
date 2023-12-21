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
package com.e1c.v8codestyle.internal.bsl.ui.views;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
    public static String BslDocCommentView_Description;
    public static String BslDocCommentView_Field;
    public static String BslDocCommentView_Link;
    public static String BslDocCommentView_Link_type;
    public static String BslDocCommentView_Parameters;
    public static String BslDocCommentView_Returns;
    public static String BslDocCommentView_Section;
    public static String BslDocCommentView_Text;
    public static String BslDocCommentView_Type;
    public static String BslDocCommentView_Types;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
