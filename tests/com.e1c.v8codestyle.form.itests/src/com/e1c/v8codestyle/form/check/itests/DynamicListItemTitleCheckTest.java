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

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.form.check.DynamicListItemTitleCheck;

/**
 * Tests for {@link DynamicListItemTitleCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class DynamicListItemTitleCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "form-dynamic-list-item-title";

    private static final String PROJECT_NAME = "FormDynamicListItemTitle";

    private static final String FQN_FORM = "Catalog.Products.Form.ListForm.Form";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test the List.Code is custom query field and has no title, that is incorrect
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListCodeIsIncorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "Code");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNotNull(marker);
    }

    /**
     *
     * Test the List.Code1 is custom query field "Code" and has no title, that is incorrect
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListCode1IsIncorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "Code1");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNotNull(marker);
    }

    /**
     *
     * Test the List.CodeCorrect is custom query field "Code" and has title, that is correct
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListCodeCorrectIsCorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "CodeCorrect");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNull(marker);
    }

    /**
     *
     * Test the List.Description is standard field "Description" and has no title, that is correct
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListDescriptionIsCorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "Description");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNull(marker);
    }

    /**
     *
     * Test the List.SKU is MD attribute "SKU" and has no title, that is correct
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListSKUIsCorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "SKU");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNull(marker);
    }

    /**
     *
     * Test the List.MyPredefined is custom query field "MyPredefined" and has no title, that is incorrect
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListMyPredefinedIsIncorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "MyPredefined");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNotNull(marker);
    }

    /**
     *
     * Test the List.MyPredefined2 is custom query field "MyPredefined2" with DCS title and has no item title,
     * that is correct
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListMyPredefined2IsCorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "MyPredefined2");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNull(marker);
    }

    /**
     *
     * Test the List.CorrectField is custom query field "MyPredefined2" with DCS title and has no item title,
     * that is correct
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListCorrectFieldIsCorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "CorrectField");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNull(marker);
    }

    /**
     *
     * Test the List.SkuDescription is custom query field "SkuDescription" has no item title,
     * that is incorrect
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormListCorrectFieldIsIncorrect() throws Exception
    {

        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "SkuDescription");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, getProject());
        assertNotNull(marker);
    }

    private FormField getListItem(Form form, String name) throws Exception
    {
        for (FormItem item : form.getItems())
        {
            if ("List".equals(item.getName()))
            {
                assertTrue(item instanceof Table);
                Table table = (Table)item;
                for (FormItem subItem : table.getItems())
                {
                    if (name.equals(subItem.getName()))
                    {
                        assertTrue(subItem instanceof FormField);
                        return (FormField)subItem;
                    }
                }
                return null;
            }
        }
        return null;
    }
}
