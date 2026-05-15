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
import com.e1c.v8codestyle.bsl.check.MethodSemicolonExtraCheck;

/**
 * Tests for {@link MethodSemicolonExtraCheck} check.
 *
 * @author Ivan Sergeev
 */
public class MethodSemicolonExtraCheckTest
    extends AbstractSingleModuleTestBase
{

    public MethodSemicolonExtraCheckTest()
    {
        super(MethodSemicolonExtraCheck.class);
    }

    /**
     * Test method declaration contains extra semicolon.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMethodDeclarationExtraSemicolon() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "method-declaration-contain-semicolon.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test method after another method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMethodAfter() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-semicolon-method-after.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(4), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test method before another method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMethodBefore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "extra-semicolon-method-before.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(8), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test method declaration does not contain extra semicolon.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMethodDeclarationNoExtraSemicolon() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "non-contain-extra-semicolon-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }
}