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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.FormListRefUserVisibilityEnabledCheck;

/**
 * Tests for {@link FormListRefUserVisibilityEnabledCheck} check.
 *
 * @author Olga Bozhko
 */
public class FormListRefUserVisibilityEnabledCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "form-list-ref-user-visibility-enabled";
    private static final String PROJECT_NAME = "FormListRefUserVisibilityEnabled";
    private static final String FQN_FORM_EN = "Catalog.TestCatalog.Form.TestListForm.Form";
    private static final String FQN_FORM_RU = "Catalog.TestCatalog.Form.TestListFormRu.Form";
    private static final String MESSAGE = "User visibility is not disabled for the Ref field ({0}) of the {1} table";

    /**
     * Test User Visibility is enabled for the Ref field in dynamic list (En Script variant).
     *
     * @throws Exception the exception
     */
    @Test
    public void testUserVisibilityEnabledForRef() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM_EN, dtProject);
        assertTrue(object instanceof Form);

        Form form = (Form)object;
        FormItem item = form.getItems().get(1);
        assertTrue(item instanceof Table);

        Table table = (Table)item;
        assertEquals("Ref", table.getItems().get(0).getName());
        FormField fieldRef = (FormField)table.getItems().get(0);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNotNull(marker);
        assertEquals(MessageFormat.format(MESSAGE, fieldRef.getName(), table.getName()), marker.getMessage());
    }

    /**
     * Test User Visibility is disabled for the Ref field in dynamic list (En Script variant).
     *
     * @throws Exception the exception
     */
    @Test
    public void testUserVisibilityDisabledForRef() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change mode")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                Form form = (Form)transaction.getTopObjectByFqn(FQN_FORM_EN);
                FormItem item = form.getItems().get(1);
                assertTrue(item instanceof Table);
                Table table = (Table)item;
                assertEquals("Ref", table.getItems().get(0).getName());
                FormField fieldRef = (FormField)table.getItems().get(0);
                fieldRef.getUserVisible().setCommon(false);
                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM_EN, dtProject);
        assertTrue(object instanceof Form);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNull(marker);
    }

    /**
     * Test User Visibility is enabled for the Ref field in dynamic list (Ru script variant).
     *
     * @throws Exception the exception
     */
    @Test
    public void testUserVisibilityEnabledForRefRu() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change mode")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject object = transaction.getTopObjectByFqn(CONFIGURATION.getName());
                assertTrue(object instanceof Configuration);
                Configuration config = (Configuration)object;
                config.setScriptVariant(ScriptVariant.RUSSIAN);
                assertTrue(config.getScriptVariant() == ScriptVariant.RUSSIAN);
                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM_RU, dtProject);
        assertTrue(object instanceof Form);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNotNull(marker);
    }

    /**
     * Test User Visibility is disabled for the Ref field in dynamic list (Ru Script variant).
     *
     * @throws Exception the exception
     */
    @Test
    public void testUserVisibilityDisabledForRefRu() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmModel modelChangeLanguage = bmModelManager.getModel(dtProject);
        modelChangeLanguage.execute(new AbstractBmTask<Void>("change mode")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject object = transaction.getTopObjectByFqn(CONFIGURATION.getName());
                assertTrue(object instanceof Configuration);
                Configuration config = (Configuration)object;
                config.setScriptVariant(ScriptVariant.RUSSIAN);
                assertTrue(config.getScriptVariant() == ScriptVariant.RUSSIAN);
                return null;
            }
        });
        waitForDD(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change mode")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                Form form = (Form)transaction.getTopObjectByFqn(FQN_FORM_RU);
                FormItem item = form.getItems().get(1);
                assertTrue(item instanceof Table);
                Table table = (Table)item;
                assertEquals("Ссылка", table.getItems().get(0).getName());
                FormField fieldRef = (FormField)table.getItems().get(0);
                fieldRef.getUserVisible().setCommon(false);
                return null;
            }
        });
        waitForDD(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM_RU, dtProject);
        assertTrue(object instanceof Form);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNull(marker);
    }
}
