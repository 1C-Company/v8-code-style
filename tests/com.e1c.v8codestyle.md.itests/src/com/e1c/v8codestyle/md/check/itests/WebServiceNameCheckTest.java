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
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.metadata.mdclass.WebService;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.md.check.WebServiceNameCheck;

/**
 *  Test for {@link WebServiceNameCheck}
 *
 *  @author Artem Samohvalov
 */
public class WebServiceNameCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "web-service-name";

    private static final String PROJECT_NAME = "WebServiceName";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * check error
     * Service in WebService name
     *
     * @throws Exception the exception
     */
    @Test
    public void testServiceWord() throws Exception
    {
        IBmObject object = getTopObjectByFqn("WebService.WebService", getProject());
        assertTrue(object instanceof WebService);

        WebService webService = (WebService)object;
        Marker marker = getFirstMarker(CHECK_ID, webService, getProject());
        assertNotNull(marker);
    }

    /**
     * check error
     * Russian letter in WebService name
     *
     * @throws Exception the exception
     */
    @Test
    public void testRuWord() throws Exception
    {
        IBmObject object = getTopObjectByFqn("WebService.ВебСервис", getProject());
        assertTrue(object instanceof WebService);

        WebService webService = (WebService)object;
        Marker marker = getFirstMarker(CHECK_ID, webService, getProject());
        assertNotNull(marker);
    }

    /**
     * check error
     * Russian letter in WebService parameter name
     *
     * @throws Exception the exception
     */
    @Test
    public void testRuWordInServiceParam() throws Exception
    {
        IBmObject object = getTopObjectByFqn("WebService.ValidName2", getProject());
        assertTrue(object instanceof WebService);

        WebService webService = (WebService)object;
        Marker marker = getFirstMarker(CHECK_ID, webService, getProject());
        assertNotNull(marker);
    }

    /**
     * check error
     * Russian letter in WebService operation name
     *
     * @throws Exception the exception
     */
    @Test
    public void testRuWordInServiceOperation() throws Exception
    {
        IBmObject object = getTopObjectByFqn("WebService.ValidName3", getProject());
        assertTrue(object instanceof WebService);

        WebService webService = (WebService)object;
        Marker marker = getFirstMarker(CHECK_ID, webService, getProject());
        assertNotNull(marker);
    }

    /**
     * check no error
     *
     * @throws Exception the exception
     */
    @Test
    public void testEnglishLetters() throws Exception
    {
        IBmObject object = getTopObjectByFqn("WebService.ValidName", getProject());
        assertTrue(object instanceof WebService);

        WebService webService = (WebService)object;
        Marker marker = getFirstMarker(CHECK_ID, webService, getProject());
        assertNull(marker);
    }
}
