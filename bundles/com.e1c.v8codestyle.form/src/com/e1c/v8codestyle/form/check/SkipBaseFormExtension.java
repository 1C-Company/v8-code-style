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
package com.e1c.v8codestyle.form.check;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.ext.ITopObjectFilter;

/**
 * Check extension to avoid checking inside of a BaseForm
 *
 * @author Vadim Geraskin
 */
public class SkipBaseFormExtension
    implements IBasicCheckExtension
{
    @Override
    public ITopObjectFilter contributeTopObjectFilter()
    {
        return (IBmObject object, ICheckParameters parameters) -> !isBaseForm((Form)object);
    }

    private static boolean isBaseForm(Form form)
    {
        return form != null && form.getMdForm().getObjectBelonging() == ObjectBelonging.ADOPTED
            && form.getExtensionForm() != null && !form.getExtensionForm().eIsProxy()
            && (form.getBaseForm() == null || form.getBaseForm().eIsProxy());
    }
}
