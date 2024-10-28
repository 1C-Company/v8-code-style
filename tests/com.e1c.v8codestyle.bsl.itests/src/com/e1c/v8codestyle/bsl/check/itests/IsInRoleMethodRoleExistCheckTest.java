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
import java.util.stream.Collectors;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
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
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(2, 9), errorLines);
    }

}
