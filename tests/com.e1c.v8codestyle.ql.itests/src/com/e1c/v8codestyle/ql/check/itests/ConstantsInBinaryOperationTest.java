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
 *     Denis Maslennikov - issue #1090
 *******************************************************************************/
/**
 *
 */
package com.e1c.v8codestyle.ql.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.e1c.v8codestyle.ql.check.ConstantsInBinaryOperationCheck;
import com.e1c.v8codestyle.ql.check.itests.TestingQlResultAcceptor.QueryMarker;

/**
 * The test for class {@link ConstantsInBinaryOperationCheck}.
 *
 * @author Denis Maslennikov
 */
public class ConstantsInBinaryOperationTest
    extends AbstractQueryTestBase
{
    private static final String PROJECT_NAME = "QlFullDemo";
    private static final String FOLDER = FOLDER_RESOURCE + "ql-constants-in-binary-operation/";

    public ConstantsInBinaryOperationTest()
    {
        super(ConstantsInBinaryOperationCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test correct using binary operation in query.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConstantsInBinaryOperationCompliant() throws Exception
    {
        loadQueryAndValidate(FOLDER + "compliant.ql");
        List<QueryMarker> markers = getQueryMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test using binary operation in query with two constants.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConstantsInBinaryOperationNonCompliant() throws Exception
    {
        loadQueryAndValidate(FOLDER + "non-compliant.ql");
        List<QueryMarker> markers = getQueryMarkers();
        assertEquals(2, markers.size());

        QueryMarker marker = markers.get(0);
        assertNotNull(marker.getTarget());
        assertEquals(3, marker.getLineNumber());

        marker = markers.get(1);
        assertNotNull(marker.getTarget());
        assertEquals(13, marker.getLineNumber());
    }
}
