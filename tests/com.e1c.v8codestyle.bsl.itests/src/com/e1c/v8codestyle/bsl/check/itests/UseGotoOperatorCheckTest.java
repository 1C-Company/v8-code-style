package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.UseGotoOperatorCheck;

/**
 * Tests for {@link UseGotoOperatorCheck} check.
 *
 * @author Vadim Goncharov
 */
public class UseGotoOperatorCheckTest
    extends AbstractSingleModuleTestBase
{

    public UseGotoOperatorCheckTest()
    {
        super(UseGotoOperatorCheck.class);
    }
    
    
    /**
     * Test the module use goto operator and labeled statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUseGotoOperator() throws Exception
    {
        
        updateModule(FOLDER_RESOURCE + "use-goto-operator.bsl");
        
        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        
        Marker marker = markers.get(0);
        assertEquals("3", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        
        marker = markers.get(1);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        
    }
    
}
