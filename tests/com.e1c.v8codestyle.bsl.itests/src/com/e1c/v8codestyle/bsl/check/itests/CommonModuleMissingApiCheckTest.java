/**
 * Copyright (C) 2022, 1C
 */
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.CommonModuleMissingApiCheck;

/**
 * Tests for {@link CommonModuleMissingApiCheck} check
 *
 * @author Artem Iliukhin
 */
public class CommonModuleMissingApiCheckTest
    extends AbstractSingleModuleTestBase
{
    public CommonModuleMissingApiCheckTest()
    {
        super(CommonModuleMissingApiCheck.class);
    }

    @Test
    public void testProgrammingInterface() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "common-module-missing-api.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);

        assertEquals("2", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

}
