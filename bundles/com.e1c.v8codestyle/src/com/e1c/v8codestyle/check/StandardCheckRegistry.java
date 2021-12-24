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
package com.e1c.v8codestyle.check;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.v8codestyle.internal.CorePlugin;
import com.google.inject.Singleton;

/**
 * The registry of IDs of check by 1C:Standards that may be registered via {@link StandardCheckExtension}
 * added to the check.
 *
 * @author Dmitriy Marmyshev
 */
@Singleton
public final class StandardCheckRegistry
{

    private final Set<CheckUid> checks = ConcurrentHashMap.newKeySet();

    public static StandardCheckRegistry getInstance()
    {
        return CorePlugin.getDefault().getInjector().getInstance(StandardCheckRegistry.class);
    }

    /**
     * Gets all checks by 1C:Standards, clients can not modify this collection.
     *
     * @return the checks, never returns {@code null}.
     */
    public Set<CheckUid> getChecks()
    {
        return Set.copyOf(checks);
    }

    /**
     * Register check for internal use only.
     *
     * @param check the check
     */
    void registerCheck(CheckUid check)
    {
        if (check == null)
        {
            return;
        }
        checks.add(check);
    }

}
