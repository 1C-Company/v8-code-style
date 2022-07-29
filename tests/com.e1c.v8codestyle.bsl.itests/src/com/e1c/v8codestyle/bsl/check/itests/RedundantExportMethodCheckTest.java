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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.RedundantExportMethodCheck;

/**
 * Tests for {@link RedundantExportMethodCheck} check.
 *
 * @author Artem Iliukhin
 */
public class RedundantExportMethodCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "redundant-export-method";
    private static final String PROJECT_NAME = "ExcessExportCheck";
    private static final String FQN_MODULE = "CommonModule.NoCallNoPublic";
    private static final String FQN_MODULE_NO_CALL_PUBLIC = "CommonModule.NoCallPublic";
    private static final String FQN_MODULE_CALL_NO_PUBLIC = "CommonModule.CallNoPublic";
    private static final String FQN_CATALOG = "Catalog.Catalog";
    private static final String FQN_CATALOG_FORM = "Catalog.Catalog.Form.ItemForm.Form";
    private static final String FQN_CATALOG_LIST_FORM = "Catalog.Catalog.Form.ListForm.Form";
    private static final String FQN_MODULE_IS_EVENT_SUBSCRIPTION = "CommonModule.isEventSubscription";
    private static final String FQN_MODULE_IS_SCHEDULED_JOB = "CommonModule.isScheduledJob";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testNoCallNoPublic() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_MODULE, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(1, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testNoCallPublic() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_MODULE_NO_CALL_PUBLIC, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(1, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNull(marker);
    }

    @Test
    public void testCallNoPublic() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_MODULE_CALL_NO_PUBLIC, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(1, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNull(marker);
    }

    @Test
    public void testLocalCall() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG, dtProject);
        assertTrue(mdObject instanceof Catalog);
        Module module = ((Catalog)mdObject).getObjectModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testNotifyCall() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_FORM, dtProject);
        assertTrue(mdObject instanceof Form);
        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNull(marker);
    }

    @Test
    public void testNotifyWithRegionCall() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_LIST_FORM, dtProject);
        assertTrue(mdObject instanceof Form);
        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method noncompliantMethod = methods.get(1);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNull(marker);
    }

    @Test
    public void testEventSubscription() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_MODULE_IS_EVENT_SUBSCRIPTION, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(1, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNull(marker);
    }

    @Test
    public void testScheduledJob() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_MODULE_IS_SCHEDULED_JOB, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(1, methods.size());

        Method noncompliantMethod = methods.get(0);
        Marker marker = getFirstMarker(CHECK_ID, noncompliantMethod, dtProject);
        assertNull(marker);
    }
}
