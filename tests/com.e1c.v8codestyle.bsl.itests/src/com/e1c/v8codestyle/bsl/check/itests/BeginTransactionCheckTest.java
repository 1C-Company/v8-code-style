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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.BeginTransactionCheck;

/**
 * Tests for {@link BeginTransactionCheck} check.
 *
 * @author Artem Iliukhin
 */
public class BeginTransactionCheckTest
    extends AbstractSingleModuleTestBase
{

    /**
     * Instantiates a new commit transaction check test.
     */
    public BeginTransactionCheckTest()
    {
        super(BeginTransactionCheck.class);
    }


    @Test
    public void testNotFoundTryAfterBegin() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "not-found-try-after-begin.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("The try operator was not found after calling begin transaction", marker.getMessage());
    }

    @Test
    public void testShouldBeNoExecutableCodebetweenBeginAndTry() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "should-be-no-executable-code-between-begin-and-try.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("There should be no executable code between begin transaction and try", marker.getMessage());
    }
}
