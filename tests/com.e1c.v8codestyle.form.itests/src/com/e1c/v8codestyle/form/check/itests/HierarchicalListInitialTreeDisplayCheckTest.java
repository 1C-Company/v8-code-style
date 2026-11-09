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

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.form.check.HierarchicalListInitialTreeDisplayCheck;

/**
 * Tests for {@link HierarchicalListInitialTreeDisplayCheck} check.
 *
 * @author Artem Samohvalov
 */
public class HierarchicalListInitialTreeDisplayCheckTest
    extends AbstractFormMarkerTestBase
{
    private static final String PROJECT_NAME = "HierarchicalListInitialTreeDisplay";

    private static final String FQN_FORM = "Catalog.Products.Form.ListForm.Form";
    private static final String FQN_FORM2 = "Catalog.Products.Form.ListForm3.Form";
    private static final String FQN_FORM3 = "Catalog.Products.Form.ListForm2.Form";

    /**
    * Test issue
    * InitialTreeView = EXPAND_ALL_LEVELS
    *
    * @throws Exception the exception
    */
    @Test
    public void testExpandAllLevels() throws Exception
    {
        List<Marker> markers = getItemMarkersByFQN(FQN_FORM);
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
        List<Marker> markers = getItemMarkersByFQN(FQN_FORM2);
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
        List<Marker> markers = getItemMarkersByFQN(FQN_FORM3);
        assertTrue(markers.isEmpty());
    }

    @Override
    protected String getCheckId()
    {
        return "hierarchical-list-initial-tree-display";
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }
}
