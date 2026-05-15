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
package com.e1c.v8codestyle.bsl.comment.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.LinkPartSpaceCheck;

/**
 * Tests for {@link LinkPartSpaceCheck} check.
 *
 * @author Ivan Sergeev
 */
public class LinkPartSpaceCheckTest
    extends AbstractSingleModuleTestBase
{
    public LinkPartSpaceCheckTest()
    {
        super(LinkPartSpaceCheck.class);
    }

    /**
     * Test incorrect link.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLink() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-missing-link-part.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-missing-link-part-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link with underscore.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkWithUnderscore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-underscore-link-part.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link with underscore.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkWithUnderscoreRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-underscore-link-part-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link with colon before.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkWithColonBeforeUnderscore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-colon-before-underscore-link-part.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link with colon before.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkWithColonBeforeUnderscoreRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-colon-before-underscore-link-part-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link with colon.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkWithColon() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-colon-link-part.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link with colon.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkWithColonRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-colon-link-part-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link with colon before space.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkWithColonBeforeSpace() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-colon-link-part.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link with colon before space.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIncorrectLinkWithColonBeforeSpaceRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-colon-link-part-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link after another link.
     *
     * @throws Exception the exception
     */
    @Test
    public void testLinkAfter() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-missing-link-part-after.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link after another link.
     *
     * @throws Exception the exception
     */
    @Test
    public void testLinkAfterRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-missing-link-part-after-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link before another link
     *
     * @throws Exception the exception
     */
    @Test
    public void testLinkBefore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-missing-link-part-before.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test incorrect link before another link
     *
     * @throws Exception the exception
     */
    @Test
    public void testLinkBeforeRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-missing-link-part-before-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test correct link.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCorrectLink() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-non-missing-link-part.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test correct link.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCorrectLinkRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-non-missing-link-part-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test correct link with tab.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCorrectLinkWithTab() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-non-missing-tab-link-part.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test correct link with tab.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCorrectLinkWithTabRu() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "space-non-missing-tab-link-part-ru.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }
}
