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
import com.e1c.v8codestyle.bsl.check.LocalizationNstrCheck;

/**
 * Tests for {@link LocalizationNstrCheck} check.
 *
 * @author Ivan Sergeev
 */
public class LocalizationNstrCheckTest
    extends AbstractSingleModuleTestBase
{

    public LocalizationNstrCheckTest()
    {
        super(LocalizationNstrCheck.class);
    }

    /**
     * Test incorrect message invocation.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectInvocation() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "nstr-localization-invocation.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect message invocation in if.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectInvocationIf() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "nstr-localization-invocation-if.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect message invocation in for.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectInvocationFor() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "nstr-localization-invocation-for.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test correct message invocation.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCorrectInvocation() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "nstr-localization-invocation-correct.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test correct message invocation in if.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCorrectInvocationIf() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "nstr-localization-invocation-correct-if.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test correct message invocation in for.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCorrectInvocationFor() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "nstr-localization-invocation-correct-for.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }
}
