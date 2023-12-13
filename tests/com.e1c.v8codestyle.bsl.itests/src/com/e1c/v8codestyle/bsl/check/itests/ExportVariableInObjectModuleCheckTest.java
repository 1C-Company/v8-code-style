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

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.ExportVariableInObjectModuleCheck;

/**
 * Tests for {@link ExportVariableInObjectModuleCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ExportVariableInObjectModuleCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "CatalogModules";

    private static final String MODULE_FILE_NAME = "/src/Catalogs/TestCatalog/ObjectModule.bsl";

    public ExportVariableInObjectModuleCheckTest()
    {
        super(ExportVariableInObjectModuleCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return MODULE_FILE_NAME;
    }

    @Test
    public void testExportVariable() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "object-module-export-variable.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        assertEquals("3", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
