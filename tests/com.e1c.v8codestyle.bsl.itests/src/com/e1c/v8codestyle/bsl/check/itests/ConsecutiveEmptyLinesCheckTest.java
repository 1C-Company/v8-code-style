/**
 * Copyright (C) 2022, 1C
 */
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.ConsecutiveEmptyLinesCheck;

/**
 * Tests for {@link ConsecutiveEmptyLinesCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ConsecutiveEmptyLinesCheckTest
    extends AbstractSingleModuleTestBase
{

    public ConsecutiveEmptyLinesCheckTest()
    {
        super(ConsecutiveEmptyLinesCheck.class);
    }

    @Test
    public void testWithManyEmptyLines() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "empty-lines.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());
        assertEquals(Integer.valueOf(3), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testWithOneEmptyLine() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "one-empty-line.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

}
