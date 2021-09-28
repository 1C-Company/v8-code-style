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
 *     Viktor Gukov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.ql.check.itests;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

/**
 * Test {@link ForUpdateUsing} class that checks FOR UPDATE clause existence.
 *
 * @author Viktor Gukov
 */
public class ForUpdateUsingTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "ql-for-update-using"; //$NON-NLS-1$;
    private static final String PROJECT_NAME = "ForUpdateUsing";

    @Test
    public void testForUpdateUse() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        IBmObject object = getTopObjectByFqn("CommonForm.Form.Form", project);
        assertNotNull(object);
        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), project);
        assertNotNull(marker);
    }

    @Test
    public void testQueryWithoutForUpdate() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        IBmObject object = getTopObjectByFqn("CommonForm.Form.Form", project);
        assertNotNull(object);

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
                ((DynamicListExtInfo)attribute.getExtInfo()).setQueryText("SELECT\r\n   \"One\" AS Field");
                return form;
            }
        });
        waitForDD(project);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), project);
        assertNull(marker);
    }
}
