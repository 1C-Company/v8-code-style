/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.FindRowServerCheck;

/**
 * Tests for {@link FindRowServerCheck} check.
 *
 * @author Ivan Sergeev
 */
public class FindRowServerCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "FindRowServerCheckTest";

    private static final String FORM_MODULE_FILE_NAME = "/src/Catalogs/Catalog/Forms/ItemForm/Module.bsl";

    public FindRowServerCheckTest()
    {
        super(FindRowServerCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return FORM_MODULE_FILE_NAME;
    }

    @Test
    public void testCallOnClient() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "findrow-onserver-incorrect.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testCallOnServer() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "findrow-onserver-correct.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }

    @Test
    public void testCallOnServerNoPragma() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "findrow-onserver-correct2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }
}
