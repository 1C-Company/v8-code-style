/**
 *
 */
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.NotifyDescriptionToServerProcedureCheck;

/**
 * The test of {@link NotifyDescriptionToServerProcedureCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class NotifyDescriptionToServerProcedureCheckTest
    extends AbstractSingleModuleTestBase
{

    public NotifyDescriptionToServerProcedureCheckTest()
    {
        super(NotifyDescriptionToServerProcedureCheck.class);
    }

    /**
     *
     * @throws Exception the exception
     */
    @Test
    public void testLocalServerProcedure() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "notify-description-to-server-procedure.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        Marker marker = markers.get(0);
        assertEquals("2", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

}
