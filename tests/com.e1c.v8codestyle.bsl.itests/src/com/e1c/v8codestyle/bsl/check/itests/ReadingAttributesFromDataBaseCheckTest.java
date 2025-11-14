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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.ReadingAttributesFromDataBaseCheck;

/**
 * Tests for {@link ReadingAttributesFromDataBaseCheck} check.
 *
 * @author Artem Iliukhin
 *
 */
public class ReadingAttributesFromDataBaseCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String CHECK_ID = "reading-attribute-from-database"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "ModuleStructureTopRegionCheck";

    public ReadingAttributesFromDataBaseCheckTest()
    {
        super(ReadingAttributesFromDataBaseCheck.class);
    }

    @Override
    protected String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testWrongReadPropertyDbCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-db-non-compliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(3), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testReadPropertyQlCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-ql-compliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testReadPropertyCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-bsl-compliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testReadPropertyNonCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-bsl-non-compliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        assertEquals(Integer.valueOf(8), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    @Test
    public void testReadPropertySimpleType() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-simple-type.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testReadPropertyEnumRefType() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-enum-ref-type.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

}
