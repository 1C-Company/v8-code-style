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

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.filesystem.IQualifiedNameFilePathConverter;
import com._1c.g5.v8.dt.metadata.mdclass.AbstractForm;
import com._1c.g5.v8.dt.ui.wizards.IDtNewWizardContext;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;
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
        if (!StrictTypeUtil.canAddModuleStrictTypesAnnotation(project))
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
                    createOrUpdateModule(bslFile);
                }
            }
        }

        if (formToAddModule != null)
        {
            IFile bslFile = getModuleFile(formToAddModule, project);
            if (bslFile != null)
            {
                createOrUpdateModule(bslFile);

                EObject module = createBslProxyModule(bslFile);
                createdModels.add(module);
            }
        }

    }

    private void createOrUpdateModule(IFile bslFile)
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

        try
        {
            if (!bslFile.exists())
            {
                createParentFolders(bslFile);
            }
            StrictTypeUtil.setStrictTypeAnnotation(bslFile, new NullProgressMonitor());
        }
        catch (IOException | CoreException e)
        {
            IStatus status = UiPlugin.createErrorStatus("Can't create bsl file with name: " + bslFile.getName(), e); //$NON-NLS-1$
            UiPlugin.log(status);
        }
    }

}
