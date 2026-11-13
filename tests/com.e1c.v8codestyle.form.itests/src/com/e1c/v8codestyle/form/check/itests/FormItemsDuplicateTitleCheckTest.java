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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.form.check.FormItemsDuplicateTitleCheck;

/**
 * The test for {@link FormItemsDuplicateTitleCheck} check.
 * 
 * @author Artem Samohvalov
 */
public class FormItemsDuplicateTitleCheckTest
    extends AbstractFormMarkerTestBase
{
    private static final String PROJECT_NAME = "FormItemsDuplicateTitle";

    private static final String FQN_ITEMS_FORM = "Catalog.Products.Form.ItemsForm.Form";
    private static final String FQN_COMMAND_BAR_FORM = "Catalog.Products.Form.CommandBarForm.Form";
    private static final String FQN_NO_DUPLICATES_FORM = "Catalog.Products.Form.NoDuplicatesForm.Form";
    private static final String FQN_EMPTY_TITLES_FORM = "Catalog.Products.Form.EmptyTitlesForm.Form";
    private static final String FQN_DIFFERENT_GROUPS_FORM = "Catalog.Products.Form.DifferentGroupsForm.Form";
    private static final String FQN_SAME_GROUP_FORM = "Catalog.Products.Form.SameGroupForm.Form";
    private static final String FQN_CONTEXT_MENU_FORM = "Catalog.Products.Form.ContextMenuForm.Form";

    /**
     * Test error.
     * Test same titles.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSameTitles() throws Exception
    {
        List<Marker> markers = getFormNestedMarkersByFQN(FQN_ITEMS_FORM);
        assertEquals(2, markers.size());
    }

    /**
     * Test error.
     * Test same titles in command bar.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSameTitlesInCommandBar() throws Exception
    {
        List<Marker> markers = getFormNestedMarkersByFQN(FQN_COMMAND_BAR_FORM);
        assertEquals(2, markers.size());
    }

    /**
     * Test same group.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSameGroup() throws Exception
    {
        List<Marker> markers = getFormNestedMarkersByFQN(FQN_SAME_GROUP_FORM);
        assertEquals(2, markers.size());
    }

    /**
     * Test context menu.
     *
     * @throws Exception the exception
     */
    @Test
    public void testContextMenu() throws Exception
    {
        List<Marker> markers = getFormNestedMarkersByFQN(FQN_CONTEXT_MENU_FORM);
        assertEquals(1, markers.size());
    }

    /**
     * Test no error.
     * Test no duplicates.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoDuplicates() throws Exception
    {
        List<Marker> markers = getFormNestedMarkersByFQN(FQN_NO_DUPLICATES_FORM);
        assertTrue(markers.isEmpty());
    }

    /**
     * Test no error.
     * Test empty titles form.
     *
     * @throws Exception the exception
     */
    @Test
    public void testEmptyTitles() throws Exception
    {
        List<Marker> markers = getFormNestedMarkersByFQN(FQN_EMPTY_TITLES_FORM);
        assertTrue(markers.isEmpty());
    }

    /**
     * Test no error.
     * Test different groups form.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDifferentGroups() throws Exception
    {
        List<Marker> markers = getFormNestedMarkersByFQN(FQN_DIFFERENT_GROUPS_FORM);
        assertTrue(markers.isEmpty());
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getCheckId()
    {
        return "form-items-duplicate-title";
    }
}
