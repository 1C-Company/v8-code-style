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

import com._1c.g5.v8.bm.core.IBmTransaction;

/**
 * The helper for SSL (Standard Subsystems Library) configurations
 *
 * @author Artem Samohvalov
 */
public class SslConfigurationHelper
{

    /**
     * Checks for SSL.
     *
     * @param bmTransaction the bm transaction, cannot be {@code null}
     * @return true, if successful
     */
    public static boolean hasSSL(IBmTransaction bmTransaction)
    {
        return hasTopObject(bmTransaction, "Subsystem.СтандартныеПодсистемы") //$NON-NLS-1$
            || hasTopObject(bmTransaction, "Subsystem.StandardSubsystems"); //$NON-NLS-1$
    }

    private static boolean hasTopObject(IBmTransaction bmTransaction, String fqn)
    {
        return bmTransaction.getTopObjectByFqn(fqn) != null;
    }

    private SslConfigurationHelper()
    {
    }
}
