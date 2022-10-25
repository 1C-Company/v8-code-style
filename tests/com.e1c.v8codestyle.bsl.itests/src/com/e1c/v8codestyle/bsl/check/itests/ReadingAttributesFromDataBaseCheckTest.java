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

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
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
    private static final String PROJECT_NAME = "ReadingAttributesFromDataBaseCheck";

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
    public void testWrongReadProperty() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        assertEquals("16", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testWrongReadPropertyComposite() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-composite.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());

        assertEquals("4", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("6", markers.get(1).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testWrongReadPropertyCompositeNonRef() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-composite-non-ref.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        assertEquals("4", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testReadProperty() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-compliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testReadPropertyBsl() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "read-single-property-in-bsl.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());

        assertEquals("8", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

}
