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
package com.e1c.v8codestyle.bsl;

import java.io.InputStream;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;

import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;

/**
 * The provider of module structure template by module type.
 * This service return either template from project settings or default template from the bundle.
 *
 *
 * @author Dmitriy Marmyshev
 */
public interface IModuleStructureProvider
{

    /** The key for preferences store the state of the creating module structure. */
    String PREF_KEY_CREATE_STRUCTURE = "createModuleStructure"; //$NON-NLS-1$

    /** The default value of creating module structure. */
    boolean PREF_DEFAULT_CREATE_STRUCTURE = true;

    /**
     * Can create module structure template for the project. This checks project or default settings.
     *
     * @param project the project, cannot be {@code null}.
     * @return true, if can create module structure for the project
     */
    boolean canCreateStructure(IProject project);

    /**
     * Gets the module structure template supplier of input stream.
     *
     * @param project the project, cannot be {@code null}.
     * @param moduleType the module type, cannot be {@code null}.
     * @param script the script, cannot be {@code null}.
     * @return the module structure template supplier of input stream, may return {@code null} if there is no template
     * for such module type and script variant.
     */
    Supplier<InputStream> getModuleStructureTemplate(IProject project, ModuleType moduleType, ScriptVariant script);


}
