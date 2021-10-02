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
package com.e1c.v8codestyle.internal.bsl;

import static com.e1c.v8codestyle.bsl.strict.StrictTypeUtil.BSL_FILE_EXTENSION;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;

/**
 * The default implementation of module structure provider.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructureProvider
    implements IModuleStructureProvider
{

    private static final String FOLDER_RU = "/templates/ru/"; //$NON-NLS-1$

    private static final String FOLDER_EN = "/templates/en/"; //$NON-NLS-1$

    private static final IPath FOLDER_SETTINGS = new org.eclipse.core.runtime.Path(".settings/templates"); //$NON-NLS-1$

    @Override
    public boolean canCreateStructure(IProject project)
    {
        ProjectScope scope = new ProjectScope(project);
        IScopeContext[] contexts = new IScopeContext[] { scope, InstanceScope.INSTANCE };

        return Platform.getPreferencesService()
            .getBoolean(BslPlugin.PLUGIN_ID, PREF_KEY_CREATE_STRUCTURE, PREF_DEFAULT_CREATE_STRUCTURE, contexts);
    }

    @Override
    public Supplier<InputStream> getModuleStructureTemplate(IProject project, ModuleType moduleType, ScriptVariant script)
    {

        if (moduleType == null || script == null)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(moduleType.getName().toLowerCase());
        sb.append("."); //$NON-NLS-1$
        sb.append(BSL_FILE_EXTENSION);

        IFile templateFile = project.getFile(FOLDER_SETTINGS.append(sb.toString()));

        if (script == ScriptVariant.ENGLISH)
        {
            sb.insert(0, FOLDER_EN);
        }
        else
        {
            sb.insert(0, FOLDER_RU);
        }
        String path = sb.toString();

        if (templateFile.exists())
        {
            return () -> {
                try
                {
                    return templateFile.getContents();
                }
                catch (CoreException e)
                {
                    IStatus message =
                        BslPlugin.createErrorStatus("Cannot read tempate file " + templateFile.toString(), e); //$NON-NLS-1$
                    BslPlugin.log(message);
                }
                return getClass().getResourceAsStream(path);
            };
        }


        Optional<Path> template = getBundleEntry(path);
        if (template.isPresent())
        {
            return () -> getClass().getResourceAsStream(path);
        }
        String message = MessageFormat.format("Cannot find module template for type: {0} and language: {1} in {2}", //$NON-NLS-1$
            moduleType.getName(), script, path);
        IStatus status = BslPlugin.createWarningStatus(message);
        BslPlugin.log(status);
        return null;

    }

    private Optional<Path> getBundleEntry(String path)
    {
        URL url = getClass().getResource(path);
        if (url == null)
            return Optional.empty();
        try
        {
            URL fileURL = FileLocator.toFileURL(url);
            return Optional.of(Paths.get(fileURL.toURI()));
        }
        catch (IOException | java.net.URISyntaxException e)
        {
            BslPlugin.logError(e);
        }
        return Optional.empty();
    }
}
