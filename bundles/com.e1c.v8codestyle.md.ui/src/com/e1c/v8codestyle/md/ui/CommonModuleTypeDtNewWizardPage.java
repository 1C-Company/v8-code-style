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
package com.e1c.v8codestyle.md.ui;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.typed.ViewerProperties;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com._1c.g5.v8.dt.md.ui.shared.MdUiSharedImages;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.ui.wizards.DtNewWizardPage;
import com._1c.g5.v8.dt.ui.wizards.IDtNewWizardPage;
import com.e1c.v8codestyle.md.CommonModuleTypes;

/**
 * DT "new Wizard" page that contribute to extension, allows to select {@link CommonModule common module} type and
 * set EMF features according to the type, also adds type suffix to the base name.
 * This page also detects type by user's input name of module with suffix and then sets EMF features.
 * Page make modification only if user switched next to the page.
 *
 * @author Dmitriy Marmyshev
 */
public class CommonModuleTypeDtNewWizardPage
    extends DtNewWizardPage<CommonModule>
    implements IDtNewWizardPage<CommonModule>, IPageChangedListener
{

    private ListViewer moduleTypeViewer;

    private String currentSuffix = ""; //$NON-NLS-1$

    public CommonModuleTypeDtNewWizardPage()
    {
        super("CommonModuleType"); //$NON-NLS-1$
        setTitle(Messages.CommonModuleTypeDtNewWizardPage_Select_common_module_type);
        setDescription(Messages.CommonModuleTypeDtNewWizardPage_Choose_valid_common_module_type_from_list);
        setImageDescriptor(MdUiSharedImages.getImageDescriptor(MdUiSharedImages.IMG_NEW_COMMON_MODULE));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void createPageControls(Composite container)
    {

        Composite composite = new Composite(container, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(composite);

        Label label = new Label(composite, SWT.WRAP);
        String title = com._1c.g5.v8.dt.common.Functions.featureToLabel().apply(MD_OBJECT__NAME);
        label.setText(title);
        Text newName = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(newName);

        Label message = new Label(composite, SWT.WRAP);
        message.setText(Messages.CommonModuleTypeDtNewWizardPage_Select_type);
        moduleTypeViewer = new ListViewer(composite, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

        moduleTypeViewer.setContentProvider(ArrayContentProvider.getInstance());
        moduleTypeViewer.setLabelProvider(new CommonModuleTypesLableProvider());
        moduleTypeViewer.setInput(CommonModuleTypes.values());
        moduleTypeViewer.addSelectionChangedListener(e -> validate());
        GridDataFactory.fillDefaults().grab(true, true).applyTo(moduleTypeViewer.getControl());

        IObservableValue<String> nameValue = EMFProperties.value(MD_OBJECT__NAME).observe(getContext().getModel());

        IViewerObservableValue<CommonModuleTypes> selection =
            ViewerProperties.singlePostSelection(CommonModuleTypes.class).observe(moduleTypeViewer);

        UpdateValueStrategy<CommonModuleTypes, String> covert =
            new UpdateValueStrategy<>(UpdateValueStrategy.POLICY_UPDATE);
        covert.setConverter(IConverter.create(this::convertTypeToName));
        getDataBindingContext().bindValue(nameValue, selection, UpdateValueStrategy.never(), covert);

        getDataBindingContext().bindValue(WidgetProperties.text().observe(newName), nameValue,
            UpdateValueStrategy.never(), null);

        if (getWizard().getContainer() instanceof IPageChangeProvider)
        {
            ((IPageChangeProvider)getWizard().getContainer()).addPageChangedListener(this);
        }

        validate();

    }

    @Override
    public void dispose()
    {
        if (getWizard().getContainer() instanceof IPageChangeProvider)
        {
            ((IPageChangeProvider)getWizard().getContainer()).removePageChangedListener(this);
        }
        super.dispose();
    }

    @Override
    public void finish(IProgressMonitor monitor)
    {
        updateModuleType(getSelection());
    }

    @Override
    public void pageChanged(PageChangedEvent event)
    {
        if (event.getSelectedPage() == this)
        {
            onPageChanged();
        }
    }

    private void onPageChanged()
    {
        CommonModule model = getContext().getModel();
        String name = model.getName();
        ScriptVariant script = getContext().getV8project().getScriptVariant();
        CommonModuleTypes type = getClosestTypeByName(name, script);

        currentSuffix = type.getNameSuffix(script);
        moduleTypeViewer.setSelection(new StructuredSelection(type), true);
        moduleTypeViewer.getControl().setFocus();

    }

    private void validate()
    {
        CommonModuleTypes type = getSelection();
        if (type != null)
        {
            setErrorMessage(null);
            setPageComplete(true);
        }
        else
        {
            setErrorMessage(Messages.CommonModuleTypeDtNewWizardPage_Select_type_from_list);
            setPageComplete(false);
        }
    }

    private void updateModuleType(CommonModuleTypes type)
    {
        if (type == null)
        {
            return;
        }

        CommonModule module = getContext().getModel();
        for (Entry<EStructuralFeature, Object> entry : type.getFeatureValues().entrySet())
        {
            module.eSet(entry.getKey(), entry.getValue());
        }

        String name = convertTypeToName(type);
        module.setName(name);
    }

    private String convertTypeToName(CommonModuleTypes type)
    {
        CommonModule module = getContext().getModel();
        ScriptVariant scriptVariant = getContext().getV8project().getScriptVariant();
        String name = module.getName();
        if (currentSuffix != null && !currentSuffix.isEmpty() && name.endsWith(currentSuffix))
        {
            name = name.substring(0, name.length() - currentSuffix.length());
        }
        String suffix = ""; //$NON-NLS-1$
        if (type != null)
        {
            suffix = type.getNameSuffix(scriptVariant);
        }
        currentSuffix = suffix;
        return name + suffix;
    }

    private CommonModuleTypes getSelection()
    {
        ISelection selection = moduleTypeViewer.getSelection();
        if (selection instanceof IStructuredSelection && !selection.isEmpty())
        {
            return (CommonModuleTypes)((IStructuredSelection)selection).getFirstElement();
        }
        return null;
    }

    private CommonModuleTypes getClosestTypeByName(String name, ScriptVariant script)
    {
        List<CommonModuleTypes> result = new ArrayList<>();
        for (int i = 0; i < CommonModuleTypes.values().length; i++)
        {
            CommonModuleTypes type = CommonModuleTypes.values()[i];
            String suffix = type.getNameSuffix(script);
            if (!suffix.isEmpty() && name.endsWith(suffix))
            {
                result.add(type);
            }
        }
        if (result.isEmpty())
        {
            result.add(CommonModuleTypes.SERVER);
        }
        if (result.size() > 1)
        {
            Collections.sort(result, (o1, o2) -> o2.getNameSuffix(script).length() - o1.getNameSuffix(script).length());
        }
        return result.get(0);
    }

    private final class CommonModuleTypesLableProvider
        extends LabelProvider
    {

        @Override
        public String getText(Object element)
        {
            if (element instanceof CommonModuleTypes)
            {
                CommonModuleTypes type = (CommonModuleTypes)element;
                ScriptVariant script = getContext().getV8project().getScriptVariant();
                String suffix = type.getNameSuffix(script);
                if (suffix.isEmpty())
                {
                    return type.getTitle();
                }
                return type.getTitle() + " (" + suffix + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            return super.getText(element);
        }

    }

}
