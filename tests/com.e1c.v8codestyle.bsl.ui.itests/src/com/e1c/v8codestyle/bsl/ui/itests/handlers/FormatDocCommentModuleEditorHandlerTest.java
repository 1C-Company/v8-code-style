/*******************************************************************************
 * Copyright (C) 2024, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.ui.itests.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com._1c.g5.v8.dt.bsl.ui.editor.BslXtextEditor;
import com._1c.g5.v8.dt.testing.TestingWorkspace;
import com._1c.g5.v8.dt.ui.util.OpenHelper;
import com.e1c.v8codestyle.internal.bsl.ui.handlers.FormatDocCommentModuleEditorHandler;

/**
 * Test {@link FormatDocCommentModuleEditorHandler}
 *
 * @author Dmitriy Marmyshev
 */
public class FormatDocCommentModuleEditorHandlerTest
{

    private static final String COMMAND_ID = "com.e1c.v8codestyle.bsl.ui.commands.formatDocCommentCommand";

    private static final String PROJECT_NAME = "CommonModule";

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

    @Rule
    public TestingWorkspace testingWorkspace = new TestingWorkspace(true, false);

    protected static final String FOLDER_RESOURCE = "/resources/";

    private IProject project;

    private OpenHelper openHelper = new OpenHelper();

    private ICommandService commandService;
    private IHandlerService handlerService;

    @Before
    public void setUp() throws CoreException
    {
        commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
        handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);

        IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
        PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);

        boolean closed = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
        assertTrue(closed);
        Display display = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay();
        while (!display.isDisposed() && display.readAndDispatch())
        {
            // do it
        }

        project = testingWorkspace.getProject(PROJECT_NAME);

        if (!project.exists() || !project.isAccessible())
        {
            testingWorkspace.cleanUpWorkspace();
            project = this.testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        }
    }

    @After
    public void shutDown()
    {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
        Display display = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay();
        while (!display.isDisposed() && display.readAndDispatch())
        {
            // do it
        }
    }

    /**
     * Test format of doc comment.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormatDocComment() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-format.bsl");

        IFile file = project.getFile(COMMON_MODULE_FILE_NAME);

        ISelection selection = new TextSelection(14, 1);
        IEditorPart fEditor = openHelper.openEditor(file, selection);
        assertTrue(fEditor instanceof FormEditor);

        IEditorPart editor = ((FormEditor)fEditor).getActiveEditor();
        assertTrue(editor instanceof BslXtextEditor);


        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(editor);

        BslXtextEditor bslEditor = (BslXtextEditor)editor;
        String expected = getResourceText(FOLDER_RESOURCE + "doc-comment-format.bsl");
        assertEquals(expected, bslEditor.getDocument().get());

        Command command = commandService.getCommand(COMMAND_ID);
        assertNotNull(command);

        ExecutionEvent executionEvent = handlerService.createExecutionEvent(command, new Event());
        assertNotNull(executionEvent);

        command.executeWithChecks(executionEvent);

        expected = getResourceText(FOLDER_RESOURCE + "doc-comment-format-result.bsl");
        assertEquals(expected, bslEditor.getDocument().get());
    }

    private String getResourceText(String pathToResource) throws Exception
    {
        try (InputStream in = getClass().getResourceAsStream(pathToResource))
        {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void updateModule(String pathToResource) throws Exception
    {
        IFile file = project.getFile(COMMON_MODULE_FILE_NAME);
        try (InputStream in = getClass().getResourceAsStream(pathToResource))
        {
            if (file.exists())
            {
                file.setContents(in, true, true, new NullProgressMonitor());
            }
            else
            {
                file.create(in, true, new NullProgressMonitor());
            }
        }
        testingWorkspace.waitForBuildCompletion();
    }
}
