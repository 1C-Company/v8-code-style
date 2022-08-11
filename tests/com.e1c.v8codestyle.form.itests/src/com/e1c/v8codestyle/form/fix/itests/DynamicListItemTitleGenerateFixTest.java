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
package com.e1c.v8codestyle.form.fix.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.form.fix.DynamicListItemTitleGenerateFix;

/**
 * Tests for {@link DynamicListItemTitleGenerateFix} fix.
 *
 * @author Dmitriy Marmyshev
 */
public class DynamicListItemTitleGenerateFixTest
    extends FormFixTestBase
{

    private static final String CHECK_ID = "form-dynamic-list-item-title";

    private static final String FIX_DESCRIPTION_PATTERN = "Generate title \".+\" for dynamic list form item \".+\"";

    private static final String PROJECT_NAME = "FormDynamicListItemTitle";

    private static final String FQN_FORM = "Catalog.Products.Form.ListForm.Form";

    public DynamicListItemTitleGenerateFixTest()
    {
        super(FIX_DESCRIPTION_PATTERN);
    }

    @Test
    public void testApplyFix() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        FormField item = getListItem(form, "Code");
        assertNotNull(item);
        Marker marker = getFirstMarker(CHECK_ID, item, dtProject);
        assertNotNull(marker);

        // make fix
        applyFix(marker, dtProject);

        waitForDD(dtProject);

        object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        form = (Form)object;

        item = getListItem(form, "Code");
        assertNotNull(item);
        assertNotNull(item.getTitle().get("en"));

        marker = getFirstMarker(CHECK_ID, item, dtProject);
        assertNull(marker);

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
