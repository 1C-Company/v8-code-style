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
package com.e1c.v8codestyle.bsl.ui.itests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com._1c.g5.v8.dt.testing.TestingWorkspace;
import com.e1c.v8codestyle.internal.bsl.ui.preferences.ModuleStructurePreferencePage;

/**
 * Integration test of the {@link ModuleStructurePreferencePage}.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructurePreferencePageTest
{
    private static final String PROJECT_NAME = "CommonModule";

    private static final String PREFERENCE_PAGE_ID =
        "com.e1c.v8codestyle.bsl.ui.preferences.ModuleStructurePreferencePage";

    @Rule
    public TestingWorkspace testingWorkspace = new TestingWorkspace(true, false);

    protected static final String FOLDER_RESOURCE = "/resources/";

    private IProject project;

    @Before
    public void setUp() throws CoreException
    {

        project = testingWorkspace.getProject(PROJECT_NAME);

        if (!project.exists() || !project.isAccessible())
        {
            testingWorkspace.cleanUpWorkspace();
            project = this.testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        }
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);

        IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
        PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
    }

    @After
    public void shutDown()
    {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
    }

    /**
     * Test open module structure preference page.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOpenModuleStructurePreferencePage() throws Exception
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        PreferenceDialog dialog =
            PreferencesUtil.createPreferenceDialogOn(shell,
            PREFERENCE_PAGE_ID, null, null);

        dialog.setBlockOnOpen(false);
        shell.getDisplay().syncExec(dialog::open);
        shell.getDisplay().syncExec(dialog::close);
    }

}
