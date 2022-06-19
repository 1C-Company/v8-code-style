/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.qfix;

import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * The fix to remove export keyword.
 *
 * @author Artem Iliukhin
 */
@QuickFix(checkId = "excess-export", supplierId = BslPlugin.PLUGIN_ID)
public final class RemoveExcessExportFix
    extends RemoveExportFix
{
    //
}
