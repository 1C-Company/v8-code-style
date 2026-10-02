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
package com.e1c.v8codestyle.bsl.fix.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.OverridableModuleOnlyExportMethodsCheck;

/**
 *  Test for {@link OverridableModuleOnlyExportMethodsFix}
 *
 *  @author Artem Samohvalov
 */
public class OverridableModuleOnlyExportMethodsFixTest
    extends AbstractQuickFixTest
{
    private static final String PROJECT_NAME = "CommonModuleOverridable";
    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModuleOverridable/Module.bsl";

    public OverridableModuleOnlyExportMethodsFixTest()
    {
        super(OverridableModuleOnlyExportMethodsCheck.class);
    }

    @Test
    public void testApplyFix() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "fix-overridable-module-only-export-methods.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        performFix(marker, Messages.OverridableModuleOnlyExportMethodsFixTest_Description);
        PlatformUI.getWorkbench().saveAllEditors(false);

        assertMarkerGone();
        assertMethodExport();
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return COMMON_MODULE_FILE_NAME;
    }

    @Override
    protected Module getModule()
    {
        IBmObject mdObject = getTopObjectByFqn("CommonModule.CommonModuleOverridable", getProject());
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        return module;
    }

    private void assertMethodExport()
    {
        Module module = getModule();
        assertTrue(module.allMethods().stream().allMatch(Method::isExport));
    }

}
