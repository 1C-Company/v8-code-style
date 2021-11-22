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
package com.e1c.v8codestyle.form.check.itests;

import static org.junit.Assert.assertEquals;
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
import com._1c.g5.v8.dt.form.model.FieldExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.InputFieldExtInfo;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.InputFieldListChoiceMode;

/**
 * Tests for {@link InputFieldListChoiceMode} check.
 *
 * @author Dmitriy Marmyshev
 */
public class InputFieldListChoiceModeTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "input-field-list-choice-mode";

    private static final String PROJECT_NAME = "InputFieldListChoiceMode";

    private static final String FQN_FORM = "CommonForm.Form.Form";

    /**
     * Test the form input field list choice mode not set
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormInputFieldListChoiceModeNotSet() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Form form = (Form)object;
        InputFieldExtInfo extInfo = getItemExtInfo(form);
        Marker marker = getFirstMarker(CHECK_ID, extInfo, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test form input field list choice mode set.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormInputFieldListChoiceModeSet() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change mode")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                Form form = (Form)transaction.getTopObjectByFqn(FQN_FORM);
                InputFieldExtInfo extInfo;
                extInfo = getItemExtInfo(form);
                extInfo.setListChoiceMode(true);

                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Form form = (Form)object;
        InputFieldExtInfo extInfo = getItemExtInfo(form);

        Marker marker = getFirstMarker(CHECK_ID, extInfo, dtProject);
        assertNull(marker);
    }

    private InputFieldExtInfo getItemExtInfo(Form form)
    {
        assertEquals(1, form.getItems().size());
        FormItem item = form.getItems().get(0);
        assertTrue(item instanceof FormField);
        FieldExtInfo extInfo = ((FormField)item).getExtInfo();
        assertTrue(extInfo instanceof InputFieldExtInfo);
        return (InputFieldExtInfo)extInfo;
    }
}
