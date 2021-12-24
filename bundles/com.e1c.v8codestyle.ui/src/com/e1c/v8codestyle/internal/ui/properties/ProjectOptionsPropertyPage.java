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
package com.e1c.v8codestyle.internal.ui.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import com._1c.g5.v8.dt.common.StringUtils;
import com.e1c.v8codestyle.IProjectOptionManager;
import com.e1c.v8codestyle.ProjectOption;
import com.google.inject.Inject;

/**
 * The property page of project functional option settings.
 *
 * @author Dmitriy Marmyshev
 */
public class ProjectOptionsPropertyPage
    extends PropertyPage
{

    private final IProjectOptionManager projectOptionManager;

    private Map<ProjectOption, Button> options = new HashMap<>();

    /**
     * Instantiates a new project options property page.
     *
     * @param projectOptionManager the project option manager service, cannot be {@code null}.
     */
    @Inject
    public ProjectOptionsPropertyPage(IProjectOptionManager projectOptionManager)
    {
        super();
        this.projectOptionManager = projectOptionManager;
    }

    /**
     * @see PreferencePage#createContents(Composite)
     */
    @Override
    protected Control createContents(Composite parent)
    {

        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(composite);

        IProject project = getProject();

        for (ProjectOption option : projectOptionManager.getAvailableOptions())
        {
            boolean value = projectOptionManager.getOption(project, option);
            Button button = new Button(composite, SWT.CHECK);
            button.setSelection(value);
            button.setText(option.getPresentation());

            String description = option.getDescription();
            if (StringUtils.isNotEmpty(description))
            {
                button.setToolTipText(description);
                Label label = new Label(composite, SWT.WRAP | SWT.SHADOW_OUT);
                label.setText(description);
                label.setToolTipText(description);
            }

            options.put(option, button);
        }

        return composite;
    }

    @Override
    public void dispose()
    {
        options.clear();
        super.dispose();
    }

    @Override
    protected void performDefaults()
    {
        super.performDefaults();
        for (Entry<ProjectOption, Button> entry : options.entrySet())
        {
            entry.getValue().setSelection(entry.getKey().getDefaultValue());
        }
    }

    @Override
    public boolean performOk()
    {

        IProject project = getProject();
        for (Entry<ProjectOption, Button> entry : options.entrySet())
        {
            projectOptionManager.save(project, entry.getKey(), entry.getValue().getSelection(),
                new NullProgressMonitor());
        }

        return true;
    }

    private IProject getProject()
    {
        return (IProject)getElement();
    }

}
