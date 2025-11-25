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
import com.e1c.v8codestyle.bsl.check.NotSupportGotoOperatorWebCheck;

/**
 * Tests for {@link NotSupportGotoOperatorWebCheck} check.
 *
 * @author Ivan Sergeev
 */
public class NotSupportGotoOperatorCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "NotSupportGotoOperatorCheckTest";

    public NotSupportGotoOperatorCheckTest()
    {
        super(NotSupportGotoOperatorWebCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test not support goto operator.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotSupportGoToOperator() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "not-support-goto-operator.bsl");


        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(7), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test operator after another operator.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOperatorAfter() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "not-support-goto-operator-after.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(9), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test operator before another operator.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOperatorBefore() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "not-support-goto-operator-before.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(7), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test operator in if preprocessor.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOperatorInIfPreprocessor() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "not-support-goto-operator-in-if-preprocessor.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(9), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

    }

    /**
     * Test operator goto support.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSupportOperator() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "support-goto-operator.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

    }
}
