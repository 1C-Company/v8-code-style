/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
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
public class NotifyDescriptionToServerProcedureCheck2Test
    extends AbstractSingleModuleTestBase
{

    public NotifyDescriptionToServerProcedureCheck2Test()
    {
        super(NotifyDescriptionToServerProcedureCheck.class);
    }

    /**
     * Test notify description to common module server procedure.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCommonModuleServerProcedure() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "notify-description-to-server-procedure2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(4, markers.size());

        Marker marker = markers.get(0);
        assertEquals("6", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

        marker = markers.get(1);
        assertEquals("8", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

        marker = markers.get(2);
        assertEquals("18", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

        marker = markers.get(3);
        assertEquals("20", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

}
