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
package com.e1c.v8codestyle.form.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.CommandHandler;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormCommand;
import com._1c.g5.v8.dt.form.model.FormCommandHandlerContainer;
import com._1c.g5.v8.dt.form.model.FormFactory;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.FormCommandsSingleEventHandlerCheck;

/**
 * Tests for {@link FormCommandsSingleEventHandlerCheck} check.
 *
 * @author Artem Iliukhin
 */
public class FormCommandsSingleEventHandlerCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "form-commands-single-action-handler"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "FormCommandsSingleEventHandlerCheck";
    private static final String FQN_FORM = "Catalog.Catalog.Form.CompliantForm.Form";
    private static final String FQN_FORM_NON_COMPLIANT = "Catalog.Catalog.Form.NonCompliantForm.Form";

    @Test
    public void testFormCommandHandlerRegion() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);

        Marker marker = getFirstNestedMarker(CHECK_ID, object, dtProject);
        assertNull(marker);
    }

    @Test
    public void testFormCommandsOneHandlerRegion() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM_NON_COMPLIANT, dtProject);
        assertTrue(object instanceof Form);

        Marker marker = getFirstNestedMarker(CHECK_ID, object, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testAddSameAction() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel bmModel = bmModelManager.getModel(dtProject);
        bmModel.execute(new AbstractBmTask<Void>("change")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                Form form = (Form)transaction.getTopObjectByFqn(FQN_FORM);

                FormCommand formCommand = form.getFormCommands().get(0);

                CommandHandler handler = FormFactory.eINSTANCE.createCommandHandler();
                handler.setName("Command2");

                FormCommandHandlerContainer container = FormFactory.eINSTANCE.createFormCommandHandlerContainer();
                container.setHandler(handler);

                formCommand.setAction(container);
                form.getFormCommands().add(formCommand);

                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNotNull(marker);
    }
}
