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
 *     Denis Maslennikov - issue #163
 *******************************************************************************/
/**
 *
 */
package com.e1c.v8codestyle.ql.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.e1c.v8codestyle.ql.check.QueryFieldIsNullCheck;
import com.e1c.v8codestyle.ql.check.itests.TestingQlResultAcceptor.QueryMarker;

/**
 * The test for class {@link QueryFieldIsNullCheck}.
 *
 * @author Denis Maslennikov
 */
public class QueryFieldIsNullTest
    extends AbstractQueryTestBase
{
    private static final String PROJECT_NAME = "QlFullDemo";
    private static final String FOLDER = FOLDER_RESOURCE + "ql-query-field-isnull/";

    public QueryFieldIsNullTest()
    {
        super(QueryFieldIsNullCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test correct ISNULL check for query field.
     *
     * @throws Exception the exception
     */
    @Test
    public void testQueryFieldIsNullCompliant() throws Exception
    {
        loadQueryAndValidate(FOLDER + "compliant.ql");
        List<QueryMarker> markers = getQueryMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test if ISNULL check is absent for query field.
     *
     * @throws Exception the exception
     */
    @Test
    public void testQueryFieldIsNullNonCompliant() throws Exception
    {
        loadQueryAndValidate(FOLDER + "non-compliant.ql");
        List<QueryMarker> markers = getQueryMarkers();
        assertEquals(3, markers.size());

        QueryMarker marker = markers.get(0);
        assertNotNull(marker.getTarget());
        assertEquals(11, marker.getLineNumber());

        marker = markers.get(1);
        assertNotNull(marker.getTarget());
        assertEquals(23, marker.getLineNumber());

        marker = markers.get(2);
        assertNotNull(marker.getTarget());
        assertEquals(35, marker.getLineNumber());

    }
}
