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
import com.e1c.v8codestyle.bsl.check.ApplicationLaunchSecurityCheck;

/**
 * Tests for {@link ApplicationLaunchSecurityCheck} check.
 *
 * @author Ivan Sergeev
 */
public class ApplicationLaunchSecurityCheckTest
    extends AbstractSingleModuleTestBase
{

    public ApplicationLaunchSecurityCheckTest()
    {
        super(ApplicationLaunchSecurityCheck.class);
    }

    /**
     * Test unsafe launch.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUnsafeLaunch() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "application-unsafe-launch.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test unsafe launch.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUnsafeLaunchEn() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "application-unsafe-launch-en.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call before another call.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUnsafeLaunchBefore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "application-unsafe-before.bsl");

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
    public void testUnsafeLaunchAfter() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "application-unsafe-after.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test safe launch.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSafeLaunch() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "application-safe-launch.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test safe launch OperatorStyleCreator.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSafeLaunchOperatorStyleCreator() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "application-safe-launch-OSC.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test unsafe launch OperatorStyleCreator.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUnsafeLaunchOperatorStyleCreator() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "application-unsafe-launch-OSC.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
