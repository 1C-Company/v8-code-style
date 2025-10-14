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
import com.e1c.v8codestyle.bsl.check.SelfAssignCheck;

/**
 * Tests for {@link SelfAssignCheck} check.
 *
 * @author Ivan Sergeev
 */
public class SelfAssignCheckTest
    extends AbstractSingleModuleTestBase
{

    public SelfAssignCheckTest()
    {
        super(SelfAssignCheck.class);
    }

    /**
     * Test variable self assign.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVaribleSelfAssigne() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "self-assigne-variable.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test variable after another variable.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVariableAfter() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "self-assigne-variable-after.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test variable before another variable.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVariablerBefore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "self-assigne-variable-before.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test variable no self assign.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVaribleNoSelfAssigne() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "non-self-assigne-variable.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }
}
