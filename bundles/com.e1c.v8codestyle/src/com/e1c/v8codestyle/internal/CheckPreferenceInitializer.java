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
package com.e1c.v8codestyle.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;

import com.e1c.v8codestyle.check.CheckUtils;

/**
 * The preference initializer for check options.
 *
 * @author Dmitriy Marmyshev
 */
public class CheckPreferenceInitializer
    extends AbstractPreferenceInitializer
{

    @Override
    public void initializeDefaultPreferences()
    {
        DefaultScope.INSTANCE.getNode(CheckUtils.PREF_QUALIFIER)
            .putBoolean(CheckUtils.PREF_KEY_COMMON_CHECKS, CheckUtils.PREF_DEFAULT_COMMON_CHECKS);
        DefaultScope.INSTANCE.getNode(CheckUtils.PREF_QUALIFIER)
            .putBoolean(CheckUtils.PREF_KEY_STANDARD_CHECKS, CheckUtils.PREF_DEFAULT_STANDARD_CHECKS);
    }

}
