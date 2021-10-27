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
/**
 *
 */
package com.e1c.v8codestyle.bsl.ui.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.testing.TestingWorkspace;
import com._1c.g5.wiring.ServiceAccess;
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.internal.bsl.ui.properties.ModuleStructurePropertyPage;
import com.google.common.io.CharStreams;

/**
 * Integration test of the {@link ModuleStructurePropertyPage}.
 *
 * @author Dmitriy Marmyshev
 */
public class ModuleStructurePropertyPageTest
{
    private static final String PROJECT_NAME = "CommonModule";

    private static final String SETTINGS_TEMPLATES_COMMON_MODULE_BSL = ".settings/templates/common_module.bsl";

    private static final String EDTOR_TITLE = "/" + PROJECT_NAME + "/" + SETTINGS_TEMPLATES_COMMON_MODULE_BSL;

    private static final String PROPERTY_PAGE_ID =
        "com.e1c.v8codestyle.bsl.ui.properties.moduleStructurePropertyPage";

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
     * Test open and close module structure property page.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOpenAndCloseModuleStructurePropertyPage() throws Exception
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(shell, project, PROPERTY_PAGE_ID, null, null);

        dialog.setBlockOnOpen(false);
        shell.getDisplay().syncExec(dialog::open);
        waitEventSetnd(dialog);
        Object selected = dialog.getSelectedPage();
        assertTrue(selected instanceof ModuleStructurePropertyPage);
        shell.getDisplay().syncExec(dialog::close);
    }

    /**
     * Test open module structure property page.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOpenModuleTemplate() throws Exception
    {

        IModuleStructureProvider moduleStructureProvider = ServiceAccess.get(IModuleStructureProvider.class);
        Supplier<InputStream> templateProvider = moduleStructureProvider.getModuleStructureTemplate(project,
            ModuleType.COMMON_MODULE, ScriptVariant.ENGLISH);

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(shell, project, PROPERTY_PAGE_ID, null, null);

        dialog.setBlockOnOpen(false);
        shell.getDisplay().syncExec(dialog::open);
        Object selected = dialog.getSelectedPage();
        assertTrue(selected instanceof ModuleStructurePropertyPage);
        ModuleStructurePropertyPage page = (ModuleStructurePropertyPage)selected;

        CheckboxTableViewer viewer = getViewer(page);
        assertFalse(viewer.getChecked(ModuleType.COMMON_MODULE));
        viewer.setChecked(ModuleType.COMMON_MODULE, true);

        Button applyButton = getApplyButtonControl(page);
        applyButton.notifyListeners(SWT.Selection, new Event());
        waitEventSetnd(dialog);

        viewer.setSelection(new StructuredSelection(ModuleType.COMMON_MODULE));
        waitEventSetnd(dialog);

        Button buttonOpen = getButtonByName(page, "Open template");
        assertNotNull(buttonOpen);
        buttonOpen.notifyListeners(SWT.Selection, new Event());
        assertNull(dialog.getShell());
        waitEventSetnd();

        // check editor content equals base template
        IFile file = project.getFile(SETTINGS_TEMPLATES_COMMON_MODULE_BSL);
        try (InputStream in = file.getContents();
            Reader fileReader = new InputStreamReader(in, StandardCharsets.UTF_8);
            InputStream template = templateProvider.get();
            Reader templateReader = new InputStreamReader(template, StandardCharsets.UTF_8);)
        {
            String templateText = CharStreams.toString(templateReader);
            assertNotNull(templateText);
            String fileText = CharStreams.toString(fileReader);
            assertEquals(templateText, fileText);
        }

        // wait until open editor
        waitEventSetnd();
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        assertNotNull(editor);
        assertEquals(EDTOR_TITLE, editor.getTitle());

        // re-open properties to check selection
        dialog = PreferencesUtil.createPropertyDialogOn(shell, project, PROPERTY_PAGE_ID, null, null);

        dialog.setBlockOnOpen(false);
        shell.getDisplay().syncExec(dialog::open);
        selected = dialog.getSelectedPage();
        assertTrue(selected instanceof ModuleStructurePropertyPage);
        page = (ModuleStructurePropertyPage)selected;

        viewer = getViewer(page);
        assertTrue(viewer.getChecked(ModuleType.COMMON_MODULE));

        shell.getDisplay().syncExec(dialog::close);

    }

    private CheckboxTableViewer getViewer(ModuleStructurePropertyPage page) throws Exception
    {
        Field field = page.getClass().getDeclaredField("checkBoxViewer");
        field.setAccessible(true);
        return (CheckboxTableViewer)field.get(page);
    }

    private Button getButtonByName(ModuleStructurePropertyPage page, String name) throws Exception
    {

        Composite top = getTopControl(page);
        assertNotNull(top);
        Queue<Control> toCheck = new LinkedList<>();
        toCheck.add(top);

        while (!toCheck.isEmpty())
        {
            Control control = toCheck.poll();
            if (control instanceof Button && name.equalsIgnoreCase(((Button)control).getText()))
            {
                return (Button)control;
            }
            else if (control instanceof Composite)
            {
                toCheck.addAll(List.of(((Composite)control).getChildren()));
            }
        }

        return null;
    }

    private Composite getTopControl(ModuleStructurePropertyPage page) throws Exception
    {
        Field field = DialogPage.class.getDeclaredField("control");
        field.setAccessible(true);
        return (Composite)field.get(page);
    }

    private Button getApplyButtonControl(ModuleStructurePropertyPage page) throws Exception
    {
        Field field = PreferencePage.class.getDeclaredField("applyButton");
        field.setAccessible(true);
        return (Button)field.get(page);
    }

    private void waitEventSetnd(PreferenceDialog dialog)
    {
        Display display = dialog.getShell().getDisplay();
        while (!display.isDisposed() && display.readAndDispatch())
        {
            // do it
        }
    }

    private void waitEventSetnd()
    {

        Display display = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay();
        while (!display.isDisposed() && display.readAndDispatch())
        {
            // do it
        }
    }

}
