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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DataPath;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.FormListRefUseAlwaysFlagDisabledCheck;

/**
 * Tests for {@link FormListRefUseAlwaysFlagDisabledCheck} check.
 *
 * @author Olga Bozhko
 */
public class FormListRefUseAlwaysFlagDisabledCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "form-list-ref-use-always-flag-disabled";
    private static final String PROJECT_NAME = "FormListRefUseAlwaysFlagDisabled";
    private static final String FQN_FORM_EN = "Catalog.TestCatalog.Form.TestListForm.Form";
    private static final String FQN_FORM_CUSTOM_EN = "Catalog.TestCatalog.Form.TestCustomListForm.Form";
    private static final String FQN_FORM_RU = "Catalog.TestCatalog.Form.TestListFormRu.Form";
    private static final String FQN_FORM_CUSTOM_RU = "Catalog.TestCatalog.Form.TestCustomListFormRu.Form";
    private static final String FQN_NON_OBJECT = "InformationRegister.TestInformationRegister.Form.TestListForm.Form";

    /**
     * Test Use Always flag is disabled for the Reference attribute in dynamic list.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUseAlwaysDisabledForRef() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        List.of(FQN_FORM_EN, FQN_FORM_CUSTOM_EN, FQN_FORM_RU, FQN_FORM_CUSTOM_RU).forEach(formFqn -> {
            IBmObject object = getTopObjectByFqn(formFqn, dtProject);
            assertTrue(object instanceof Form);

            Form form = (Form)object;
            assertTrue(form.getAttributes().get(0).getNotDefaultUseAlwaysAttributes().isEmpty());

            Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
            assertNotNull(marker);
        });
    }

    /**
     * Test Use Always flag is enabled for the Reference attribute in dynamic list.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUseAlwaysEnabledForRef() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);

        List.of(FQN_FORM_EN, FQN_FORM_CUSTOM_EN, FQN_FORM_RU, FQN_FORM_CUSTOM_RU).forEach(formFqn -> {
            model.execute(new AbstractBmTask<Void>("change mode")
            {
                @Override
                public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
                {
                    Form form = (Form)transaction.getTopObjectByFqn(formFqn);
                    FormItem item = form.getItems().get(1);
                    assertTrue(item instanceof Table);
                    Table table = (Table)item;
                    AbstractDataPath path = (DataPath)table.getItems().get(0).eContents().get(1);
                    form.getAttributes().get(0).getNotDefaultUseAlwaysAttributes().add(path);
                    assertFalse(form.getAttributes().get(0).getNotDefaultUseAlwaysAttributes().isEmpty());
                    return null;
                }
            });
            waitForDD(dtProject);

            IBmObject object = getTopObjectByFqn(formFqn, dtProject);
            assertTrue(object instanceof Form);

            Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
            assertNull(marker);
        });
    }

    /**
     * Test Use Always flag is disabled for an attribute in dynamic list of the non object table.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUseAlwaysDisabledForAttributeOnNonObjectTable() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_NON_OBJECT, dtProject);
        assertTrue(object instanceof Form);

        Form form = (Form)object;
        assertTrue(form.getAttributes().get(0).getNotDefaultUseAlwaysAttributes().isEmpty());

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNull(marker);
    }

    /**
     * Test Use Always flag is enabled in the dynamic list for an attribute with a nonstandard data path.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUseAlwaysEnabledForAttributeWithNonStandardDataPath() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);

        List.of(FQN_FORM_RU, FQN_FORM_CUSTOM_RU).forEach(formFqn -> {
            model.execute(new AbstractBmTask<Void>("change mode")
            {
                @Override
                public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
                {
                    Form form = (Form)transaction.getTopObjectByFqn(formFqn);
                    FormItem item = form.getItems().get(1);
                    assertTrue(item instanceof Table);
                    Table table = (Table)item;
                    AbstractDataPath path = (DataPath)table.getItems().get(0).eContents().get(1);
                    path.getSegments().add(0, "SomeExtraSegment");
                    form.getAttributes().get(0).getNotDefaultUseAlwaysAttributes().add(path);
                    String fqn = form.getAttributes().get(0).getNotDefaultUseAlwaysAttributes().get(0).toString();
                    assertTrue(fqn.equals("/SomeExtraSegment/List/Ref") || fqn.equals("/SomeExtraSegment/Список/Ref"));
                    return null;
                }
            });
            waitForDD(dtProject);

            IBmObject object = getTopObjectByFqn(formFqn, dtProject);
            assertTrue(object instanceof Form);

            Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
            assertNotNull(marker);
        });
    }
}
