package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
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
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(3, 5), errorLines);

    }

}
