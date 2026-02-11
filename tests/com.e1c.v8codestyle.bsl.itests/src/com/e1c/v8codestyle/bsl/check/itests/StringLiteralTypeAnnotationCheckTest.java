/**
 * Copyright (C) 2025, 1C
 */
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.StringLiteralTypeAnnotationCheck;

/**
 * A class for testing {@link StringLiteralTypeAnnotationCheck}
 *
 * @author Babin Nikolay
 *
 */
public class StringLiteralTypeAnnotationCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "StringLiteralTypeAnnotation";

    private static final String MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

    public StringLiteralTypeAnnotationCheckTest()
    {
        super(StringLiteralTypeAnnotationCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return MODULE_FILE_NAME;
    }

    @Override
    protected String getModuleId()
    {
        return Path.ROOT.append(getTestConfigurationName()).append(MODULE_FILE_NAME).toString();
    }

    /**
     * Checks invalid annotations locations.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvalidAnnotationsLocationsMarkers() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        assertEquals(4, markers.size());

        assertEquals(Integer.valueOf(5), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(Integer.valueOf(10), markers.get(1).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(Integer.valueOf(11), markers.get(2).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(Integer.valueOf(13), markers.get(3).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
