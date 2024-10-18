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
package com.e1c.v8codestyle.internal.bsl.ui.properties;

import static com.e1c.v8codestyle.bsl.strict.StrictTypeUtil.BSL_FILE_EXTENSION;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.common.FileUtil;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.ui.util.OpenHelper;
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;
import com.google.inject.Inject;

/**
 * The property page of module structure settings.
 * Allows to disable auto-creating module structure or manage custom project templates.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructurePropertyPage
    extends PropertyPage
{
    private static final IPath FOLDER_SETTINGS = new Path(".settings/templates"); //$NON-NLS-1$

    private final IModuleStructureProvider moduleStructureProvider;

    private final IV8ProjectManager v8ProjectManager;

    private Button createStructureButton;

    private Button createStrictTypesButton;

    private Button openButton;

    private CheckboxTableViewer checkBoxViewer;

    private final Set<ModuleType> existTemplates = new HashSet<>();

    private boolean currentCreateStructure;

    private boolean currentCreateStrictTypes;

    private final OpenHelper openHelper = new OpenHelper();

    /**
     * Constructor for module structure Property Page.
     *
     * @param moduleStructureProvider the module structure provider, cannot be {@code null}.
     * @param v8ProjectManager the v8 project manager, cannot be {@code null}.
     */
    @Inject
    public ModuleStructurePropertyPage(IModuleStructureProvider moduleStructureProvider,
        IV8ProjectManager v8ProjectManager)
    {
        super();
        this.moduleStructureProvider = moduleStructureProvider;
        this.v8ProjectManager = v8ProjectManager;
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        composite.setLayoutData(data);

        addFirstSection(composite);
        addSeparator(composite);
        addSecondSection(composite);

        return composite;
    }

    private Composite createDefaultComposite(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(data);

        return composite;
    }

    @Override
    protected void performDefaults()
    {
        super.performDefaults();
        createStructureButton.setSelection(IModuleStructureProvider.PREF_DEFAULT_CREATE_STRUCTURE);
        existTemplates.forEach(type -> checkBoxViewer.setChecked(type, true));
    }

    @Override
    public boolean performOk()
    {
        ProjectScope scope = new ProjectScope(getProject());
        IEclipsePreferences node = scope.getNode(IModuleStructureProvider.PREF_QUALIFIER);
        boolean value1 = createStructureButton.getSelection();
        boolean value2 = createStrictTypesButton.getSelection();
        node.putBoolean(IModuleStructureProvider.PREF_KEY_CREATE_STRUCTURE, value1);
        node.putBoolean(StrictTypeUtil.PREF_KEY_CREATE_STRICT_TYPES, value2);
        try
        {
            node.flush();
        }
        catch (BackingStoreException e)
        {
            UiPlugin.logError(e);
            return false;
        }
        currentCreateStructure = value1;
        currentCreateStrictTypes = value2;
        for (ModuleType type : ModuleType.VALUES)
        {
            if (checkBoxViewer.getChecked(type) && !existTemplates.contains(type))
            {
                saveTemplate(type);
            }
            else if (existTemplates.contains(type) && !checkBoxViewer.getChecked(type))
            {
                removeTemplate(type);
            }
        }
        loadExistTemplate();
        return true;
    }

    private void saveTemplate(ModuleType type)
    {
        IFile file = getFileByType(type);
        if (file.isAccessible())
        {
            return;
        }
        IV8Project v8Project = v8ProjectManager.getProject(getProject());
        if (v8Project == null)
        {
            return;
        }
        ScriptVariant script = v8Project.getScriptVariant();
        Supplier<InputStream> content = moduleStructureProvider.getModuleStructureTemplate(getProject(), type, script);
        if (content == null)
        {
            return;
        }
        try
        {
            FileUtil.createParentFolders(file);
        }
        catch (CoreException e)
        {
            UiPlugin.logError(e);
            return;
        }

        try (InputStream in = content.get())
        {
            file.create(in, true, new NullProgressMonitor());
        }
        catch (CoreException | IOException e)
        {
            UiPlugin.logError(e);
        }

    }

    private void removeTemplate(ModuleType type)
    {
        IFile file = getFileByType(type);
        if (!file.isAccessible())
        {
            return;
        }
        try
        {
            file.delete(true, true, new NullProgressMonitor());
        }
        catch (CoreException e)
        {
            UiPlugin.logError(e);
        }
    }

    private void addFirstSection(Composite parent)
    {
        Composite composite = createDefaultComposite(parent);

        Label createStrictTypesLabel = new Label(composite, SWT.NONE);
        createStrictTypesLabel.setText(Messages.ModuleStructurePropertyPage_Automatically_create_strict_types_module);

        currentCreateStrictTypes = StrictTypeUtil.canAddModuleStrictTypesAnnotation(getProject());

        createStrictTypesButton = new Button(composite, SWT.CHECK);
        createStrictTypesButton.setSelection(currentCreateStrictTypes);

        Label createStructureLabel = new Label(composite, SWT.NONE);
        createStructureLabel.setText(Messages.ModuleStructurePropertyPage_Automatically_create_module_structure);

        currentCreateStructure = moduleStructureProvider.canCreateStructure(getProject());

        createStructureButton = new Button(composite, SWT.CHECK);
        createStructureButton.setSelection(currentCreateStructure);
    }

    private void addSeparator(Composite parent)
    {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }

    private void addSecondSection(Composite parent)
    {
        Composite composite = createDefaultComposite(parent);

        Label customTemplatesLabel = new Label(composite, SWT.NONE | SWT.WRAP);
        customTemplatesLabel
            .setText(Messages.ModuleStructurePropertyPage_Select_module_type_to_create_custom_structure_templates);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        customTemplatesLabel.setLayoutData(data);

        checkBoxViewer = CheckboxTableViewer.newCheckList(composite,
            SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
        checkBoxViewer.setContentProvider(ArrayContentProvider.getInstance());
        checkBoxViewer.setLabelProvider(LabelProvider.createTextProvider(type -> {
            if (type instanceof ModuleType)
            {
                String label = StringUtils.nameToText(((ModuleType)type).getName()).toLowerCase();
                return StringUtils.capitalize(label);
            }
            return type == null ? "" : type.toString(); //$NON-NLS-1$
        }));

        checkBoxViewer.setInput(ModuleType.VALUES);

        Composite buttonPanel = createDefaultComposite(composite);
        openButton = new Button(buttonPanel, SWT.PUSH);
        openButton.setText(Messages.ModuleStructurePropertyPage_Open_template);
        openButton.setToolTipText(Messages.ModuleStructurePropertyPage_Open_template_tooltip);
        openButton.setEnabled(false);
        openButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
            IStructuredSelection selection = checkBoxViewer.getStructuredSelection();
            if (!selection.isEmpty())
            {
                ModuleType type = (ModuleType)selection.getFirstElement();
                saveSettingsAndOpenTemplateAndClose(type);
            }
        }));
        checkBoxViewer.addPostSelectionChangedListener(e -> {
            boolean enable = !e.getStructuredSelection().isEmpty()
                && checkBoxViewer.getChecked(e.getStructuredSelection().getFirstElement());
            openButton.setEnabled(enable);
        });

        loadExistTemplate();

    }

    private IProject getProject()
    {
        return (IProject)getElement();
    }

    private void loadExistTemplate()
    {
        existTemplates.clear();

        for (ModuleType type : ModuleType.VALUES)
        {
            IFile file = getFileByType(type);
            if (file.isAccessible())
            {
                existTemplates.add(type);
            }
        }

        checkBoxViewer.setCheckedElements(new ModuleType[0]);
        existTemplates.forEach(type -> checkBoxViewer.setChecked(type, true));
    }

    private IFile getFileByType(ModuleType type)
    {
        return getProject()
            .getFile(FOLDER_SETTINGS.append(type.getName().toLowerCase()).addFileExtension(BSL_FILE_EXTENSION));
    }

    private boolean isDirty()
    {
        if (createStructureButton.getSelection() != currentCreateStructure
            || createStrictTypesButton.getSelection() != currentCreateStrictTypes)
        {
            return true;
        }
        Set<Object> checked = Set.of(checkBoxViewer.getCheckedElements());
        return !existTemplates.equals(checked);
    }

    private void saveSettingsAndOpenTemplateAndClose(ModuleType type)
    {
        if (isDirty())
        {
            if (!MessageDialog.openQuestion(getShell(), Messages.ModuleStructurePropertyPage_Save_settings,
                Messages.ModuleStructurePropertyPage_Save_custom_template_to_project_settings))
            {
                return;
            }
            if (!performOk())
            {
                return;
            }
        }
        getShell().getDisplay().asyncExec(() -> {
            IFile file = getFileByType(type);
            if (file.isAccessible())
            {
                openHelper.openEditor(file, null);
            }
        });
        if (getContainer() instanceof PreferenceDialog)
        {
            ((PreferenceDialog)getContainer()).close();
        }
    }
}
