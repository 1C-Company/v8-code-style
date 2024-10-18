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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ExtensionVariablePrefixCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link ExtensionVariablePrefixCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ExtensionVariablePrefixCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "extension-variable-prefix"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "ExtensionVariablePrefixCheck";
    private static final String PROJECT_EXTENSION_NAME = "ExtensionVariablePrefixCheck_Extension";

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";
    private static final String COMMON_MODULE_COMPLIANT_FILE_NAME =
        "/src/CommonModules/CompliantCommonModule/Module.bsl";
    private static final String CATALOG_FORM_MODULE_FILE_NAME = "/src/Catalogs/Catalog/Forms/ItemForm/Module.bsl";

    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/Form/Module.bsl";

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

    @Test
    public void testCommonModuleCompliantPrefix() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_FILE_NAME);
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testCommonFormMethodsVarPrefix() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_FORM_FILE_NAME);
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testCompliantPrefix() throws Exception
    {
        List<Marker> markers = getMarkers(COMMON_MODULE_COMPLIANT_FILE_NAME);
        assertTrue(markers.isEmpty());
    }

    @Test
    public void testDSVariablePrefix() throws Exception
    {
        List<Marker> markers = getMarkers(CATALOG_FORM_MODULE_FILE_NAME);
        assertFalse(markers.isEmpty());
        assertEquals(Integer.valueOf(1), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
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
