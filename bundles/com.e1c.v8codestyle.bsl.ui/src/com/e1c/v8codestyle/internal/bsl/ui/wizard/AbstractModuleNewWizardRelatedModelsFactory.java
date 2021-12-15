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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.QualifiedName;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.common.FileUtil;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.filesystem.IQualifiedNameFilePathConverter;
import com._1c.g5.v8.dt.metadata.mdclass.AbstractForm;
import com._1c.g5.v8.dt.ui.wizards.IDtNewWizardRelatedModelsFactory;

/**
 * Abstract factory for creating module in new-wizard.
 *
 * @author Dmitriy Marmyshev
 */
public abstract class AbstractModuleNewWizardRelatedModelsFactory
    implements IDtNewWizardRelatedModelsFactory<EObject>
{

    private static final String LINE_SEPARATOR_WIN = "\r\n"; //$NON-NLS-1$

    private static final String LINE_SEPARATOR_LINUX = "\n"; //$NON-NLS-1$

    protected final IQualifiedNameFilePathConverter qualifiedNameFilePathConverter;

    /**
     * Instantiates a new abstract module new wizard related models factory.
     *
     * @param qualifiedNameFilePathConverter the qualified name file path converter, cannot be {@code null}.
     */
    protected AbstractModuleNewWizardRelatedModelsFactory(
        IQualifiedNameFilePathConverter qualifiedNameFilePathConverter)
    {
        this.qualifiedNameFilePathConverter = qualifiedNameFilePathConverter;
    }

    /**
     * Resolve line separator from text content.
     *
     * @param text the text, cannot be {@code null}.
     * @return the string of line separator.
     */
    protected String resolveLineSeparator(String text)
    {
        return text.contains(LINE_SEPARATOR_WIN) ? LINE_SEPARATOR_WIN : LINE_SEPARATOR_LINUX;
    }

    /**
     * Creates a new BSL proxy module object.
     *
     * @param bslFile the BSL file, cannot be {@code null}.
     * @return the object of module, never returns {@code null}.
     */
    protected EObject createBslProxyModule(IFile bslFile)
    {
        EObject module = EcoreUtil.create(MODULE);
        URI uri = URI.createPlatformResourceURI(bslFile.getFullPath().toString(), true).appendFragment("/0"); //$NON-NLS-1$
        ((InternalEObject)module).eSetProxyURI(uri);
        return module;
    }

    /**
     * Gets the module file.
     *
     * @param module the model, cannot be {@code null}.
     * @return the module file or {@code null} if module URI is not platform URI.
     */
    protected IFile getModuleFile(Module module)
    {
        URI uri = EcoreUtil.getURI(module);

        if (uri != null && uri.isPlatform())
        {
            IPath path = new Path(uri.trimFragment().toPlatformString(true));
            return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        }
        return null;
    }

    /**
     * Gets the module file of form.
     *
     * @param form the form, cannot be {@code null}.
     * @param project the project, cannot be {@code null}.
     * @return the module file
     */
    protected IFile getModuleFile(AbstractForm form, IProject project)
    {
        String fqn = ((IBmObject)form).bmGetFqn();

        QualifiedName name = QualifiedName.create(fqn).append(StringUtils.capitalize(ABSTRACT_FORM__MODULE.getName()));

        IPath path = qualifiedNameFilePathConverter.getFilePath(name, MODULE);

        return project.getFile(path);
    }

    /**
     * Creates a parent directories of file without exceptions.
     *
     * @param bslFile the BSL file, cannot be {@code null}.
     */
    protected void createParentFolders(IFile bslFile)
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
