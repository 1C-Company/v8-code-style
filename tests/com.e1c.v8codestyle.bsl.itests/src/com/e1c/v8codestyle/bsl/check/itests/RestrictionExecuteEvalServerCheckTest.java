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
import com.e1c.v8codestyle.bsl.check.RestrictionExecuteEvalServerCheck;

/**
 * Tests for {@link RestrictionExecuteEvalServerCheck} check.
 *
 * @author Ivan Sergeev
 */
public class RestrictionExecuteEvalServerCheckTest
    extends AbstractSingleModuleTestBase
{

    public RestrictionExecuteEvalServerCheckTest()
    {
        super(RestrictionExecuteEvalServerCheck.class);
    }

    /**
     * Test call on server.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallOnServer() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-server.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call on server with safe mode.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallOnServerWithSafeMode() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-server-safe-mode.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test call on server with safe mode ru.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallOnServerWithSafeModeRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-server-safe-mode-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test call on server with safe mode false.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallOnServerWithSafeModeFalse() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-server-safe-mode-false.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call on server with safe mode false.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallOnServerWithSafeModeFalseRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-server-safe-mode-false-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call on client.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallOnClient() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-client.bsl");
        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test call on server.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallOnServerRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-server.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call on server ru.
     *
     * @throws Exception the exception
     */
    @Test
    public void testEvalCallOnServer() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "eval-call-on-server-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call on server ru.
     *
     * @throws Exception the exception
     */
    @Test
    public void testEvalCallOnServerRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "eval-call-on-server-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test call after another statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallWithSafeModeInIf() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-server-if-statement.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test call on server in if statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCallWithSafeModeInIf2() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "execute-call-on-server-if-statement2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

}
