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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.lang.reflect.Field;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.bsl.ui.editor.BslXtextEditor;
import com._1c.g5.v8.dt.testing.TestingWorkspace;
import com._1c.g5.v8.dt.ui.util.OpenHelper;
import com.e1c.v8codestyle.internal.bsl.ui.views.BslDocCommentView;

/**
 * Integration test of the {@link BslDocCommentViewTest} that process selection in BSL editor and reacts in the view.
 *
 * @author Dmitriy Marmyshev
 */
public class BslDocCommentViewTest
{
    private static final String PROJECT_NAME = "CommonModule";

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

    @Rule
    public TestingWorkspace testingWorkspace = new TestingWorkspace(true, false);

    protected static final String FOLDER_RESOURCE = "/resources/";

    private IProject project;

    private OpenHelper openHelper = new OpenHelper();

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
     * Load module and open Bsl documentation comment view, select elements in doc comment test
     * then check selected in view.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOpenModuleAndSelectElements() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-view.bsl");

        IFile file = project.getFile(COMMON_MODULE_FILE_NAME);

        IViewPart view =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(BslDocCommentView.ID);
        assertTrue(view instanceof BslDocCommentView);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(view);

        IEditorPart fEditor = openHelper.openEditor(file, new TextSelection(14, 1));
        assertTrue(fEditor instanceof FormEditor);

        IEditorPart editor = ((FormEditor)fEditor).getActiveEditor();
        assertTrue(editor instanceof BslXtextEditor);

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(editor);

        BslXtextEditor bslEditor = (BslXtextEditor)editor;

        selectText(bslEditor, 14, 1, view);
        Object first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof TextPart);
        TextPart textPart = (TextPart)first;
        assertEquals(0, textPart.getLineNumber());
        assertEquals("Method description", textPart.getText());

        selectText(bslEditor, 92, 1, view);
        first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof TextPart);
        textPart = (TextPart)first;
        assertEquals(4, textPart.getLineNumber());
        assertEquals("- has not type for key", textPart.getText());

        selectText(bslEditor, 77, 1, view);
        first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof FieldDefinition);
        FieldDefinition fieldPart = (FieldDefinition)first;
        assertEquals(4, fieldPart.getLineNumber());
        assertEquals("Key1", fieldPart.getName());

        selectText(bslEditor, 50, 1, view);
        first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof FieldDefinition);
        fieldPart = (FieldDefinition)first;
        assertEquals(3, fieldPart.getLineNumber());
        assertEquals("Parameters", fieldPart.getName());

        selectText(bslEditor, 33, 0, view);
        first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof ParametersSection);
        ParametersSection sectionPart = (ParametersSection)first;
        assertEquals(2, sectionPart.getLineNumber());

        selectText(bslEditor, 64, 0, view);
        first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof TypeDefinition);
        TypeDefinition typePart = (TypeDefinition)first;
        assertEquals(3, typePart.getLineNumber());
        assertEquals("Structure", typePart.getTypeName());

        selectText(bslEditor, 169, 0, view);
        first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof ReturnSection);
        ReturnSection returnPart = (ReturnSection)first;
        assertEquals(0, returnPart.getLineNumber());

        selectText(bslEditor, 187, 1, view);
        first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof LinkPart);
        LinkPart linkPart = (LinkPart)first;
        assertEquals(1, linkPart.getLineNumber());
        assertEquals("Test", linkPart.getLinkText());
        assertEquals("See Test", linkPart.getInitialContent());

        selectText(bslEditor, 192, 1, view);
        first = getViewerSelection((BslDocCommentView)view);
        assertTrue(first instanceof LinkPart);
        linkPart = (LinkPart)first;
        assertEquals(1, linkPart.getLineNumber());
        assertEquals("Test", linkPart.getLinkText());
        assertEquals("See Test", linkPart.getInitialContent());

        selectText(bslEditor, 243, 1, view);
        TreeViewer viewer = getViewer((BslDocCommentView)view);
        ITreeSelection selection = viewer.getStructuredSelection();
        assertTrue(selection.isEmpty());
        Object input = viewer.getInput();
        assertTrue(input instanceof BslDocumentationComment);

    }

    private Object getViewerSelection(BslDocCommentView view) throws Exception
    {
        TreeViewer viewer = getViewer(view);
        ITreeSelection selection = viewer.getStructuredSelection();
        assertFalse(selection.isEmpty());
        return selection.getFirstElement();
    }

    private void selectText(BslXtextEditor bslEditor, int offset, int lenght, IViewPart view) throws Exception
    {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(bslEditor);

        boolean[] selected = new boolean[1];
        ISelectionListener listener = (part, selection) -> {
            if (selection instanceof TextSelection)
            {
                selected[0] = true;
            }
        };
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPostSelectionListener(listener);
        Display display = bslEditor.getShell().getDisplay();
        display.syncExec(() -> {
            bslEditor.getSelectionProvider().setSelection(new TextSelection(offset, lenght));
        });
        waitEventSetnd(bslEditor);

        for (int i = 0; !selected[0] && i < 10; i++)
        {
            Thread.sleep(500);
            waitEventSetnd(bslEditor);
        }

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().removePostSelectionListener(listener);
    }

    private TreeViewer getViewer(BslDocCommentView view) throws Exception
    {
        Field field = view.getClass().getDeclaredField("viewer");
        field.setAccessible(true);
        return (TreeViewer)field.get(view);
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

    private void waitEventSetnd(IWorkbenchPart part)
    {
        Display display = part.getSite().getShell().getDisplay();
        while (!display.isDisposed() && display.readAndDispatch())
        {
            // do it
        }
    }
}
