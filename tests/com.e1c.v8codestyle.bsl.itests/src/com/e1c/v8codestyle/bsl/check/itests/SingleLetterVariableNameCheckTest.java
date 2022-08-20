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

    private static final String parameterTestPath = "single-letter-parameter-name.bsl";

    private static final String declaredVariableTestPath = "single-letter-declared-variable-name.bsl";

    private static final String initializedVariableTestPath = "single-letter-initialized-variable-name.bsl";

    private static final String message = "Variable has a single letter name";


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
    public void testParameterNameIncorrect() throws Exception
    {
        // com.e1c.v8codestyle.bsl.itests/resources/single-letter-declared-variable-name.bsl
        updateModule(FOLDER_RESOURCE + parameterTestPath);

        List<Marker> markers = getModuleMarkers();
        assertEquals(markers.size(), 1);

        Marker marker = markers.get(0);

        assertEquals(message, marker.getMessage());

        String mistakeLineIndex = "8";
        assertEquals(mistakeLineIndex, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test for case when initialized variable name is 1 letter long.
     *
     * @throws Exception
     */
    @Test
    public void testInitializedVariableNameCorrect() throws Exception
    {
        updateModule(FOLDER_RESOURCE + initializedVariableTestPath);

        List<Marker> markers = getModuleMarkers();
        assertEquals(markers.size(), 1);

        Marker marker = markers.get(0);

        assertEquals(message, marker.getMessage());

        String mistakeLineIndex = "8";
        assertEquals(mistakeLineIndex, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
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
    public void testDeclaredVariableNameCorrect() throws Exception
    {
        updateModule(FOLDER_RESOURCE + declaredVariableTestPath);

        List<Marker> markers = getModuleMarkers();
        assertEquals(markers.size(), 1);

        Marker marker = markers.get(0);

        assertEquals(message, marker.getMessage());

        String mistakeLineIndex = "8";
        assertEquals(mistakeLineIndex, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
