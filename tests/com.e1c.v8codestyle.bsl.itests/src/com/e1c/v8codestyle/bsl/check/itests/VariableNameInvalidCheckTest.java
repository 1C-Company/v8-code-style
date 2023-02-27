package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.VariableNameInvalidCheck;

/**
 * The test for class {@link VariableNameInvalidCheck}.
 *
 * @author Vadim Goncharov
 *
 */
public class VariableNameInvalidCheckTest
    extends AbstractSingleModuleTestBase
{

    public VariableNameInvalidCheckTest()
    {
        super(VariableNameInvalidCheck.class);
    }

    /**
     * Test use invalid variable name.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVariableName() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "bsl-variable-name-invalid.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(6, markers.size());

        List<String> benchmarkLines = Arrays.asList("2", "3", "7", "8", "12", "13");

        List<String> markersLines = new ArrayList<>();
        for (Marker m : markers)
        {
            markersLines.add(m.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        }

        assertEquals(true, markersLines.containsAll(benchmarkLines) && benchmarkLines.containsAll(markersLines));

    }

}
