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
package com.e1c.v8codestyle.bsl.comment.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.FunctionReturnSectionCheck;

/**
 * Tests for {@link FunctionReturnSectionCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class FunctionReturnSectionCheckTest
    extends AbstractSingleModuleTestBase
{

    public FunctionReturnSectionCheckTest()
    {
        super(FunctionReturnSectionCheck.class);
    }

    /**
     * Test the documentation comment return section has valid types or ref link to function
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvalidFunctionReturnTypes() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-return-section-type.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(3, markers.size());
        Marker marker = markers.get(0);
        assertEquals("2", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        marker = markers.get(1);
        assertEquals("10", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        marker = markers.get(2);
        assertEquals("16", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

    }
}
