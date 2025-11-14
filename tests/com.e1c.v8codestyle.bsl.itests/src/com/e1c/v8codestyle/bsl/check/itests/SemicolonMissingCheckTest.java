/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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
import com.e1c.v8codestyle.bsl.check.SemicolonMissingCheck;

/**
 * Tests for {@link SemicolonMissingCheck} check.
 *
 * @author Ivan Sergeev
 */
public class SemicolonMissingCheckTest
    extends AbstractSingleModuleTestBase
{

    public SemicolonMissingCheckTest()
    {
        super(SemicolonMissingCheck.class);
    }

    /**
     * Test statement missing semicolon.
     *
     * @throws Exception the exception
     */
    @Test
    public void testStatementMissingSemicolon() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "missing-semicolon-statement.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test statement after another statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testStatementAfter() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "missing-semicolon-statement-after.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test statement before another statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testStatementBefore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "missing-semicolon-statement-before.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(7), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test statement sub statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testStatementSubStatement() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "missing-semicolon-statement-sub-statement.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

    }

    /**
     * Test statement no missing semicolon.
     *
     * @throws Exception the exception
     */
    @Test
    public void testStatementNoMissingSemicolon() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "non-missing-semicolon-statement.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }
}
