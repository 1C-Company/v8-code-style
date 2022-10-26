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
package com.e1c.v8codestyle.bsl.check;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;

/**
 *  Filters a module owner is it was adopted on precheck phase extension.
 *
 * @author Artem Iliukhin
 */
public class AdoptedModuleOwnerExtension
    implements IBasicCheckExtension
{

    @Override
    public boolean preCheck(Object object, ICheckParameters parameters, IProgressMonitor progressMonitor)
    {
        Module module = EcoreUtil2.getContainerOfType((EObject)object, Module.class);
        return isParentAdopted(module);
    }

    private boolean isParentAdopted(Module module)
    {
        EObject owner = module.getOwner();
        if (owner == null)
        {
            return false;
        }
        if (owner.eIsProxy())
        {
            owner = EcoreUtil.resolve(owner, module);
        }
        if (!owner.eIsProxy())
        {
            if (owner instanceof Form)
            {
                owner = ((Form)owner).getMdForm();
            }
            return owner instanceof MdObject && ((MdObject)owner).getObjectBelonging() == ObjectBelonging.ADOPTED;
        }
        return false;
    }
}
