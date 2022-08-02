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

import java.util.Optional;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.dcs.model.schema.DataCompositionSchemaDataSetField;
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.form.fix.DynamicListFieldTitleGenerateFix;

/**
 * Tests for {@link DynamicListFieldTitleGenerateFix} fix.
 *
 * @author Dmitriy Marmyshev
 */
public class DynamicListFieldTitleGenerateFixTest
    extends FormFixTestBase
{
    private static final String CHECK_ID = "form-dynamic-list-item-title";

    private static final String FIX_DESCRIPTION_PATTERN =
        "Generate default title for dynamic list field \\(8\\.3\\.19\\+\\)";

    private static final String PROJECT_NAME = "FormDynamicListItemTitle";

    private static final String FQN_FORM = "Catalog.Products.Form.ListForm.Form";

    public DynamicListFieldTitleGenerateFixTest()
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

        String name = "SkuDescription";

        FormField item = getListItem(form, name);
        assertNotNull(item);
        assertNull(item.getTitle().get("en"));
        Marker marker = getFirstMarker(CHECK_ID, item, dtProject);
        assertNotNull(marker);

        // make fix
        applyFix(marker, dtProject);

        waitForDD(dtProject);

        object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        form = (Form)object;


        item = getListItem(form, name);
        assertNotNull(item);
        assertNull(item.getTitle().get("en"));

        marker = getFirstMarker(CHECK_ID, item, dtProject);
        assertNull(marker);

        DynamicListExtInfo custormQuery = (DynamicListExtInfo)form.getAttributes().get(0).getExtInfo();
        Optional<DataCompositionSchemaDataSetField> field = custormQuery.getFields()
            .stream()
            .filter(DataCompositionSchemaDataSetField.class::isInstance)
            .map(DataCompositionSchemaDataSetField.class::cast)
            .filter(f -> name.equals(f.getDataPath()))
            .findFirst();
        assertTrue(field.isPresent());

        if (field.isPresent())
        {
            assertNotNull(field.get().getTitle().getLocalValue().getContent().get("en"));
        }
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
