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
package com.e1c.v8codestyle.autosort.ui.properties;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.SUBSYSTEM__SUBSYSTEMS;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com._1c.g5.v8.dt.common.Functions;
import com.e1c.v8codestyle.autosort.AutoSortPreferences;
import com.e1c.v8codestyle.autosort.ISortService;
import com.e1c.v8codestyle.autosort.ListConstants;
import com.e1c.v8codestyle.internal.autosort.ui.UiPlugin;
import com.google.inject.Inject;

public class AutoSortPropertyPage
    extends PropertyPage
{

    private final ISortService sortService;

    private FormToolkit toolkit;

    private IEclipsePreferences prefs;

    private Map<String, Button> buttons = new HashMap<>();

    private Map<String, Boolean> topObjects = new HashMap<>();

    private Button ascendingButton;

    private Button sortOrderButton;

    @Inject
    public AutoSortPropertyPage(ISortService sortService)
    {
        super();
        this.sortService = sortService;
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents(Composite parent)
    {

        toolkit = new FormToolkit(parent.getDisplay());

        prefs = AutoSortPreferences.getPreferences(getProject());

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        composite.setLayoutData(data);

        addSortSection(composite);
        addSortOrderSection(composite);
        addSeparator(composite);
        addTopObjectsSection(composite);
        addSubordinateSection(composite);
        this.setMessage(Messages.AutoSortPropertyPage_Automatically_sort_medata_objects_on_edit);
        return composite;
    }

    @Override
    public void dispose()
    {
        toolkit.dispose();
        super.dispose();
    }

    @Override
    protected void performDefaults()
    {
        super.performDefaults();
        ascendingButton.setSelection(AutoSortPreferences.DEFAULT_SORT_ASCENDING);
        sortOrderButton.setSelection(AutoSortPreferences.DEFAULT_SORT_ORDER);
        buttons.get(AutoSortPreferences.KEY_ALL_TOP).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_SUBORDINATE_OBJECTS).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_FORMS).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_TEMPLATES).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_COMMANDS).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_OPERATIONS).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_URL_TEMPLATES).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_METHODS).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_ATTRIBUTES).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_REGISTRY_RESOURCES).setSelection(AutoSortPreferences.DEFAULT_SORT);
        buttons.get(AutoSortPreferences.KEY_TABULAR_SECTIONS).setSelection(AutoSortPreferences.DEFAULT_SORT);
    }

    @Override
    public boolean performOk()
    {
        boolean needToSort = needToSort();

        boolean ascending = ascendingButton.getSelection();
        if (ascending == AutoSortPreferences.DEFAULT_SORT_ASCENDING)
        {
            prefs.remove(AutoSortPreferences.KEY_ASCENDING);
        }
        else
        {
            prefs.putBoolean(AutoSortPreferences.KEY_ASCENDING, ascending);
        }

        boolean sortOrder = sortOrderButton.getSelection();
        if (sortOrder == AutoSortPreferences.DEFAULT_SORT_ORDER)
        {
            prefs.remove(AutoSortPreferences.KEY_SORT_ORDER);
        }
        else
        {
            prefs.putBoolean(AutoSortPreferences.KEY_SORT_ORDER, sortOrder);
        }

        updateSortPreferences(prefs, AutoSortPreferences.KEY_ALL_TOP,
            buttons.get(AutoSortPreferences.KEY_ALL_TOP).getSelection());
        if (!buttons.get(AutoSortPreferences.KEY_ALL_TOP).getSelection())
        {
            Preferences node = prefs.node(AutoSortPreferences.KEY_TOP_NODE);
            for (Entry<String, Boolean> entry : topObjects.entrySet())
            {
                updateSortPreferences(node, entry.getKey(), entry.getValue());
            }
        }
        updateSortPreferences(prefs, AutoSortPreferences.KEY_SUBORDINATE_OBJECTS,
            buttons.get(AutoSortPreferences.KEY_SUBORDINATE_OBJECTS).getSelection());
        if (!buttons.get(AutoSortPreferences.KEY_SUBORDINATE_OBJECTS).getSelection())
        {
            Preferences node = prefs.node(AutoSortPreferences.KEY_SUBORDINATE_NODE);
            updateSortPreferences(node, AutoSortPreferences.KEY_FORMS,
                buttons.get(AutoSortPreferences.KEY_FORMS).getSelection());
            updateSortPreferences(node, AutoSortPreferences.KEY_TEMPLATES,
                buttons.get(AutoSortPreferences.KEY_TEMPLATES).getSelection());
            updateSortPreferences(node, AutoSortPreferences.KEY_COMMANDS,
                buttons.get(AutoSortPreferences.KEY_COMMANDS).getSelection());
            updateSortPreferences(node, AutoSortPreferences.KEY_OPERATIONS,
                buttons.get(AutoSortPreferences.KEY_OPERATIONS).getSelection());
            updateSortPreferences(node, AutoSortPreferences.KEY_URL_TEMPLATES,
                buttons.get(AutoSortPreferences.KEY_URL_TEMPLATES).getSelection());
            updateSortPreferences(node, AutoSortPreferences.KEY_METHODS,
                buttons.get(AutoSortPreferences.KEY_METHODS).getSelection());
            updateSortPreferences(node, AutoSortPreferences.KEY_ATTRIBUTES,
                buttons.get(AutoSortPreferences.KEY_ATTRIBUTES).getSelection());
            updateSortPreferences(node, AutoSortPreferences.KEY_REGISTRY_RESOURCES,
                buttons.get(AutoSortPreferences.KEY_REGISTRY_RESOURCES).getSelection());
            updateSortPreferences(node, AutoSortPreferences.KEY_TABULAR_SECTIONS,
                buttons.get(AutoSortPreferences.KEY_TABULAR_SECTIONS).getSelection());
        }

        try
        {
            if (buttons.get(AutoSortPreferences.KEY_ALL_TOP).getSelection())
            {
                prefs.remove(AutoSortPreferences.KEY_TOP_NODE);
                if (prefs.nodeExists(AutoSortPreferences.KEY_TOP_NODE))
                {
                    prefs.node(AutoSortPreferences.KEY_TOP_NODE).removeNode();
                }
            }
            if (buttons.get(AutoSortPreferences.KEY_SUBORDINATE_OBJECTS).getSelection())
            {
                prefs.remove(AutoSortPreferences.KEY_SUBORDINATE_NODE);
                if (prefs.nodeExists(AutoSortPreferences.KEY_SUBORDINATE_NODE))
                {
                    prefs.node(AutoSortPreferences.KEY_SUBORDINATE_NODE).removeNode();
                }
            }
            prefs.flush();
        }
        catch (BackingStoreException e)
        {
            UiPlugin.logError(e);
            return false;
        }

        if (needToSort && MessageDialog.openQuestion(getShell(), Messages.AutoSortPropertyPage_Sort_question_title,
            Messages.AutoSortPropertyPage_Sort_question))
        {
            sortService.startSortAllMetadata(getProject());
        }
        return true;
    }

    private boolean needToSort()
    {
        if (ascendingButton.getSelection() != prefs.getBoolean(AutoSortPreferences.KEY_ASCENDING,
            AutoSortPreferences.DEFAULT_SORT_ASCENDING)
            || sortOrderButton.getSelection() != prefs.getBoolean(AutoSortPreferences.KEY_SORT_ORDER,
                AutoSortPreferences.DEFAULT_SORT_ORDER)
            || !prefs.getBoolean(AutoSortPreferences.KEY_ALL_TOP, AutoSortPreferences.DEFAULT_SORT)
                && buttons.get(AutoSortPreferences.KEY_ALL_TOP).getSelection()
            || !prefs.getBoolean(AutoSortPreferences.KEY_SUBORDINATE_OBJECTS, AutoSortPreferences.DEFAULT_SORT)
                && buttons.get(AutoSortPreferences.KEY_SUBORDINATE_OBJECTS).getSelection())
        {
            return true;
        }

        return false;
    }

    private IProject getProject()
    {
        return (IProject)getElement();
    }

    private void updateSortPreferences(Preferences node, String key, boolean value)
    {
        if (value == AutoSortPreferences.DEFAULT_SORT)
        {
            node.remove(key);
        }
        else
        {
            node.putBoolean(key, value);
        }
    }

    private Composite createDefaultComposite(Composite parent)
    {
        Composite composite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(data);

        return composite;
    }

    private Button createCheckbox(Composite parent, String label, String key, boolean isCheck)
    {
        final Button but = new Button(parent, SWT.CHECK);
        but.setData(key);
        but.setSelection(isCheck);
        but.pack();
        buttons.put(key, but);
        Label buttonLabel = new Label(parent, SWT.NONE);
        buttonLabel.setText(label);
        buttonLabel.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseUp(MouseEvent e)
            {
                but.setSelection(!but.getSelection());
            }
        });
        buttonLabel.pack();
        return but;
    }

    private void addSortSection(Composite parent)
    {
        Group sortGroup = new Group(parent, SWT.NONE);
        sortGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
        toolkit.createLabel(sortGroup, Messages.AutoSortPropertyPage_Sort_direction);
        ascendingButton = toolkit.createButton(sortGroup, Messages.AutoSortPropertyPage_Ascending, SWT.RADIO);
        final Button descendingButton =
            toolkit.createButton(sortGroup, Messages.AutoSortPropertyPage_Descending, SWT.RADIO);
        descendingButton.setSelection(true);
        boolean ascending =
            prefs.getBoolean(AutoSortPreferences.KEY_ASCENDING, AutoSortPreferences.DEFAULT_SORT_ASCENDING);
        ascendingButton.setSelection(ascending);
        descendingButton.setSelection(!ascending);
    }

    private void addSortOrderSection(Composite parent)
    {
        Group sortGroup = new Group(parent, SWT.NONE);
        sortGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
        toolkit.createLabel(sortGroup, Messages.AutoSortPropertyPage_Sort_order);
        sortOrderButton = toolkit.createButton(sortGroup, Messages.AutoSortPropertyPage_Sort_order_natural, SWT.RADIO);
        final Button asDesignerSortOrderButton =
            toolkit.createButton(sortGroup, Messages.AutoSortPropertyPage_Sort_order_as_designer, SWT.RADIO);
        asDesignerSortOrderButton.setSelection(true);
        boolean naturalSortOrder =
            prefs.getBoolean(AutoSortPreferences.KEY_SORT_ORDER, AutoSortPreferences.DEFAULT_SORT_ORDER);
        sortOrderButton.setSelection(naturalSortOrder);
        asDesignerSortOrderButton.setSelection(!naturalSortOrder);
    }

    private void addSeparator(Composite parent)
    {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }

    private void addTopObjectsSection(Composite parent)
    {
        Composite composite = createDefaultComposite(parent);

        final boolean check = prefs.getBoolean(AutoSortPreferences.KEY_ALL_TOP, AutoSortPreferences.DEFAULT_SORT);
        Button topCheckbox = createCheckbox(composite, Messages.AutoSortPropertyPage_All_top_metadata_objects,
            AutoSortPreferences.KEY_ALL_TOP, check);

        Section section = toolkit.createSection(parent,
            Section.DESCRIPTION | ExpandableComposite.SHORT_TITLE_BAR | ExpandableComposite.TWISTIE
                | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.TREE_NODE | ExpandableComposite.COMPACT);

        section.setText(Messages.AutoSortPropertyPage_Select_top_objects);
        section.setDescription(Messages.AutoSortPropertyPage_Select_top_objects_description);
        final GridData collapsed = new GridData(SWT.FILL, SWT.FILL, true, false);
        final GridData expanded = new GridData(SWT.FILL, SWT.FILL, true, true);
        section.setLayoutData(collapsed);
        Composite content = toolkit.createComposite(section);
        GridLayoutFactory.fillDefaults().applyTo(content);
        section.setClient(content);

        CheckboxTableViewer viewer = CheckboxTableViewer.newCheckList(content,
            SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        section.addExpansionListener(new ExpansionAdapter()
        {
            @Override
            public void expansionStateChanged(ExpansionEvent e)
            {
                if (e.getState())
                {
                    section.setLayoutData(expanded);
                }
                else
                {
                    section.setLayoutData(collapsed);
                }
            }
        });
        Table table = viewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setLabelProvider(new FeatureLabelProvider());
        viewer.setCheckStateProvider(new TopCheckStateProvider());
        viewer.addCheckStateListener(event -> {
            if (event.getElement() instanceof EReference)
            {
                topObjects.put(((EReference)event.getElement()).getName(), event.getChecked());
            }
        });
        topCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                boolean check = topCheckbox.getSelection();
                for (Entry<String, Boolean> entry : topObjects.entrySet())
                {
                    entry.setValue(check);
                }
                viewer.refresh();
            }
        });

        Preferences node = prefs.node(AutoSortPreferences.KEY_TOP_NODE);
        ListConstants.TOP_OPBJECT_LISTS.forEach(
            e -> topObjects.put(e.getName(), node.getBoolean(e.getName(), check || AutoSortPreferences.DEFAULT_SORT)));
        viewer.setInput(ListConstants.TOP_OPBJECT_LISTS.stream()
            .filter(e -> !e.equals(SUBSYSTEM__SUBSYSTEMS))
            .collect(Collectors.toList()));

    }

    private void addSubordinateSection(Composite parent)
    {
        Composite composite = createDefaultComposite(parent);

        final boolean check =
            prefs.getBoolean(AutoSortPreferences.KEY_SUBORDINATE_OBJECTS, AutoSortPreferences.DEFAULT_SORT);
        final Button subordinateCheckbox = createCheckbox(composite,
            Messages.AutoSortPropertyPage_All_subordinate_objects, AutoSortPreferences.KEY_SUBORDINATE_OBJECTS, check);

        Section section = toolkit.createSection(parent,
            Section.DESCRIPTION | ExpandableComposite.SHORT_TITLE_BAR | ExpandableComposite.TWISTIE
                | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.TREE_NODE | ExpandableComposite.COMPACT);

        section.setText(Messages.AutoSortPropertyPage_Select_subordinate_objects);
        section.setDescription(Messages.AutoSortPropertyPage_Select_subordinate_objects_description);
        Composite subContent = createDefaultComposite(section);
        section.setClient(subContent);

        Preferences node = prefs.node(AutoSortPreferences.KEY_SUBORDINATE_NODE);

        createCheckbox(subContent, Messages.AutoSortPropertyPage_Forms_of_object, AutoSortPreferences.KEY_FORMS,
            node.getBoolean(AutoSortPreferences.KEY_FORMS, check || AutoSortPreferences.DEFAULT_SORT));

        createCheckbox(subContent, Messages.AutoSortPropertyPage_Templates_of_object, AutoSortPreferences.KEY_TEMPLATES,
            node.getBoolean(AutoSortPreferences.KEY_TEMPLATES, check || AutoSortPreferences.DEFAULT_SORT));

        createCheckbox(subContent, Messages.AutoSortPropertyPage_Commands_of_object, AutoSortPreferences.KEY_COMMANDS,
            node.getBoolean(AutoSortPreferences.KEY_COMMANDS, check || AutoSortPreferences.DEFAULT_SORT));

        createCheckbox(subContent, Messages.AutoSortPropertyPage_Operations_of_Web_service,
            AutoSortPreferences.KEY_OPERATIONS,
            node.getBoolean(AutoSortPreferences.KEY_OPERATIONS, check || AutoSortPreferences.DEFAULT_SORT));

        createCheckbox(subContent, Messages.AutoSortPropertyPage_URL_templates_of_HTTP_service,
            AutoSortPreferences.KEY_URL_TEMPLATES,
            node.getBoolean(AutoSortPreferences.KEY_URL_TEMPLATES, check || AutoSortPreferences.DEFAULT_SORT));

        createCheckbox(subContent, Messages.AutoSortPropertyPage_Methods_of_URL_template,
            AutoSortPreferences.KEY_METHODS,
            node.getBoolean(AutoSortPreferences.KEY_METHODS, check || AutoSortPreferences.DEFAULT_SORT));

        createCheckbox(subContent, Messages.AutoSortPropertyPage_Attributes_of_object,
            AutoSortPreferences.KEY_ATTRIBUTES,
            node.getBoolean(AutoSortPreferences.KEY_ATTRIBUTES, check || AutoSortPreferences.DEFAULT_SORT));

        createCheckbox(subContent, Messages.AutoSortPropertyPage_Tabular_sections_of_object,
            AutoSortPreferences.KEY_TABULAR_SECTIONS,
            node.getBoolean(AutoSortPreferences.KEY_TABULAR_SECTIONS, check || AutoSortPreferences.DEFAULT_SORT));

        createCheckbox(subContent, Messages.AutoSortPropertyPage_Resources_of_registry,
            AutoSortPreferences.KEY_REGISTRY_RESOURCES,
            node.getBoolean(AutoSortPreferences.KEY_REGISTRY_RESOURCES, check || AutoSortPreferences.DEFAULT_SORT));

        subordinateCheckbox.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                boolean check = subordinateCheckbox.getSelection();
                buttons.get(AutoSortPreferences.KEY_FORMS).setSelection(check);
                buttons.get(AutoSortPreferences.KEY_TEMPLATES).setSelection(check);
                buttons.get(AutoSortPreferences.KEY_COMMANDS).setSelection(check);
                buttons.get(AutoSortPreferences.KEY_OPERATIONS).setSelection(check);
                buttons.get(AutoSortPreferences.KEY_URL_TEMPLATES).setSelection(check);
                buttons.get(AutoSortPreferences.KEY_METHODS).setSelection(check);
                buttons.get(AutoSortPreferences.KEY_ATTRIBUTES).setSelection(check);
                buttons.get(AutoSortPreferences.KEY_REGISTRY_RESOURCES).setSelection(check);
                buttons.get(AutoSortPreferences.KEY_TABULAR_SECTIONS).setSelection(check);
            }
        });
    }

    private static final class FeatureLabelProvider
        extends BaseLabelProvider
        implements ILabelProvider
    {

        @Override
        public Image getImage(Object element)
        {
            return null;
        }

        @Override
        public String getText(Object element)
        {
            if (element instanceof EReference)
            {
                String label = Functions.featureToLabel().apply((EReference)element);
                if (label == null)
                {
                    label = ((EReference)element).getName();
                }
                return label;
            }
            return null;
        }
    }

    private final class TopCheckStateProvider
        implements ICheckStateProvider
    {

        @Override
        public boolean isChecked(Object element)
        {
            if (element instanceof EReference)
            {
                return AutoSortPropertyPage.this.topObjects.getOrDefault(((EReference)element).getName(), false);
            }
            return false;
        }

        @Override
        public boolean isGrayed(Object element)
        {
            return false;
        }
    }

}
