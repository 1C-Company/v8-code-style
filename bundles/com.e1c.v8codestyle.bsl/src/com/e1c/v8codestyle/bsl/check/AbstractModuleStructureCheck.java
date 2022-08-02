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
 * Abstract check of module structure, which declare top region names and order, where method should be included
 * and etc.
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 */
public abstract class AbstractModuleStructureCheck
    extends BasicCheck
{

    /**
     * Checks if the region is empty and has no any code elements.
     *
     * @param region the region, cannot be {@code null}.
     * @return true, if the region is empty
     */
    protected boolean isEmpty(RegionPreprocessor region)
    {
        PreprocessorItem item = region.getItem();
        return item == null || !item.hasElement();
    }

    /**
     * Gets the type of module by the given module objec as contect.
     *
     * @param context the context, cannot be {@code null}.
     * @return the module type, may return {@code null} if cannot find module or the module has no type.
     */
    protected ModuleType getModuleType(EObject context)
    {
        Module module = EcoreUtil2.getContainerOfType(context, Module.class);
        if (module == null)
        {
            return null;
        }

        return module.getModuleType();
    }

    /**
     * Gets the parent region of given region.
     *
     * @param object the region to find the parent, cannot be {@code null}.
     * @return the parent region, cannot return {@code null}.
     */
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

    /**
     * Gets the top parent region.
     *
     * @param object the object, cannot be {@code null}.
     * @return the top parent region, cannot return {@code null}.
     */
    protected Optional<RegionPreprocessor> getTopParentRegion(EObject object)
    {
        EObject parent = object.eContainer();
        PreprocessorItem lastItem = null;
        RegionPreprocessor region = null;
        do
        {
            if (parent instanceof RegionPreprocessor)
            {
                RegionPreprocessor parentRegion = (RegionPreprocessor)parent;
                if (lastItem != null && parentRegion.getItem().equals(lastItem))
                {
                    region = parentRegion;
                }
                else
                {
                    lastItem = null;
                }
            }
            else if (parent instanceof PreprocessorItem)
            {
                lastItem = (PreprocessorItem)parent;
            }
            parent = parent.eContainer();
        }
        while (parent != null);

        return Optional.ofNullable(region);
    }
}
