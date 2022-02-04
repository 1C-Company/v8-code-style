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

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.IsInRoleCheck;

/**
 *  Tests for {@link IsInRoleCheck} check.
 *
 * @author Artem Iliukhin
 */
public class IsInRoleCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String USING_IS_IN_ROLE_METHOD = "Use the AccessRight() function instead of IsInRole()";

    public IsInRoleCheckTest()
    {
        super(IsInRoleCheck.class);
    }

    /**
     * Test IsInRole method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIsInRoleMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "isinrole-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(USING_IS_IN_ROLE_METHOD, marker.getMessage());
    }
}
