/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.form.check.HierarchicalListInitialTreeDisplayCheck;

/**
 * Tests for {@link HierarchicalListInitialTreeDisplayCheck} check.
 *
 * @author Artem Samohvalov
 */
public class HierarchicalListInitialTreeDisplayCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "hierarchical-list-initial-tree-display";
    private static final String PROJECT_NAME = "HierarchicalListInitialTreeDisplay";

    private static final String FQN_FORM = "Catalog.Products.Form.ListForm.Form";
    private static final String FQN_FORM2 = "Catalog.Products.Form.ListForm3.Form";
    private static final String FQN_FORM3 = "Catalog.Products.Form.ListForm2.Form";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
    * Test issue
    * InitialTreeView = EXPAND_ALL_LEVELS
    *
    * @throws Exception the exception
    */
    @Test
    public void testExpandAllLevels() throws Exception
    {
        IBmObject object = getTopObjectByFqn(FQN_FORM, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        var markers = getMarkers(form);
        assertFalse(markers.isEmpty());
    }

    /**
    * Test no issue
    * InitialTreeView = EXPAND_ALL_LEVELS and List not Hierarchical
    *
    * @throws Exception the exception
    */
    @Test
    public void testExpandAllLevelsAndNoHierarchical() throws Exception
    {
        IBmObject object = getTopObjectByFqn(FQN_FORM2, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        var markers = getMarkers(form);
        assertTrue(markers.isEmpty());
    }

    /**
    * Test no issue
    * InitialTreeView = NO_EXPAND
    *
    * @throws Exception the exception
    */
    @Test
    public void testNoExpand() throws Exception
    {
        IBmObject object = getTopObjectByFqn(FQN_FORM3, getProject());
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        var markers = getMarkers(form);
        assertTrue(markers.isEmpty());
    }

    private List<Marker> getMarkers(Form form)
    {
        List<Marker> markers = new ArrayList<>();
        for (FormItem item : form.getItems())
        {
            if (item instanceof Table table)
            {
                Marker marker = getFirstMarker(CHECK_ID, table, getProject());

                if (marker != null)
                {
                    markers.add(marker);
                }
            }
        }
        return markers;
    }
}
