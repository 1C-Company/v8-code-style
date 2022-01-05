/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.metadata.mdclass.BasicCommand;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ExportMethodInCommandFormModuleCheck;

/**
 * Tests for {@link ExportMethodInCommandFormModuleCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ExportMethodInCommandFormModuleCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "export-method-in-command-form-module";
    private static final String MESSAGE =
        "Do not embed export procedures and functions in modules of commands and forms. "
            + "You cannot address such modules from external code, "
            + "so embedded export procedures and functions become dysfunctional.";
    private static final String PROJECT_NAME = "ExportMethodInCommandFormModuleCheck";
    private static final String FQN_CATALOG = "Catalog.Products";
    private static final String FQN_CATALOG_FORM = "Catalog.Products.Form.ItemForm.Form";
    private static final String FQN_COMMAND = "CommonCommand.CommonCommand";
    private static final String FQN_COMMAND_NOTIFY = "CommonCommand.CommonCommand1";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testForm() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_FORM, dtProject);
        assertTrue(mdObject instanceof Form);
        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(1, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNotNull(marker);
        assertEquals(MESSAGE, marker.getMessage());
    }

    @Test
    public void testCommonCommand() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_COMMAND, dtProject);
        assertTrue(mdObject instanceof BasicCommand);
        Module module = ((BasicCommand)mdObject).getCommandModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(1, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNotNull(marker);
        assertEquals(MESSAGE, marker.getMessage());
    }

    @Test
    public void testCommonCommandNotify() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_COMMAND_NOTIFY, dtProject);
        assertTrue(mdObject instanceof BasicCommand);
        Module module = ((BasicCommand)mdObject).getCommandModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method method = methods.get(1);
        Marker marker = getFirstMarker(CHECK_ID, method, dtProject);
        assertNull(marker);
    }

    @Test
    public void testCatalogCommand() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG, dtProject);
        assertTrue(mdObject instanceof Catalog);
        assertEquals(2, ((Catalog)mdObject).getCommands().size());
        BasicCommand command = ((Catalog)mdObject).getCommands().get(0);
        assertTrue(command instanceof BasicCommand);
        Module module = command.getCommandModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(1, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNotNull(marker);
        assertEquals(MESSAGE, marker.getMessage());
    }

    @Test
    public void testCatalogCommandNotify() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG, dtProject);
        assertTrue(mdObject instanceof Catalog);
        assertEquals(2, ((Catalog)mdObject).getCommands().size());
        BasicCommand command = ((Catalog)mdObject).getCommands().get(1);
        assertTrue(command instanceof BasicCommand);
        Module module = command.getCommandModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method method = methods.get(1);
        Marker marker = getFirstMarker(CHECK_ID, method, dtProject);
        assertNull(marker);
    }

}
