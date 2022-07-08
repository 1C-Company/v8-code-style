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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ModuleStructureInterfaceRegionsCheck;

/**
 * Tests for {@link ModuleStructureInterfaceRegionsCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureInterfaceRegionsCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "module-structure-interface-regions"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "StructureModule";

    private static final String FQN_COMMON_MODULE_IN_REGION = "CommonModule.CommonModuleInRegion";
    private static final String FQN_COMMON_MODULE_EXPORT_IN_REGION = "CommonModule.CommonModuleExportInRegion";
    private static final String FQN_COMMON_MODULE_EXPORT_IN_NON_INTERFACE_REGION =
        "CommonModule.CommonModuleInNonInterfaceRegion";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }


    @Test
    public void testExportMethodInNonInterfaceRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_COMMON_MODULE_EXPORT_IN_NON_INTERFACE_REGION, dtProject);
        assertTrue(mdObject instanceof CommonModule);

        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testMethodInRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_COMMON_MODULE_IN_REGION, dtProject);
        assertTrue(mdObject instanceof CommonModule);

        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testExportMethodInRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_COMMON_MODULE_EXPORT_IN_REGION, dtProject);
        assertTrue(mdObject instanceof CommonModule);

        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNull(marker);
    }

}
