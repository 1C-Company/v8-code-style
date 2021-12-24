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
package com.e1c.v8codestyle.internal.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.ui.wizards.DtNewWizardPage;
import com.e1c.v8codestyle.IProjectOptionManager;
import com.e1c.v8codestyle.ProjectOption;
import com.google.inject.Inject;

/**
 * The project functional options wizard page for new project.
 *
 * @author Dmitriy Marmyshev
 */
public class ProjectOptionsDtNewWizardPage
    extends DtNewWizardPage<EObject>
{

    private final IProjectOptionManager projectOptionManager;

    private Map<ProjectOption, Button> options = new HashMap<>();

    /**
     * Instantiates a new project options DT new wizard page.
     *
     * @param projectOptionManager the project option manager service, cannot be {@code null}.
     */
    @Inject
    public ProjectOptionsDtNewWizardPage(IProjectOptionManager projectOptionManager)
    {
        super("ProjectOptions"); //$NON-NLS-1$

        this.projectOptionManager = projectOptionManager;
        setDescription(Messages.ProjectOptionsDtNewWizardPage_description);
        setTitle(Messages.ProjectOptionsDtNewWizardPage_title);
    }

    @Override
    public void createPageControls(Composite container)
    {

        Composite composite = new Composite(container, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(composite);

        for (ProjectOption option : projectOptionManager.getAvailableOptions())
        {
            Button button = new Button(composite, SWT.CHECK);
            button.setSelection(option.getDefaultValue());
            button.setText(option.getPresentation());

            String description = option.getDescription();
            if (StringUtils.isNotEmpty(description))
            {
                button.setToolTipText(description);
                Label label = new Label(composite, SWT.WRAP | SWT.SHADOW_IN);
                label.setText(description);
                label.setToolTipText(description);
            }

            options.put(option, button);
        }

    }

    @Override
    public void dispose()
    {
        options.clear();
        super.dispose();
    }

    @Override
    public void finish(IProgressMonitor monitor)
    {
        IProject project = getContext().getV8project().getProject();
        for (Entry<ProjectOption, Button> entry : options.entrySet())
        {
            if (monitor.isCanceled())
            {
                return;
            }
            projectOptionManager.save(project, entry.getKey(), entry.getValue().getSelection(), monitor);
        }
    }

}
