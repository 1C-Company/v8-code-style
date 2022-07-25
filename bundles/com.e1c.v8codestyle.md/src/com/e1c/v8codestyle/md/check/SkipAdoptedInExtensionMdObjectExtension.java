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
package com.e1c.v8codestyle.md.check;

import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.ext.ITopObjectFilter;

/**
 * The extension skips TOP MD object that adopted in Extension Configuration.
 *
 * @author Dmitriy Marmyshev
 */
public class SkipAdoptedInExtensionMdObjectExtension
    implements IBasicCheckExtension
{
    @Override
    public ITopObjectFilter contributeTopObjectFilter()
    {
        return (object, parameters) -> {

            if (object instanceof MdObject)
            {
                return ((MdObject)object).getObjectBelonging() != ObjectBelonging.ADOPTED;
            }
            return true;
        };
    }

}
