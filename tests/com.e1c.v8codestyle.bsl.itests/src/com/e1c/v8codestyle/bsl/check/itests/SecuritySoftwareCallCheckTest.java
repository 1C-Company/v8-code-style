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
import com.e1c.v8codestyle.bsl.check.SecuritySoftwareCallCheck;

/**
 * Tests for {@link SecuritySoftwareCallCheck} check.
 *
 * @author Ivan Sergeev
 */
public class SecuritySoftwareCallCheckTest
    extends AbstractSingleModuleTestBase
{
    public SecuritySoftwareCallCheckTest()
    {
        super(SecuritySoftwareCallCheck.class);
    }

    /**
     * Test launch secure.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSecureSoftareLaunch() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "secure-software-launch-secure.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test launch secure 2.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSecureSoftareLaunch2() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "secure-software-launch-secure2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test launch non secure.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNonSecureSoftareLaunch() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "secure-software-launch-non-secure.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test launch non secure 2.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNonSecureSoftareLaunch2() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "secure-software-launch-non-secure2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test launch secure off .
     *
     * @throws Exception the exception
     */
    @Test
    public void testSecureOffSoftareLaunch() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "secure-software-launch-non-secure2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test launch secure off 2.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSecureOffSoftareLaunch2() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "secure-software-launch-non-secure2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
