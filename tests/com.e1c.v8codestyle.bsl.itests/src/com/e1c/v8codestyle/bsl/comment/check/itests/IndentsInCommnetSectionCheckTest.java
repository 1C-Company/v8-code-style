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
package com.e1c.v8codestyle.bsl.comment.check.itests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.IndentsInCommnetSectionCheck;

/**
 * Test for {@link IndentsInCommnetSectionCheck}
 * 
 * @author Artem Samohvalov
 */
public class IndentsInCommnetSectionCheckTest
    extends AbstractSingleModuleTestBase
{

    public IndentsInCommnetSectionCheckTest()
    {
        super(IndentsInCommnetSectionCheck.class);
    }

    /**
     * Test no error
     * Test valid indents in sections.
     *
     * @throws Exception the exception
     */
    @Test
    public void testValidIndentsInSections() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "valid-indents-in-sections.bsl");

        List<Marker> markers = getModuleMarkers();

        assertTrue(markers.isEmpty());
    }

    /**
     * Test error
     * Test no indents in param section.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoIndentsInParamSection() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-indents-in-param-field-section.bsl");

        List<Marker> markers = getModuleMarkers();

        assertTrue(markers.size() == 5);

        for (Marker marker : markers)
        {
            int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            assertTrue(val == 4 || val == 11 || val == 17 || val == 18 || val == 24);
        }
    }

    /**
     * Test error
     * Test no indents in param description section.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoIndentsInParamDescriptionSection() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-indents-in-param-field-description-section.bsl");

        List<Marker> markers = getModuleMarkers();

        assertTrue(markers.size() == 3);

        for (Marker marker : markers)
        {
            int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            assertTrue(val == 4 || val == 14 || val == 23);
        }
    }

    /**
     * Test error
     * Test no indents in return description section.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoIndentsInReturnDescriptionSection() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-indents-in-return-description-section.bsl");

        List<Marker> markers = getModuleMarkers();

        assertTrue(markers.size() == 4);

        for (Marker marker : markers)
        {
            int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            assertTrue(val == 4 || val == 14 || val == 25 || val == 36);
        }
    }

    /**
     * Test error
     * Test no indents in example section.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoIndentsInReturnSection() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-indents-in-return-section.bsl");

        List<Marker> markers = getModuleMarkers();

        assertTrue(markers.size() == 2);

        for (Marker marker : markers)
        {
            int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            assertTrue(val == 3 || val == 12);
        }
    }

    /**
     * Test error
     * Test no indents in call options section.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoIndentsInCallOptionsSection() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-indents-in-call-options-section.bsl");

        List<Marker> markers = getModuleMarkers();

        assertTrue(markers.size() == 3);

        for (Marker marker : markers)
        {
            int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            assertTrue(val == 4 || val == 10 || val == 17);
        }
    }

    /**
     * Test error
     * Test no indents in return section.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoIndentsInExampleSection() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-indents-in-example-section.bsl");

        List<Marker> markers = getModuleMarkers();

        assertTrue(markers.size() == 3);

        for (Marker marker : markers)
        {
            int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            assertTrue(val == 3 || val == 12 || val == 21);
        }
    }
}
