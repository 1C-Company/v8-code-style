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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.InvocationFormEventHandlerCheck;

/**
 * Tests for {@link InvocationFormEventHandlerCheck} check
 *
 * @author Artem Iliukhin
 */
public class InvocationFormEventHandlerCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "invocation-form-event-handler";
    private static final String INV_FORM_EVENT_HANDLER_CHECK = "Program invocation of form event handler";
    private static final String PROJECT_NAME = "InvocationFormEventHandler";
    private static final String FQN_CATALOG_PRODUCTS_FORM = "Catalog.Products.Form.ItemForm.Form";
    private static final String FQN_CATALOG_PRODUCTS_FORM_ITEM = "Catalog.Products.Form.ItemForm1.Form";
    private static final String FQN_CATALOG_PRODUCTS_FORM_COMMAND = "Catalog.Products.Form.ItemForm2.Form";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void invocationFormEventHandler() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS_FORM, dtProject);
        assertTrue(mdObject instanceof Form);
        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method noncompliantMethod = methods.get(1);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getLeft(), dtProject);
        assertNotNull(marker);
        assertEquals(INV_FORM_EVENT_HANDLER_CHECK, marker.getMessage());
    }

    @Test
    public void invocationItemEventHandler() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS_FORM_ITEM, dtProject);
        assertTrue(mdObject instanceof Form);
        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method noncompliantMethod = methods.get(1);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getLeft(), dtProject);
        assertNotNull(marker);
        assertEquals(INV_FORM_EVENT_HANDLER_CHECK, marker.getMessage());
    }

    @Test
    public void invocationCommandEventHandler() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS_FORM_COMMAND, dtProject);
        assertTrue(mdObject instanceof Form);
        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method noncompliantMethod = methods.get(1);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getLeft(), dtProject);
        assertNotNull(marker);
        assertEquals(INV_FORM_EVENT_HANDLER_CHECK, marker.getMessage());
    }
}
