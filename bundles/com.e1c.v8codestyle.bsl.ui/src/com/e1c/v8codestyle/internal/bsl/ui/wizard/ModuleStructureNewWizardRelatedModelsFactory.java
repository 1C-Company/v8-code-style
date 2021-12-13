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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.ABSTRACT_FORM__MODULE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.QualifiedName;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.util.BslUtil;
import com._1c.g5.v8.dt.common.FileUtil;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.filesystem.IQualifiedNameFilePathConverter;
import com._1c.g5.v8.dt.metadata.mdclass.AbstractForm;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.ui.wizards.IDtNewWizardContext;
import com._1c.g5.v8.dt.ui.wizards.IDtNewWizardRelatedModelsFactory;
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;

/**
 * A factory for creating module structure when creating new object in wizard.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructureNewWizardRelatedModelsFactory
    implements IDtNewWizardRelatedModelsFactory<EObject>
{

    private static final String CURRENT_CODE = "//%CURRENT_CODE%"; //$NON-NLS-1$

    private static final String LINE_SEPARATOR_WIN = "\r\n"; //$NON-NLS-1$

    private static final String LINE_SEPARATOR_LINUX = "\n"; //$NON-NLS-1$

    private final IQualifiedNameFilePathConverter qualifiedNameFilePathConverter;

    private final IModuleStructureProvider moduleStructureProvider;

    /**
     * Instantiates a new module structure new wizard related models factory.
     *
     * @param qualifiedNameFilePathConverter the qualified name file path converter service, cannot be {@code null}.
     * @param moduleStructureProvider the module structure provider service, cannot be {@code null}.
     */
    @Inject
    public ModuleStructureNewWizardRelatedModelsFactory(IQualifiedNameFilePathConverter qualifiedNameFilePathConverter,
        IModuleStructureProvider moduleStructureProvider)
    {
        this.qualifiedNameFilePathConverter = qualifiedNameFilePathConverter;
        this.moduleStructureProvider = moduleStructureProvider;
    }

    @Override
    public void createModels(IDtNewWizardContext<EObject> context, Set<EObject> createdModels)
    {
        IProject project = context.getV8project().getProject();
        if (!moduleStructureProvider.canCreateStructure(project))
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
                    ModuleType type = BslUtil.computeModuleType(module, qualifiedNameFilePathConverter);
                    createOrUpdateModule(bslFile, type, context);
                }
            }
        }

        if (formToAddModule != null)
        {
            IFile bslFile = getModuleFile(formToAddModule, project);
            if (bslFile != null)
            {
                createOrUpdateModule(bslFile, ModuleType.FORM_MODULE, context);

                EObject module = createBslProxyModule(bslFile);
                createdModels.add(module);
            }
        }
    }

    private void createOrUpdateModule(IFile bslFile, ModuleType type, IDtNewWizardContext<EObject> context)
    {
        ScriptVariant script = context.getV8project().getScriptVariant();
        Supplier<InputStream> content =
            moduleStructureProvider.getModuleStructureTemplate(bslFile.getProject(), type, script);
        if (content == null)
        {
            return;
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

        try (InputStream template = content.get();
            Reader reader = new InputStreamReader(template, StandardCharsets.UTF_8);)
        {
            String text = CharStreams.toString(reader);

            // Depends where plug-in is build (Windows or Linux) need to fix line-endings with
            // project settings.
            String actualLineSeparator = resolveLineSeparator(text);
            String preferedLineSeparator = PreferenceUtils.getLineSeparator(project);
            if (!actualLineSeparator.equals(preferedLineSeparator))
            {
                text = text.replace(actualLineSeparator, preferedLineSeparator);
            }

            if (text.contains(CURRENT_CODE))
            {
                text = text.replace(CURRENT_CODE, currentCode);
            }
            else if (!currentCode.isEmpty())
            {
                text = text.concat(currentCode);
            }

            InputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
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

    private String resolveLineSeparator(String text)
    {
        return text.contains(LINE_SEPARATOR_WIN) ? LINE_SEPARATOR_WIN : LINE_SEPARATOR_LINUX;
    }

    private EObject createBslProxyModule(IFile bslFile)
    {
        EObject module = EcoreUtil.create(MODULE);
        URI uri = URI.createPlatformResourceURI(bslFile.getFullPath().toString(), true).appendFragment("/0"); //$NON-NLS-1$
        ((InternalEObject)module).eSetProxyURI(uri);
        return module;
    }

    private IFile getModuleFile(Module model)
    {
        URI uri = EcoreUtil.getURI(model);

        if (uri != null && uri.isPlatform())
        {
            IPath path = new Path(uri.trimFragment().toPlatformString(true));
            return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        }
        return null;
    }

    private IFile getModuleFile(AbstractForm model, IProject project)
    {
        String fqn = ((IBmObject)model).bmGetFqn();

        QualifiedName name = QualifiedName.create(fqn).append(StringUtils.capitalize(ABSTRACT_FORM__MODULE.getName()));

        IPath path = qualifiedNameFilePathConverter.getFilePath(name, MODULE);

        return project.getFile(path);
    }

    private void createParentFolders(IFile bslFile)
    {
        try
        {
            FileUtil.createParentFolders(bslFile);
        }
        catch (Exception e)
        {
            // skip if cannot create parent folders in other threads
        }
    }

}
