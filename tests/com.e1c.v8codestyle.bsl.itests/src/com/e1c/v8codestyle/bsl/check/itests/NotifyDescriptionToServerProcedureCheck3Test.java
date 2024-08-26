/*******************************************************************************
 * Copyright (C) 2024, 1C-Soft LLC and others.
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

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.NotifyDescriptionToServerProcedureCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * The test of {@link NotifyDescriptionToServerProcedureCheck} check.
 *
 * @author Dzyuba_M
 */
public class NotifyDescriptionToServerProcedureCheck3Test
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "notify-description-to-server-procedure"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "ExtensionNotifyDescriptionToServerProcedureCheck";
    private static final String PROJECT_EXTENSION_NAME = "ExtensionNotifyDescriptionToServerProcedureCheck_Extension";

    /**
     * Test notify description to common module server procedure.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCommonModuleServerProcedure() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());

        Marker marker = markers.get(0);
        assertEquals("11", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

        marker = markers.get(1);
        assertEquals("21", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Override
    public void setUp() throws CoreException
    {
        IProject project = testingWorkspace.getProject(PROJECT_NAME);
        if (!project.exists() || !project.isAccessible())
        {
            try
            {
                testingWorkspace.cleanUpWorkspace();
                openProjectAndWaitForValidationFinish(PROJECT_NAME);
            }
            catch (CoreException e)
            {
                BslPlugin.logError(e);
            }
        }
        super.setUp();
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_EXTENSION_NAME;
    }

    private String getModuleFileName()
    {
        return "/src/Catalogs/Справочник1/Forms/ФормаЭлемента/Module.bsl"; //$NON-NLS-1$
    }

    private List<Marker> getModuleMarkers()
    {
        String moduleId = Path.ROOT.append(getTestConfigurationName()).append(getModuleFileName()).toString();
        List<Marker> markers = List.of(markerManager.getMarkers(getProject().getWorkspaceProject(), moduleId));

        return markers.stream()
            .filter(marker -> CHECK_ID.equals(getCheckIdFromMarker(marker, getProject())))
            .collect(Collectors.toList());
    }
}
