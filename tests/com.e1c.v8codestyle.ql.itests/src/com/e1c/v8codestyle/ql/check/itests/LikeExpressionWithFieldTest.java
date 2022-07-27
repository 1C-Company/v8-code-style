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
 *     Denis Maslennikov - issue #86
 *******************************************************************************/
/**
 *
 */
package com.e1c.v8codestyle.ql.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.e1c.v8codestyle.ql.check.LikeExpressionWithFieldCheck;
import com.e1c.v8codestyle.ql.check.itests.TestingQlResultAcceptor.QueryMarker;

/**
 * The test for class {@link LikeExpressionWithFieldCheck}.
 *
 * @author Denis Maslennikov
 */
public class LikeExpressionWithFieldTest
    extends AbstractQueryTestBase
{
    private static final String PROJECT_NAME = "QlFullDemo";
    private static final String FOLDER = FOLDER_RESOURCE + "ql-like-expression-with-field/";

    public LikeExpressionWithFieldTest()
    {
        super(LikeExpressionWithFieldCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test correct right operand of LIKE statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testLikeExpressionWithFieldCompliant() throws Exception
    {
        loadQueryAndValidate(FOLDER + "compliant.ql");
        List<QueryMarker> markers = getQueryMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test incorrect right operand of LIKE statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testikeExpressionWithFieldNonCompliant() throws Exception
    {
        loadQueryAndValidate(FOLDER + "non-compliant.ql");
        List<QueryMarker> markers = getQueryMarkers();
        assertEquals(8, markers.size());

        QueryMarker marker = markers.get(0);
        assertNotNull(marker.getTarget());
        assertEquals(6, marker.getLineNumber());

        marker = markers.get(1);
        assertNotNull(marker.getTarget());
        assertEquals(14, marker.getLineNumber());

        marker = markers.get(2);
        assertNotNull(marker.getTarget());
        assertEquals(22, marker.getLineNumber());

        marker = markers.get(3);
        assertNotNull(marker.getTarget());
        assertEquals(22, marker.getLineNumber());

        marker = markers.get(4);
        assertNotNull(marker.getTarget());
        assertEquals(30, marker.getLineNumber());

        marker = markers.get(5);
        assertNotNull(marker.getTarget());
        assertEquals(30, marker.getLineNumber());

        marker = markers.get(6);
        assertNotNull(marker.getTarget());
        assertEquals(38, marker.getLineNumber());

        marker = markers.get(7);
        assertNotNull(marker.getTarget());
        assertEquals(46, marker.getLineNumber());
    }
}
