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
import com.e1c.v8codestyle.bsl.check.NewColorCheck;

/**
 * Tests for {@link NewColorCheck} check.
 *
 * @author Artem Iliukhin
 */
public class NewColorCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String NEW_COLOR =
        "To change the design, you should use style elements, and not set specific values directly in the controls";

    public NewColorCheckTest()
    {
        super(NewColorCheck.class);
    }

    @Test
    public void testNewColorO() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "new-color.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(NEW_COLOR, marker.getMessage());
    }

    @Test
    public void testNewColorF() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "new-color2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(NEW_COLOR, marker.getMessage());
    }

    @Test
    public void testNewColor3() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "new-color3.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    @Test
    public void testNewColor4() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "new-color4.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(NEW_COLOR, marker.getMessage());
    }
}
