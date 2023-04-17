/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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
import com.e1c.v8codestyle.bsl.check.MethodOptionalParameterBeforeRequiredCheck;

/**
 * Test for the class {@link MethodOptionalParameterBeforeRequiredCheck}.
 * @author Vadim Goncharov
 */
public class MethodOptionalParameterBeforeRequiredCheckTest
    extends AbstractSingleModuleTestBase
{

    public MethodOptionalParameterBeforeRequiredCheckTest()
    {
        super(MethodOptionalParameterBeforeRequiredCheck.class);
    }

    /**
     * Test optional param before require.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOptionalParamBeforeRequire() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "method-optional-parameter-before-required.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());

        assertEquals("1", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("10", markers.get(1).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

}
