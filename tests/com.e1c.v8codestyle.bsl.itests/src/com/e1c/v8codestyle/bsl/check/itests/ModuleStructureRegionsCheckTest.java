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

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.ModuleStructureRegionsCheck;

/**
 * Tests for {@link ModuleStructureRegionsCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureRegionsCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String CHECK_ID = "module-structure-regions"; //$NON-NLS-1$

    public ModuleStructureRegionsCheckTest()
    {
        super(ModuleStructureRegionsCheck.class);
    }

    @Test
    public void testNoRegion() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-no-region.bsl");

        Module module = getModule();
        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testMethodAfterRegion() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "module-structure-method-after-region.bsl");

        Module module = getModule();
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
        updateModule(FOLDER_RESOURCE + "module-structure-method-in-region.bsl");

        Module module = getModule();
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
        updateModule(FOLDER_RESOURCE + "module-structure-export-method-in-region.bsl");

        Module module = getModule();
        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNull(marker);
    }
}
