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
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.ModuleUnusedMethodCheck;

/**
 * Tests for the {@link ModuleUnusedMethodCheck}
 *
 * @author Andrey Volkov
 * @author Dmitriy Marmyshev
 */
public class ModuleUnusedMethodCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String CONFIG_MODULE_FILE_NAME = "/src/Configuration/ManagedApplicationModule.bsl";

    public ModuleUnusedMethodCheckTest()
    {
        super(ModuleUnusedMethodCheck.class);
    }

    @Test
    public void testSingleCheckMarker() throws Exception
    {

        updateModule(FOLDER_RESOURCE + "module-unused-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Override
    protected String getModuleFileName()
    {
        return CONFIG_MODULE_FILE_NAME;
    }
}
