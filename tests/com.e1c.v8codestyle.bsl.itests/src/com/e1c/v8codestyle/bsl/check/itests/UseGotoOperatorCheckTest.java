package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
        
        Set<String> testMarkersList = Set.of("3", "5");
        Set<String> projectMarkersList = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        
        for (Marker marker : markers)
        {
            projectMarkersList.add(marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        }
        
        assertTrue(testMarkersList.equals(projectMarkersList));

    }

}
