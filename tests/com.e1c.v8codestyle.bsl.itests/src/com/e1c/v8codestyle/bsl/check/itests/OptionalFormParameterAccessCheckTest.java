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
import com.e1c.v8codestyle.bsl.check.OptionalFormParameterAccessCheck;

/**
 * Test class for {@link OptionalFormParameterAccessCheck}
 */
public class OptionalFormParameterAccessCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "OptionalFormParameterAccess";

    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/TestForm/Module.bsl";

    public OptionalFormParameterAccessCheckTest()
    {
        super(OptionalFormParameterAccessCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return COMMON_FORM_FILE_NAME;
    }

    /**
     * Test the Parameters.Property("") access to exist form parameter.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOptionalFormParameterAccess() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        Marker marker = markers.get(0);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

}
