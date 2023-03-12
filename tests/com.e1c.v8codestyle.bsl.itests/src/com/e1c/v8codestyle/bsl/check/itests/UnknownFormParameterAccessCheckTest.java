package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.UnknownFormParameterAccessCheck;

/**
 * Test class for {@link UnknownFormParameterAccessCheck}.
 * @author Vadim Goncharov
 */
public class UnknownFormParameterAccessCheckTest
    extends AbstractSingleModuleTestBase
{
    
    private static final String PROJECT_NAME = "UnknownFormParameterAccess";
    
    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/TestForm/Module.bsl";
    
    
    public UnknownFormParameterAccessCheckTest()
    {
        super(UnknownFormParameterAccessCheck.class);
    }
    
    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return COMMON_FORM_FILE_NAME;
    }
    
    /**
     * Test the form module use unknown parameters access.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormModuleUnknownParametersAccess() throws Exception
    {

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());

        Marker marker = markers.get(0);
        assertEquals("8", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

        marker = markers.get(1);
        assertEquals("10", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
