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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.LocalizationFormNstrCheck;

/**
 * Tests for {@link LocalizationFormNstrCheck} check.
 *
 * @author Ivan Sergeev
 */
public class LocalizationFormTextCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String PROJECT_NAME = "LocalizationFormTextCheckTest";

    private static final String LOCALIZATION_NOT_USED = "CommonForm.Form.Form";

    private static final String LOCALIZATION_USED = "CommonForm.FormCorrect.Form";

    private static final String CHECK_ID = "nstr-form-localization";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testNotUseNstr() throws Exception
    {
        SimpleStatement statement = getFirstStatement(LOCALIZATION_NOT_USED);

        Marker marker = getFirstMarker(CHECK_ID, statement, dtProjectManager.getDtProject(PROJECT_NAME));
        assertNotNull(marker);
    }

    @Test
    public void testUseNstr() throws Exception
    {
        SimpleStatement statement = getFirstStatement(LOCALIZATION_USED);

        Marker marker = getFirstMarker(CHECK_ID, statement, dtProjectManager.getDtProject(PROJECT_NAME));
        assertNull(marker);
    }

    private SimpleStatement getFirstStatement(String fqn)
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(fqn, dtProject);
        assertNotNull(mdObject);
        assertTrue(mdObject instanceof Form);

        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        Method method = methods.get(0);
        return (SimpleStatement)method.allStatements().get(0);
    }
}
