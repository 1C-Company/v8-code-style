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
 *     Viktor Gukov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.EmptyExceptStatementCheck;

/**
 * Tests for {@link EmptyExceptStatementCheck} check.
 *
 * @author Viktor Gukov
 *
 */
public class EmptyExceptStatementCheckTest
    extends AbstractSingleModuleTestBase
{

    public EmptyExceptStatementCheckTest()
    {
        super(EmptyExceptStatementCheck.class);
    }

    /**
     * Test common module methods for empty except statements
     *
     * @throws Exception
     */
    @Test
    public void testEmptyExceptStatement() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "empty-except-statement.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("2", marker.getExtraInfo().get("line"));

    }
}
