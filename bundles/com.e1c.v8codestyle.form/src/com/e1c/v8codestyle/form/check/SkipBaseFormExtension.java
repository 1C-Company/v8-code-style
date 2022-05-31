/**
 * Copyright (C) 2022, 1C
 */
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
        if (form != null && form.getMdForm().getObjectBelonging() == ObjectBelonging.ADOPTED
            && form.getExtensionForm() != null && !form.getExtensionForm().eIsProxy()
            && (form.getBaseForm() == null || form.getBaseForm().eIsProxy()))
        {
            return true;
        }
        return false;
    }
}
