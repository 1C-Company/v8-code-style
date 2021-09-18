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
package com.e1c.v8codestyle.ql.check.itests;

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
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.ql.check.CamelCaseStringLiteral;

/**
 * Test {@link CamelCaseStringLiteral} class that checks string literal contains only camel-case words
 * or non-word symbols.
 *
 * @author Dmitriy Marmyshev
 */
public class CamelCaseStringLiteralTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "ql-camel-case-string-literal"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "StringLiteralCameCase";

    @Test
    public void testCamelCaseWithSymbols() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        IBmObject object = getTopObjectByFqn("CommonForm.Form.Form", project);
        assertNotNull(object);
        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), project);
        assertNotNull(marker);
    }

    @Test
    public void testCamelCaseWithoutSymbols() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        IBmObject object = getTopObjectByFqn("CommonForm.Form.Form", project);
        assertTrue(object instanceof Form);
        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), project);
        assertNotNull(marker);

        // update query text
        long id = object.bmGetId();
        IBmModel model = bmModelManager.getModel(project);
        object = model.getGlobalContext().execute(new AbstractBmTask<IBmObject>("Edit Form")
        {

            @Override
            public IBmObject execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                Form form = (Form)transaction.getObjectById(id);
                FormAttribute attribute = form.getAttributes().get(0);
                ((DynamicListExtInfo)attribute.getExtInfo()).setQueryText("SELECT\r\n" + "   \"A1\" AS Field");

                return form;
            }
        });
        waitForDD(project);

        marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), project);
        assertNull(marker);
    }
}
