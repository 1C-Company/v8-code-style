package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.SingleLetterVariableNameCheck;


/**
 * The test for class {@SingleLetterVariableNameCheck}.
 *
 * @author Vitaly Prolomov
 */
public class SingleLetterVariableNameCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PARAMETER_TEST_PATH = "single-letter-parameter-name.bsl";

    private static final String SINGLE_DECLARED_VARIABLE_TEST_PATH = "single-letter-declared-variable-name.bsl";

    private static final String INITIALIZED_VARIABLE_TEST_PATH = "single-letter-initialized-variable-name.bsl";

    private static final String LOOPS_COUNTERS_TEST_PATH = "single-letter-loop-counters-names.bsl";


    public SingleLetterVariableNameCheckTest()
    {
        super(SingleLetterVariableNameCheck.class);
    }


    /**
     * Test for case when parameter name is 1 letter long.
     *
     * @throws Exception
     */
    @Test
    public void testParameterName() throws Exception
    {
        // com.e1c.v8codestyle.bsl.itests/resources/single-letter-declared-variable-name.bsl
        updateModule(FOLDER_RESOURCE + PARAMETER_TEST_PATH);

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        Marker marker = markers.get(0);

        assertEquals("6", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test for case when initialized variable name is 1 letter long.
     *
     * @throws Exception
     */
    @Test
    public void testInitializedVariableName() throws Exception
    {
        updateModule(FOLDER_RESOURCE + INITIALIZED_VARIABLE_TEST_PATH);

        List<Marker> markers = getModuleMarkers();
        assertEquals(markers.size(), 1);

        Marker marker = markers.get(0);

        assertEquals("6", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test for case when declared variable name is 1 letter long.
     *
     * Different from previous test because declared and initialized variables need to
     * be checked separately.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeclaredVariableName() throws Exception
    {
        updateModule(FOLDER_RESOURCE + SINGLE_DECLARED_VARIABLE_TEST_PATH);

        List<Marker> markers = getModuleMarkers();
        assertEquals(markers.size(), 4);

        // First variable is single declared variable in single line.
        Marker firstMarker = markers.get(0);
        assertEquals("6", firstMarker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

        for (int i = 1; i < markers.size(); ++i)
        {
            assertEquals("7", markers.get(i).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        }
    }

    /*
     * As it is identified in description of this check, loop counters can
     * have a name of any length, hence short counters should not be marked.
     */
    @Test
    public void testLoopsCountersName() throws Exception
    {
        updateModule(FOLDER_RESOURCE + LOOPS_COUNTERS_TEST_PATH);

        List<Marker> markers = getModuleMarkers();
        assertEquals(markers.size(), 0);
    }
}
