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
package com.e1c.v8codestyle.form.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.form.check.DataPathReferredObjectCheck;

/**
 * Tests for {@link DataPathReferredObjectCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class DataPathReferredObjectCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "form-data-path";

    private static final String PROJECT_NAME = "FormDataPath";

    private static final String FQN_FORM = "CommonForm.ListForm.Form";

    private static final String FQN_FORM2 = "Catalog.Products.Form.ListForm.Form";

    private static final String FQN_FORM3 = "Catalog.Products.Form.ItemForm.Form";

    /**
     * Test the dynamic list form with custom query finds data path to unknown field that not exist in query text
     *
     * @throws Exception the exception
     */
    @Test
    public void testDynamicListFormWithCustomQuery() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormItem item = getItemByName(form, "List");
        assertTrue(item instanceof Table);

        AbstractDataPath dataPath = ((Table)item).getDataPath();

        Marker marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "ListField1");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "ListField2");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test the dynamic list form with main table finds data path to unknown field that not exist in the object
     *
     * @throws Exception the exception
     */
    @Test
    public void testListFormWithMainTable() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM2, dtProject);
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormItem item = getItemByName(form, "List");
        assertTrue(item instanceof Table);

        AbstractDataPath dataPath = ((Table)item).getDataPath();

        Marker marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        dataPath = ((Table)item).getRowPictureDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "Ref");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "Code");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "SKU");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "Articule");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNotNull(marker);

        item = getItemByName(form, "Current");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "CurrentUnknown");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test the object form finds data path to unknown field that not exist in the object
     *
     * @throws Exception the exception
     */
    @Test
    public void testItemForm() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM3, dtProject);
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormItem item = getItemByName(form, "Code");
        assertTrue(item instanceof FormField);

        AbstractDataPath dataPath = ((FormField)item).getDataPath();

        Marker marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "SKU");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNull(marker);

        item = getItemByName(form, "Article");
        assertTrue(item instanceof FormField);

        dataPath = ((FormField)item).getDataPath();

        marker = getFirstMarker(CHECK_ID, dataPath, dtProject);
        assertNotNull(marker);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    private FormItem getItemByName(Form form, String name)
    {
        for (TreeIterator<EObject> iterator = form.eAllContents(); iterator.hasNext();)
        {
            EObject child = iterator.next();
            if (child instanceof FormItem && name.equals(((FormItem)child).getName()))
            {
                return (FormItem)child;
            }
        }
        return null;

    }

}
