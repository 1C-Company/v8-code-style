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
package com.e1c.v8codestyle.ql.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.util.Triple;
import org.junit.Test;

import com.e1c.v8codestyle.ql.check.VirtualTableFiltersCheck;

/**
 * The test for {@link VirtualTableFiltersCheck} check
 *
 * @author Dmitriy Marmyshev
 */
public class VirtualTableFiltersCheckTest
    extends AbstractQueryTestBase
{
    private static final String PROJECT_NAME = "QlFullDemo";

    public VirtualTableFiltersCheckTest()
    {
        super(VirtualTableFiltersCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test virtual table dimension in filters that is non-compliant query.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVirtualTableDimensionInFilters() throws Exception
    {
        loadQueryAndValidate(FOLDER_RESOURCE + "ql-virtual-table-filters.ql");
        List<Triple<String, EObject, EStructuralFeature>> markers = getQueryMarkers();
        assertEquals(2, markers.size());

        EObject object = markers.get(0).getSecond();
        assertNotNull(object);
        ICompositeNode node = NodeModelUtils.getNode(object);
        assertEquals(10, NodeModelUtils.getLineAndColumn(node.getRootNode(), node.getOffset()).getLine());

        object = markers.get(1).getSecond();
        assertNotNull(object);
        node = NodeModelUtils.getNode(object);
        assertEquals(11, NodeModelUtils.getLineAndColumn(node.getRootNode(), node.getOffset()).getLine());
    }

    /**
     * Test virtual table dimension in parameters of the virtual table that is compliant query.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVirtualTableDimensionInParameters() throws Exception
    {
        loadQueryAndValidate(FOLDER_RESOURCE + "ql-virtual-table-filters-compliant.ql");
        List<Triple<String, EObject, EStructuralFeature>> markers = getQueryMarkers();
        assertTrue(markers.isEmpty());
    }
}
