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
package com.e1c.v8codestyle.bsl.check;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com.e1c.g5.v8.dt.check.components.BasicCheck;

/**
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 *
 */
public abstract class AbstractModuleStructureCheck
    extends BasicCheck
{

    protected boolean isEmpty(RegionPreprocessor region)
    {
        PreprocessorItem item = region.getItem();
        return item == null || !item.hasElement();
    }

    protected ModuleType getModuleType(EObject context)
    {
        Module module = EcoreUtil2.getContainerOfType(context, Module.class);
        if (module == null)
        {
            return null;
        }

        return module.getModuleType();
    }

    protected Optional<RegionPreprocessor> getParentRegion(RegionPreprocessor object)
    {
        EObject parent = object.eContainer();
        PreprocessorItem lastItem = null;
        do
        {
            if (parent instanceof RegionPreprocessor)
            {
                RegionPreprocessor parentRegion = (RegionPreprocessor)parent;

                if (parentRegion.getItem().equals(lastItem) || parentRegion.getItem().eContents().contains(object))
                {
                    return Optional.ofNullable(parentRegion);
                }

                return Optional.empty();
            }
            else if (parent instanceof PreprocessorItem)
            {
                lastItem = (PreprocessorItem)parent;
            }
            parent = parent.eContainer();
        }
        while (parent != null);

        return Optional.empty();
    }

}
