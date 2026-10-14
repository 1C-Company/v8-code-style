/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.check;

import com._1c.g5.v8.dt.metadata.mdclass.Configuration;

/**
 * The helper for SSL (Standard Subsystems Library) configurations
 *
 * @author Artem Samohvalov
 */
public class SslConfigurationHelper
{
    private static final String STANDARD_SUBSYSTEM_NAME = "StandardSubsystems"; //$NON-NLS-1$
    private static final String STANDARD_SUBSYSTEM_NAME_RU = "СтандартныеПодсистемы"; //$NON-NLS-1$

    /**
     * Checks for SSL.
     *
     * @param configuration the configuration, can be {@code null}
     * @return true, if successful
     */
    public static boolean hasSSL(Configuration configuration)
    {
        if (configuration == null)
        {
            return false;
        }

        return configuration.getSubsystems()
            .stream()
            .anyMatch(subsystem -> subsystem.getName().equalsIgnoreCase(STANDARD_SUBSYSTEM_NAME_RU)
                || subsystem.getName().equalsIgnoreCase(STANDARD_SUBSYSTEM_NAME));
    }

    private SslConfigurationHelper()
    {
    }
}
