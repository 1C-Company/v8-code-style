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
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ModuleStructureEventFormRegionsCheck;

/**
 * Tests for {@link ModuleStructureEventFormRegionsCheck} check.
 *
 * @author Artem Iliukhin
 *
 */
public class ModuleStructureEventFormRegionsCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "module-structure-form-event-regions"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "ModuleStructureEventFormRegionsCheck";

    private static final String CATALOG_WRONG_REGION_FILE_NAME =
        "/src/Catalogs/CatalogInWrongRegion/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_WRONG_METHOD_FILE_NAME =
        "/src/Catalogs/CatalogInWrongMethod/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_FILE_NAME = "/src/Catalogs/Catalog/Forms/ItemForm/Module.bsl";

    private static final String CATALOG_FIELD_FILE_NAME = "/src/Catalogs/CatalogField/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_FIELD_WRONG_REGION_FILE_NAME =
        "/src/Catalogs/CatalogFieldInWrongRegion/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_FIELD_WRONG_METHOD_FILE_NAME =
        "/src/Catalogs/CatalogFieldInWrongMethod/Forms/ItemForm/Module.bsl";

    private static final String CATALOG_COMMAND_FILE_NAME = "/src/Catalogs/CatalogCommand/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_COMMAND_WRONG_REGION_FILE_NAME =
        "/src/Catalogs/CatalogCommandInWrongRegion/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_COMMAND_WRONG_METHOD_FILE_NAME =
        "/src/Catalogs/CatalogCommandInWrongMethod/Forms/ItemForm/Module.bsl";

    private static final String CATALOG_TABLE_FILE_NAME = "/src/Catalogs/CatalogTable/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_TABLE_WRONG_REGION_FILE_NAME =
        "/src/Catalogs/CatalogTableInWrongRegion/Forms/ItemForm/Module.bsl";
    private static final String CATALOG_TABLE_WRONG_METHOD_FILE_NAME =
        "/src/Catalogs/CatalogTableInWrongMethod/Forms/ItemForm/Module.bsl";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testFormEventInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testFormEventInWrongRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_WRONG_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("20", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("The event method \"OnOpen\" should be placed in the \"FormEventHandlers\" region",
            markers.get(0).getMessage());
    }

    @Test
    public void testFormEventInWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("3", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("Method \"WrongMethod\" can not be placed in the \"FormEventHandlers\" region",
            markers.get(0).getMessage());
    }

    @Test
    public void testFormFieldEventInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FIELD_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testFormFieldEventInWrongRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FIELD_WRONG_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("13", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("The event method \"DescriptionOnChange\" should be placed "
            + "in the \"FormHeaderItemsEventHandlers\" region", markers.get(0).getMessage());
    }

    @Test
    public void testFormFieldEventInWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FIELD_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("7", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("Method \"WrongMethod\" can not be placed in the \"FormHeaderItemsEventHandlers\" region",
            markers.get(0).getMessage());
    }

    @Test
    public void testFormCommandEventInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_COMMAND_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testFormCommandEventInWrongRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_COMMAND_WRONG_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("5", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("The event method \"Command1\" should be placed in the \"FormCommandsEventHandlers\" region",
            markers.get(0).getMessage());
    }

    @Test
    public void testFormFieldCommandInWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_COMMAND_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("15", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("Method \"WrongMethod\" can not be placed in the \"FormCommandsEventHandlers\" region",
            markers.get(0).getMessage());
    }

    @Test
    public void testFormTableEventInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_TABLE_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testFormTableEventInWrongRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_TABLE_WRONG_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("9", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(
            "The event method \"TableAttribute1OnChange\" should be placed "
                + "in the \"FormTableItemsEventHandlersTable\" region",
            markers.get(0).getMessage());
    }

    @Test
    public void testFormTableCommandInWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_TABLE_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("12", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("Method \"WrongMethod\" can not be placed in the \"FormTableItemsEventHandlers\" region",
            markers.get(0).getMessage());
    }

    private List<Marker> getMarkers(String moduleFileName)
    {
        String moduleId = Path.ROOT.append(getTestConfigurationName()).append(moduleFileName).toString();
        List<Marker> markers = List.of(markerManager.getMarkers(getProject().getWorkspaceProject(), moduleId));

        return markers.stream()
            .filter(marker -> CHECK_ID.equals(getCheckIdFromMarker(marker, getProject())))
            .collect(Collectors.toList());
    }

}
