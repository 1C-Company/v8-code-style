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
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.e1c.v8codestyle.md.check.ExtensionMdObjectNamePrefixCheck;

/**
 * Tests for {@link ExtensionMdObjectNamePrefixCheck} check.
 *
 * @author Artem Iliukhin
 */
public class ExtensionMdObjectNamePrefixCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "extension-mdobject-prefix-check"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "ExtensionObjectNamePrefixCheck";
    private static final String PROJECT_EXTENSION_NAME = "ExtensionObjectNamePrefixCheck_Extension";

    @Override
    protected String getTestConfigurationName()
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
                CorePlugin.logError(e);
                return e.getMessage();
            }
        }
        return PROJECT_EXTENSION_NAME;
    }

    @Test
    public void testNonCompliantPrefix() throws Exception
    {
        IDtProject dtProject = getProject();
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.NonCompliant", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCompliantPrefix() throws Exception
    {
        IDtProject dtProject = getProject();
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.Ext1_Compliant", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

}
