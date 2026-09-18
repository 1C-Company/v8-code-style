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
import com.e1c.v8codestyle.bsl.check.DefinitionTypeVariableCheck;

/**
 * Tests for {@link DefinitionTypeVariableCheck} check.
 *
 * @author Ivan Sergeev
 */
public class DefinitionTypeVariableCheckTest
    extends AbstractSingleModuleTestBase
{

    public DefinitionTypeVariableCheckTest()
    {
        super(DefinitionTypeVariableCheck.class);
    }

    @Test
    public void testDefinitionTypeVariableIncorrect() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "definition-type-incorrect.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testDefinitionTypeVariableIncorrect2() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "definition-type-incorrect2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testDefinitionTypeVariableCorrect() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "definition-type-correct.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testDefinitionTypeVariableCorrect2() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "definition-type-correct2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }
}
