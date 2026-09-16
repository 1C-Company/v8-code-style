/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.OverridableModuleOnlyExportMethodsCheck;

/**
 *  Test for {@link OverridableModuleOnlyExportMethodsCheck}
 *
 *  @author Artem Samohvalov
 */
public class OverridableModuleOnlyExportMethodsCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "CommonModuleOverridable";

    public OverridableModuleOnlyExportMethodsCheckTest()
    {
        super(OverridableModuleOnlyExportMethodsCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * test no error
     * 
     * @throws Exception
     */
    @Test
    public void testExportFunction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "overridable-module-export-function.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * test error
     * 
     * @throws Exception
     */
    @Test
    public void testNotExportFunction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "overridable-module-not-export-function.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());

        assertEquals(Integer.valueOf(1), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
