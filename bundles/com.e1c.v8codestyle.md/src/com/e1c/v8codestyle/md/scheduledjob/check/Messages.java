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
package com.e1c.v8codestyle.md.scheduledjob.check;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitriy Marmyshev
 *
 */
final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
    public static String MdScheduledJobDescriptionCheck_title;
    public static String MdScheduledJobDescriptionCheck_description;
    public static String MdScheduledJobDescriptionCheck_message;
    public static String MdScheduledJobPeriodicityCheck_description;
    public static String MdScheduledJobPeriodicityCheck_The_minimum_job_interval_is_less_then_minute;
    public static String MdScheduledJobPeriodicityCheck_title;
    public static String MdScheduledJobPeriodicityCheck_Minimum_job_interval_description;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }
}
