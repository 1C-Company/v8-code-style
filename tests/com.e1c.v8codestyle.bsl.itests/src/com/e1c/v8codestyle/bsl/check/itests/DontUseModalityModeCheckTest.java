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
import com.e1c.v8codestyle.bsl.check.DontUseModalityModeCheck;

/**
 * Tests for {@link DontUseModalityModeCheck} check.
 *
 * @author Ivan Sergeev
 */
public class DontUseModalityModeCheckTest
    extends AbstractSingleModuleTestBase
{

    public DontUseModalityModeCheckTest()
    {
        super(DontUseModalityModeCheck.class);
    }

    /**
     * Test call modality.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModalityCall() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "modality-call.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call after another call.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModalityCallAfter() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "modality-call-after.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call after before another call.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModalityCallBefore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "modality-call-before.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call non modality.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNonModalityCall() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "non-modality-call.bsl");
        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test call modality.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModalityCallEn() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "modality-call-en.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call after another call.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModalityCallAfterEn() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "modality-call-after-en.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call after before another call.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModalityCallBeforeEn() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "modality-call-before-en.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call non modality.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNonModalityCallEn() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "non-modality-call-en.bsl");
        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }
}
