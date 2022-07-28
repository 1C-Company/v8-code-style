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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ModuleStructureEventRegionsCheck;

/**
 * Tests for {@link ModuleStructureEventRegionsCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureEventRegionsCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "module-structure-event-regions"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "StructureModule";

    private static final String FQN_CATALOG_MODULE_MANAGER_EVENT = "Catalog.CatalogInRegion";
    private static final String FQN_CATALOG_MODULE_MANAGER_EVENT_WRONG_REGION = "Catalog.CatalogInWrongRegion";
    private static final String FQN_CATALOG_MODULE_MANAGER_EVENT_WRONG_METHOD = "Catalog.CatalogInRegionWrongMethod";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testEventModuleManagerInWrongRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_MODULE_MANAGER_EVENT_WRONG_REGION, dtProject);
        assertTrue(mdObject instanceof Catalog);

        Module module = ((Catalog)mdObject).getManagerModule();
        assertNotNull(module);

        Marker marker = getFirstMarker(CHECK_ID, module.eResource().getURI(), getProject());
        assertNotNull(marker);
    }

    @Test
    public void testEventModuleManagerInRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_MODULE_MANAGER_EVENT, dtProject);
        assertTrue(mdObject instanceof Catalog);

        Module module = ((Catalog)mdObject).getManagerModule();
        assertNotNull(module);

        Marker marker = getFirstMarker(CHECK_ID, module.eResource().getURI(), getProject());
        assertNull(marker);
    }

    @Test
    public void testEventModuleManagerInRegionWrongMethod() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_MODULE_MANAGER_EVENT_WRONG_METHOD, dtProject);
        assertTrue(mdObject instanceof Catalog);

        Module module = ((Catalog)mdObject).getManagerModule();
        assertNotNull(module);

        Marker marker = getFirstMarker(CHECK_ID, module.eResource().getURI(), getProject());
        assertNotNull(marker);
    }

}
