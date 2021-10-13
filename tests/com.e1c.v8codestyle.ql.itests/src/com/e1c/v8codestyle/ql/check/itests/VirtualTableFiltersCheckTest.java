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

import org.junit.Test;

import com.e1c.v8codestyle.ql.check.VirtualTableFiltersCheck;
import com.e1c.v8codestyle.ql.check.itests.TestingQlResultAcceptor.QueryMarker;

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
        List<QueryMarker> markers = getQueryMarkers();
        assertEquals(2, markers.size());

        QueryMarker marker = markers.get(0);
        assertNotNull(marker.getTarget());
        assertEquals(10, marker.getLineNumber());

        marker = markers.get(1);
        assertNotNull(marker.getTarget());
        assertEquals(11, marker.getLineNumber());
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
        List<QueryMarker> markers = getQueryMarkers();
        assertTrue(markers.isEmpty());
    }
}
