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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.EmptyQueryResultCheck;

/**
 * Tests for {@link EmptyQueryResultCheck} check.
 *
 * @author Ivan Sergeev
 */
public class EmptyQueryResultCheckTest
    extends AbstractSingleModuleTestBase
{

    public EmptyQueryResultCheckTest()
    {
        super(EmptyQueryResultCheck.class);
    }

    @Test
    public void testIncorrectCheckQueryResult() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "empty-query-result-incorrect.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(8), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testIncorrectCheckQueryResultIf() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "empty-query-result-incorrect2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(9), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testIncorrectCheckQueryResultFor() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "empty-query-result-incorrect3.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(9), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testIncorrectCheckQueryResultTryCath() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "empty-query-result-incorrect4.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(9), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testCorrectCheckQueryResult() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "empty-query-result-correct.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }
}
