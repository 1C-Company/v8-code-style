/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.fix.itests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.common.Symbols;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.FormContextServerCallInEventHandlerCheck;

/**
 * Tests for {@code FormContextServerCallInEventHandlerFix} fix.
 *
 * @author Ivan Sergeev
 */
public class FormContextServerCallInEventHandlerFixTest
    extends AbstractQuickFixTest
{
    private static final String CHECK_ID = "form-context-server-call-in-event-handler"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "CommonForm"; //$NON-NLS-1$

    private static final String FQN_FORM = "CommonForm.Form.Form"; //$NON-NLS-1$

    private static final String FORM_MODULE_FILE_NAME = "/src/CommonForms/Form/Module.bsl"; //$NON-NLS-1$

    private static final String SERVER_METHOD = "ПриИзмененииОбразцаНаСервере"; //$NON-NLS-1$

    private static final String FIX_DESCRIPTION = "Changing the directive to an appropriate one"; //$NON-NLS-1$

    /**
     * Instantiates a new form context server call in event handler fix test.
     */
    public FormContextServerCallInEventHandlerFixTest()
    {
        super(FormContextServerCallInEventHandlerCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return FORM_MODULE_FILE_NAME;
    }

    @Override
    protected Module getModule()
    {
        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Module module = ((Form)object).getModule();
        assertNotNull(module);
        return module;
    }

    /**
     * The fix replaces the {@code &AtServer} directive of the called method
     * with the {@code &AtServerNoContext} one and resolves the marker.
     * The fix is interactive: it edits the open form editor, so the editor must be saved.
     *
     * @throws Exception the exception
     */
    @Test
    public void testReplaceServerDirective() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-context-server-call-in-event-handler-fix.bsl"); //$NON-NLS-1$

        IDtProject dtProject = getProject();
        try
        {
            Marker marker = findFirstMarker(dtProject);
            assertNotNull(CHECK_ID, marker);

            Method methodBefore = findMethod(getModule(), SERVER_METHOD);
            assertNotNull(SERVER_METHOD, methodBefore);
            assertTrue(hasServerPragma(methodBefore));
            assertFalse(hasServerNoContextPragma(methodBefore));

            performFix(marker, FIX_DESCRIPTION);
            saveDirtyEditors();
            waitForDD(dtProject);

            dtProject.getWorkspaceProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
            waitForDD(dtProject);

            Method methodAfter = findMethod(getModule(), SERVER_METHOD);
            assertNotNull(SERVER_METHOD, methodAfter);
            assertTrue(hasServerNoContextPragma(methodAfter));
            assertFalse(hasServerPragma(methodAfter));

            assertMarkerGone();
        }
        finally
        {
            closeEditors();
        }
    }

    private Marker findFirstMarker(IDtProject dtProject) throws Exception
    {
        TreeIterator<EObject> iterator = EcoreUtil.getAllContents(getModule(), true);
        while (iterator.hasNext())
        {
            if (iterator.next() instanceof Invocation invocation)
            {
                Marker marker = getFirstMarker(CHECK_ID, invocation, dtProject);
                if (marker != null)
                {
                    return marker;
                }
            }
        }
        return null;
    }

    private Method findMethod(Module module, String name)
    {
        return module.allMethods().stream().filter(m -> name.equals(m.getName())).findFirst().orElse(null);
    }

    private boolean hasServerNoContextPragma(Method method)
    {
        return method.getPragmas().stream()
            .map(Pragma::getSymbol)
            .anyMatch(symbol -> Symbols.AT_SERVER_NO_CONTEXT_RUS.equalsIgnoreCase(symbol)
                || Symbols.AT_SERVER_NO_CONTEXT_INTNL.equalsIgnoreCase(symbol));
    }

    private boolean hasServerPragma(Method method)
    {
        return method.getPragmas().stream()
            .map(Pragma::getSymbol)
            .anyMatch(symbol -> Symbols.AT_SERVER_RUS.equalsIgnoreCase(symbol)
                || Symbols.AT_SERVER_INTNL.equalsIgnoreCase(symbol));
    }

    private void saveDirtyEditors()
    {
        runOnAllPages(page -> page.saveAllEditors(false));
    }

    private void closeEditors()
    {
        runOnAllPages(page -> page.closeAllEditors(false));
    }

    private void runOnAllPages(Consumer<IWorkbenchPage> action)
    {
        PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
            for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
            {
                for (IWorkbenchPage page : window.getPages())
                {
                    action.accept(page);
                }
            }
        });
    }
}