/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.qfix.external;

import com.e1c.g5.v8.dt.check.qfix.IFixSession;

/**
 * Functional delegate that allows to specify the matching method as a change execution logic
 *
 * @author Vadim Geraskin
 */
@FunctionalInterface
public interface VariantXtextModuleFixChangeDelegate
{
    /**
     * Perfoms apply of the fix
     *
     * @param context the quick fix context, cannot be {@code null}
     * @param session the session, cannot be {@code null}
     */
    void applyFix(SingleVariantXtextBslModuleFixContext context, IFixSession session);
}
