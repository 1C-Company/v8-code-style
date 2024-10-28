/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.AttachableEventHandlerNameCheck;

/**
 * Tests for {@link AttachableEventHandlerNameCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class AttachableEventHandlerNameCheckTest
    extends AbstractSingleModuleTestBase
{

    public AttachableEventHandlerNameCheckTest()
    {
        super(AttachableEventHandlerNameCheck.class);
    }

    /**
     * Test attachable event handler name.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAttachableEventHandlerName() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-attachable-event-handler-name.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
