package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
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
        assertEquals(7, markers.size());
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(2, 3, 7, 8, 12, 13, 36), errorLines);
    }

}
