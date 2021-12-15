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
package com.e1c.v8codestyle.internal.bsl.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.documentation.comment.IBslCommentToken;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.filesystem.IQualifiedNameFilePathConverter;
import com._1c.g5.v8.dt.metadata.mdclass.AbstractForm;
import com._1c.g5.v8.dt.ui.wizards.IDtNewWizardContext;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;

/**
 * A factory for creating strict-types module when creating new object in wizard.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStrictTypesNewWizardRelatedModelsFactory
    extends AbstractModuleNewWizardRelatedModelsFactory
{

    @Inject
    public ModuleStrictTypesNewWizardRelatedModelsFactory(
        IQualifiedNameFilePathConverter qualifiedNameFilePathConverter)
    {
        super(qualifiedNameFilePathConverter);
    }

    @Override
    public void createModels(IDtNewWizardContext<EObject> context, Set<EObject> createdModels)
    {
        IProject project = context.getV8project().getProject();
        if (!StrictTypeUtil.canCreateStrictTypesModule(project))
        {
            return;
        }

        AbstractForm formToAddModule = null;
        for (EObject model : context.getRelatedModels())
        {
            if (model instanceof AbstractForm)
            {
                formToAddModule = (AbstractForm)model;
            }
            else if (model instanceof Module)
            {
                formToAddModule = null;
                Module module = (Module)model;
                IFile bslFile = getModuleFile(module);
                if (bslFile != null)
                {
                    createOrUpdateModule(bslFile, context);
                }
            }
        }

        if (formToAddModule != null)
        {
            IFile bslFile = getModuleFile(formToAddModule, project);
            if (bslFile != null)
            {
                createOrUpdateModule(bslFile, context);

                EObject module = createBslProxyModule(bslFile);
                createdModels.add(module);
            }
        }

    }

    private void createOrUpdateModule(IFile bslFile, IDtNewWizardContext<EObject> context)
    {
        try
        {
            if (bslFile.exists() && StrictTypeUtil.hasStrictTypeAnnotation(bslFile))
            {
                return;
            }
        }
        catch (CoreException | IOException e)
        {
            UiPlugin.logError(e);
        }

        String currentCode = StringUtils.EMPTY;
        if (bslFile.exists())
        {
            try (InputStream in = bslFile.getContents();
                Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);)
            {
                currentCode = CharStreams.toString(reader);
            }
            catch (IOException | CoreException e)
            {
                IStatus status = UiPlugin.createErrorStatus("Can't read bsl file with name: " + bslFile.getName(), e); //$NON-NLS-1$
                UiPlugin.log(status);
            }
        }

        IProject project = context.getV8project().getProject();
        String preferedLineSeparator = PreferenceUtils.getLineSeparator(project);
        StringBuilder sb = new StringBuilder();

        int insertOffset = getInserOffset(currentCode);
        if (insertOffset > 0)
        {
            sb.append(currentCode.substring(0, insertOffset));
            sb.append(preferedLineSeparator);
        }

        sb.append(IBslCommentToken.LINE_STARTER);
        sb.append(" "); //$NON-NLS-1$
        sb.append(StrictTypeUtil.STRICT_TYPE_ANNOTATION);
        sb.append(preferedLineSeparator);
        sb.append(preferedLineSeparator);
        sb.append(currentCode.substring(insertOffset));

        try (InputStream in = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));)
        {
            if (bslFile.exists())
            {
                bslFile.setContents(in, true, true, new NullProgressMonitor());
            }
            else
            {
                createParentFolders(bslFile);
                bslFile.create(in, true, new NullProgressMonitor());
            }
        }
        catch (CoreException | IOException e)
        {
            IStatus status = UiPlugin.createErrorStatus("Can't create bsl file with name: " + bslFile.getName(), e); //$NON-NLS-1$
            UiPlugin.log(status);
        }
    }

    private int getInserOffset(String currentCode)
    {
        int separator = resolveLineSeparator(currentCode).length();

        int offset = 0;
        for (Iterator<String> iterator = currentCode.lines().iterator(); iterator.hasNext();)
        {
            if (offset > 0)
            {
                offset = offset + separator;
            }

            String line = iterator.next();
            if (StringUtils.isBlank(line))
            {
                return offset;
            }
            else if (!line.stripLeading().startsWith(IBslCommentToken.LINE_STARTER))
            {
                return 0;
            }

            offset = offset + line.length();

        }
        return 0;
    }

}
