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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ModuleStructureMethodInRegionCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link ModuleStructureMethodInRegionCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ModuleStructureMethodInRegionCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "module-structure-method-in-regions"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "ModuleStructureMethodInRegionCheck";

    private static final String COMMON_MODULE_NO_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleNoRegion/Module.bsl";
    private static final String COMMON_MODULE_AFTER_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleAfterRegion/Module.bsl";
    private static final String CATALOG_MODULE_MANAGER_EVENT_OUT_REGION_FILE_NAME =
        "/src/Catalogs/CatalogOutOfRegion/ManagerModule.bsl";

    private static final String COMMON_MODULE_IN_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleInRegion/Module.bsl";
    private static final String COMMON_MODULE_EXPORT_IN_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleExportInRegion/Module.bsl";
    private static final String COMMON_MODULE_EXPORT_IN_NON_INTERFACE_REGION_FILE_NAME =
        "/src/CommonModules/CommonModuleInNonInterfaceRegion/Module.bsl";
    private static final String COMMAND_MODULE_OUT_OF_REGION_FILE_NAME =
        "/src/CommonCommands/CommonCommand/CommandModule.bsl";
    private static final String FORM_MODULE_OUT_OF_REGION_FILE_NAME =
        "/src/Catalogs/CatalogOutOfRegion/Forms/ItemForm/Module.bsl";
    private static final Object MULTILEVEL_NESTING_OF_REGIONS = "multilevelNestingOfRegions";
    private static final String COMMON_MODULE_OUT_OF_MULTI_REGION_FILE_NAME =
        "/src/CommonModules/CommonModulMultiLevel/Module.bsl";
    private static final String COMMON_MODULE_OUT_OF_MULTI_REGION_NONCOMPLIENT_FILE_NAME =
        "/src/CommonModules/CommonModuleMultiLevel1/Module.bsl";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Before
    public void setSettings()
    {
        IDtProject dtProject = getProject();
        IProject project = dtProject.getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(new CheckUid(CHECK_ID, BslPlugin.PLUGIN_ID), project);
        settings.getParameters().get(MULTILEVEL_NESTING_OF_REGIONS).setValue(Boolean.toString(Boolean.TRUE));
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(dtProject);
    }

    @Test
    public void testNoRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_NO_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("1", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testMethodAfterRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_AFTER_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("5", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testEventModuleManagerOutOfRegion() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_MODULE_MANAGER_EVENT_OUT_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("10", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testExportMethodInNonInterfaceRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_EXPORT_IN_NON_INTERFACE_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("3", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testMethodInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_IN_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("3", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testExportMethodInRegion() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_EXPORT_IN_REGION_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testMethodOutOfRegionInForm() throws Exception
    {
        List<Marker> markers = getMarkers(FORM_MODULE_OUT_OF_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("2", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testMethodOutOfRegionInCommand() throws Exception
    {
        List<Marker> markers = getMarkers(COMMAND_MODULE_OUT_OF_REGION_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("3", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testMethodOutOfNonMultiRegionOption() throws Exception
    {
        IDtProject dtProject = getProject();
        IProject project = dtProject.getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(new CheckUid(CHECK_ID, BslPlugin.PLUGIN_ID), project);
        settings.getParameters().get(MULTILEVEL_NESTING_OF_REGIONS).setValue(Boolean.toString(Boolean.FALSE));
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(dtProject);

        List<Marker> markers = getMarkers(COMMON_MODULE_OUT_OF_MULTI_REGION_FILE_NAME);
        assertEquals(0, markers.size());
    }

    @Test
    public void testMethodOutOfNonMultiRegionOptionNonComplient() throws Exception
    {
        IDtProject dtProject = getProject();
        IProject project = dtProject.getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(new CheckUid(CHECK_ID, BslPlugin.PLUGIN_ID), project);
        settings.getParameters().get(MULTILEVEL_NESTING_OF_REGIONS).setValue(Boolean.toString(Boolean.FALSE));
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(dtProject);

        List<Marker> markers = getMarkers(COMMON_MODULE_OUT_OF_MULTI_REGION_NONCOMPLIENT_FILE_NAME);
        assertEquals(1, markers.size());

        assertEquals("6", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testMethodOutOfMultiRegionOption() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_OUT_OF_MULTI_REGION_FILE_NAME);
        assertEquals(0, markers.size());
    }

    private List<Marker> getMarkers(String moduleFileName)
    {
        String moduleId = Path.ROOT.append(getTestConfigurationName()).append(moduleFileName).toString();
        List<Marker> markers = List.of(markerManager.getMarkers(getProject().getWorkspaceProject(), moduleId));

        return markers.stream()
            .filter(marker -> CHECK_ID.equals(getCheckIdFromMarker(marker, getProject())))
            .collect(Collectors.toList());
    }

}
