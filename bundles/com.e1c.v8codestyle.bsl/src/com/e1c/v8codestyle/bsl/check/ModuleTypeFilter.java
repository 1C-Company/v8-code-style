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

import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;

/**
 * The extension allows to skip checking the module by it's type.
 *
 * @author Dmitriy Marmyshev
 */
public final class ModuleTypeFilter
    implements IBasicCheckExtension
{

    /**
     * Create extension that allows to check module with certain types.
     *
     * @param types the types, cannot be {@code null}.
     * @return the module type filter, cannot return {@code null}.
     */
    public static ModuleTypeFilter onlyTypes(ModuleType... types)
    {
        Assert.isNotNull(types);
        Set<ModuleType> include = Set.of(types);
        return new ModuleTypeFilter(include);
    }

    /**
     * Create extension that allows to check module with all types excluding specified.
     *
     * @param types the types, cannot be {@code null}.
     * @return the module type filter, cannot return {@code null}.
     */
    public static ModuleTypeFilter excludeTypes(ModuleType... types)
    {
        Assert.isNotNull(types);
        Set<ModuleType> exclude = Set.of(types);

        Set<ModuleType> include =
            Set.copyOf(ModuleType.VALUES.stream().filter(t -> !exclude.contains(t)).collect(Collectors.toSet()));
        return new ModuleTypeFilter(include);
    }

    private final Set<ModuleType> types;

    private ModuleTypeFilter(Set<ModuleType> types)
    {
        this.types = types;
    }

    @Override
    public boolean preCheck(Object object, ICheckParameters parameters, IProgressMonitor progressMonitor)
    {
        ModuleType type = null;
        if (object instanceof EObject)
        {
            type = getModuleType((EObject)object);
        }
        else if (object instanceof IDescriptionPart)
        {
            type = getModuleType((IDescriptionPart)object);
        }

        return type != null && types.contains(type);
    }

    private ModuleType getModuleType(EObject context)
    {
        Module module = EcoreUtil2.getContainerOfType(context, Module.class);
        if (module == null)
        {
            return null;
        }

        return module.getModuleType();
    }

    private ModuleType getModuleType(IDescriptionPart context)
    {
        IDescriptionPart parent = context;
        while (parent != null)
        {
            if (parent instanceof BslDocumentationComment)
            {
                BslDocumentationComment root = (BslDocumentationComment)parent;
                return getModuleType(root.getModule());
            }
            parent = parent.getParent();
        }

        return null;
    }
}
