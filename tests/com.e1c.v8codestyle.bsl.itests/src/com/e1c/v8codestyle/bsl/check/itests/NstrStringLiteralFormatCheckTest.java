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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.NstrStringLiteralFormatCheck;

/**
 * Test for the {@link NstrStringLiteralFormatCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class NstrStringLiteralFormatCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String FOLDER = FOLDER_RESOURCE + "nstr-format/";

    public NstrStringLiteralFormatCheckTest()
    {
        super(NstrStringLiteralFormatCheck.class);
    }

    /**
     * Test that Nstr first parameter is compliant
     *
     * @throws Exception the exception
     */
    @Test
    public void testNstrStringLiteralCompliant() throws Exception
    {
        updateModule(FOLDER + "compliant.bsl");

        Marker marker = getModuleFirstMarker();
        assertNull(marker);
    }


    /**
     * Test that Nstr first parameter is string literal
     *
     * @throws Exception the exception
     */
    @Test
    public void testNstrStringIsStringLiteral() throws Exception
    {
        updateModule(FOLDER + "non-compliant-1.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("4", marker.getExtraInfo().get("line"));

    }

    /**
     * Test that Nstr first parameter is string literal and it is not empty
     *
     * @throws Exception the exception
     */
    @Test
    public void testNstrStringLiteralIsEmpty() throws Exception
    {
        updateModule(FOLDER + "non-compliant-2.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("4", marker.getExtraInfo().get("line"));

    }

    /**
     * Test that Nstr first parameter is string literal and it's format in valid key=value format
     *
     * @throws Exception the exception
     */
    @Test
    public void testNstrStringLiteralFormatIsNotValid() throws Exception
    {
        updateModule(FOLDER + "non-compliant-3.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("4", marker.getExtraInfo().get("line"));

    }

    /**
     * Test that Nstr first parameter is string literal contains not existing language code
     *
     * @throws Exception the exception
     */
    @Test
    public void testNstrLanguageCodeNotExist() throws Exception
    {
        updateModule(FOLDER + "non-compliant-4.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("4", marker.getExtraInfo().get("line"));

    }

    /**
     * Test that Nstr first parameter is string literal and the language text is empty
     *
     * @throws Exception the exception
     */
    @Test
    public void testNstrLanguageTextIsEmpty() throws Exception
    {
        updateModule(FOLDER + "non-compliant-5.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("4", marker.getExtraInfo().get("line"));

    }

    /**
     * Test that Nstr first parameter is string literal and language text ends with space.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNstrStringLiteralEndsWithStapce() throws Exception
    {
        updateModule(FOLDER + "non-compliant-6.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("4", marker.getExtraInfo().get("line"));

    }

    /**
     * Test that Nstr first parameter is string literal and language text ends with new line.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNstrStringLiteralEndsWithNewLine() throws Exception
    {
        updateModule(FOLDER + "non-compliant-7.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("4", marker.getExtraInfo().get("line"));

    }

}
