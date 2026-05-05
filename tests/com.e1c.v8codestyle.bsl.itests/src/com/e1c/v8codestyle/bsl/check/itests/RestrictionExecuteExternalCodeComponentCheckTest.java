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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.RestrictionExecuteExternalCodeComponentCheck;

/**
 * Tests for {@link RestrictionExecuteExternalCodeComponentCheck} check.
 *
 * @author Ivan Sergeev
 */
public class RestrictionExecuteExternalCodeComponentCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "ExecuteExternalCodeCheckTest";

    public RestrictionExecuteExternalCodeComponentCheckTest()
    {
        super(RestrictionExecuteExternalCodeComponentCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * External call not use ssl.
     *
     * @throws Exception the exception
     */
    @Test
    public void testExternalCallNotUseSSL() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "external-call-not-use-ssl.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * External call not use ssl eng.
     *
     * @throws Exception the exception
     */
    @Test
    public void testExternalCallNotUseSSLEng() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "external-call-not-use-ssl-eng.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * External call use ssl.
     *
     * @throws Exception the exception
     */
    @Test
    public void testExternalCallUseSSL() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "external-call-use-ssl.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }
}
