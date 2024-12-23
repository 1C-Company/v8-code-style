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
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.bsl.check.EventDataExchangeLoadCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link EventDataExchangeLoadCheck} check.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class EventDataExchangeLoadCheckTest
    extends CheckTestBase
{
    private static final String FQN_CATALOG_PRODUCTS = "Catalog.Products";

    private static final String CHECK_ID = "data-exchange-load";

    private static final String PARAM_CHECK_AT_BEGINNING = "checkAtBeginning"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "EventDataExchangeLoadCheck";

    /**
     * Test data exchange load in event handlers
     *
     * @throws Exception the exception
     */
    @Test
    public void testDataExchangeLoad() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS, dtProject);
        assertTrue(mdObject instanceof Catalog);
        Module module = ((Catalog)mdObject).getObjectModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(3, methods.size());

        Marker marker = getFirstMarker(CHECK_ID, methods.get(0), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        Method method = methods.get(2);
        assertEquals(2, method.allStatements().size());
        Statement statement = method.allStatements().get(1);
        assertTrue(statement instanceof IfStatement);

        marker = getFirstMarker(CHECK_ID, ((IfStatement)statement).getIfPart(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(24), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

    }

    /**
     * Test data exchange load in the beginning of event handlers
     *
     * @throws Exception
     */
    @Test
    public void testDataExchangeLoadAtBeginning() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS, dtProject);
        assertTrue(mdObject instanceof Catalog);
        Module module = ((Catalog)mdObject).getObjectModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(3, methods.size());

        IProject project = dtProject.getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(new CheckUid(CHECK_ID, BslPlugin.PLUGIN_ID), project);
        settings.getParameters().get(PARAM_CHECK_AT_BEGINNING).setValue(Boolean.toString(true));
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(dtProject);

        // check third method has warning on method name
        Method method = methods.get(2);
        Marker marker = getFirstMarker(CHECK_ID, method, dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(18), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        assertEquals(2, method.allStatements().size());
        Statement statement = method.allStatements().get(1);
        assertTrue(statement instanceof IfStatement);

        marker = getFirstMarker(CHECK_ID, ((IfStatement)statement).getIfPart(), dtProject);
        assertNull(marker);

    }
}
