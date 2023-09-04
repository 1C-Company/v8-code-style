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
import com.e1c.v8codestyle.bsl.check.IsInRoleMethodRoleExistCheck;

/**
 * The test for {@link IsInRoleMethodRoleExistCheck} class.
 * @author Vadim Goncharov
 */
public class IsInRoleMethodRoleExistCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "IsInRoleMethodRoleExist"; //$NON-NLS-1$

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/RolesCommonModule/Module.bsl"; //$NON-NLS-1$

    public IsInRoleMethodRoleExistCheckTest()
    {
        super(IsInRoleMethodRoleExistCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return COMMON_MODULE_FILE_NAME;
    }

    /**
     * Test invocation role check access exist role check.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIsInRoleMethodRoleExistCheck() throws Exception
    {

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());

        assertEquals("2", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("9", markers.get(1).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

}
