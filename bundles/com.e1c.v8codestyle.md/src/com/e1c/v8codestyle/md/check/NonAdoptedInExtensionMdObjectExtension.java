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

import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.ext.ITopObjectFilter;

/**
 * The extension TOP MD object that nonadopted in Extension Configuration.
 *
 * @author Artem Iliukhin
 */
public class NonAdoptedInExtensionMdObjectExtension
    implements IBasicCheckExtension
{
    private final IV8ProjectManager v8ProjectManager;
    public NonAdoptedInExtensionMdObjectExtension(IV8ProjectManager v8ProjectManager)
    {
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    public ITopObjectFilter contributeTopObjectFilter()
    {
        return (object, parameters) -> {
            if (object instanceof MdObject)
            {
                IV8Project extension = v8ProjectManager.getProject(object);
                if (extension instanceof IExtensionProject && ((IExtensionProject)extension).getParent() != null)
                {
                    return ((MdObject)object).getObjectBelonging() != ObjectBelonging.ADOPTED;
                }
            }
            return false;
        };
    }

}
