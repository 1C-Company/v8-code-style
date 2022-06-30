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
 *     Manaev Konstantin - issue #855
 *******************************************************************************/
package com.e1c.v8codestyle.form.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.EventHandler;
import com._1c.g5.v8.dt.form.model.FieldExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.FormItemsSingleEventHandlerCheck;

/**
 * Tests for {@link FormItemsSingleEventHandlerCheck} check.
 *
 * @author Manaev Konstantin
 */
public class FormItemsSingleEventHandlerCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "form-items-single-event-handler";
    private static final String PROJECT_NAME = "FormItemsSingleEventHandler";
    private static final String FQN_FORM = "CommonForm.TestForm.Form";
    private static final URI URI_ATTRIBUTE1 =
        URI.createURI("bm://FormItemsSingleEventHandler/CommonForm.TestForm.Form#/items:Attribute1");
    private static final URI URI_ATTRIBUTE2 =
        URI.createURI("bm://FormItemsSingleEventHandler/CommonForm.TestForm.Form#/items:Attribute2");
    private static final URI URI_TABLE1_COLUMN = URI.createURI(
        "bm://FormItemsSingleEventHandler/CommonForm.TestForm.Form#/items:Table1/items:Table1Group1/items:Table1Column1");

    /**
     * Test the form items event handlers are single
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormItemsEventHandlersAreSingle() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Marker marker = getFirstNestedMarker(CHECK_ID, object, dtProject);
        assertNull(marker);
    }

    /**
     * Test the form items event handler assigned for other event
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormItemsEventHandlerAssignedForOtherEvent() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change event handler")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                EList<EventHandler> attribute1Handlers = getHandlersByURI(transaction, URI_ATTRIBUTE1);
                assertEquals(attribute1Handlers.size(), 2);
                attribute1Handlers.get(1).setName(attribute1Handlers.get(0).getName());
                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Marker marker = getFirstNestedMarker(CHECK_ID, object, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test the form items event handler assigned for other item
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormItemsEventHandlerAssignedForOtherItem() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change event handler")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                EList<EventHandler> attribute1Handlers = getHandlersByURI(transaction, URI_ATTRIBUTE1);
                assertFalse(attribute1Handlers.isEmpty());
                EList<EventHandler> attribute2Handlers = getHandlersByURI(transaction, URI_ATTRIBUTE2);
                assertFalse(attribute2Handlers.isEmpty());
                attribute2Handlers.get(0).setName(attribute1Handlers.get(0).getName());
                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Marker marker = getFirstNestedMarker(CHECK_ID, object, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test the form items event handler assigned for other nested item
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormItemsEventHandlerAssignedForOtherNestedItem() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change event handler")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                EList<EventHandler> attribute1Handlers = getHandlersByURI(transaction, URI_ATTRIBUTE1);
                assertFalse(attribute1Handlers.isEmpty());
                EList<EventHandler> columnHandlers = getHandlersByURI(transaction, URI_TABLE1_COLUMN);
                assertFalse(columnHandlers.isEmpty());
                columnHandlers.get(0).setName(attribute1Handlers.get(0).getName());
                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Marker marker = getFirstNestedMarker(CHECK_ID, object, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test the form items event handler assigned for form event
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormItemsEventHandlerAssignedForFormEvent() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change event handler")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                EList<EventHandler> attribute1Handlers = getHandlersByURI(transaction, URI_ATTRIBUTE1);
                assertFalse(attribute1Handlers.isEmpty());
                Form form = (Form)transaction.getTopObjectByFqn(FQN_FORM);
                assertNotNull(form);
                EList<EventHandler> formHandlers = form.getHandlers();
                assertFalse(formHandlers.isEmpty());
                formHandlers.get(0).setName(attribute1Handlers.get(0).getName());
                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Marker marker = getFirstNestedMarker(CHECK_ID, object, dtProject);
        assertNotNull(marker);
    }

    private final EList<EventHandler> getHandlersByURI(IBmTransaction transaction, URI uriItem)
    {
        FormField item = (FormField)transaction.getObjectByUri(uriItem);
        assertNotNull(item);
        FieldExtInfo extInfo = item.getExtInfo();
        assertNotNull(extInfo);
        return extInfo.getHandlers();
    }
}
