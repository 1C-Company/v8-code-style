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

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
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

        assertEquals(Integer.valueOf(20), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__should_be_placed_in_the_region__1, "OnOpen",
            "FormEventHandlers"), markers.get(0).getMessage());
    }

    @Test
    public void testFormEventInWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(3), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__can_not_be_placed_in_the_region__1,
            "WrongMethod", "FormEventHandlers"), markers.get(0).getMessage());
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

        assertEquals(Integer.valueOf(13), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__should_be_placed_in_the_region__1,
            "DescriptionOnChange", "FormHeaderItemsEventHandlers"), markers.get(0).getMessage());
    }

    @Test
    public void testFormFieldEventInWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FIELD_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(7), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__can_not_be_placed_in_the_region__1,
            "WrongMethod", "FormHeaderItemsEventHandlers"), markers.get(0).getMessage());
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

        assertEquals(Integer.valueOf(5), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__should_be_placed_in_the_region__1,
            "Command1", "FormCommandsEventHandlers"), markers.get(0).getMessage());
    }

    @Test
    public void testFormFieldCommandInWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_COMMAND_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(15), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__can_not_be_placed_in_the_region__1,
            "WrongMethod", "FormCommandsEventHandlers"), markers.get(0).getMessage());
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

        assertEquals(Integer.valueOf(9), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__should_be_placed_in_the_region__1,
            "TableAttribute1OnChange", "FormTableItemsEventHandlersTable"), markers.get(0).getMessage());
    }

    @Test
    public void testFormTableCommandInWrongMethod() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_TABLE_WRONG_METHOD_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(12), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__can_not_be_placed_in_the_region__1,
            "WrongMethod", "FormTableItemsEventHandlers"), markers.get(0).getMessage());
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
