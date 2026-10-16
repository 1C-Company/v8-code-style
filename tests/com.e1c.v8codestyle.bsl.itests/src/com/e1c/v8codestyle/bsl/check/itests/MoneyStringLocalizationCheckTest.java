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
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.MoneyStringLocalizationCheck;

/**
 * Tests for {@link MoneyStringLocalizationCheck} check.
 *
 * @author Ivan Sergeev
 */
public class MoneyStringLocalizationCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String PROJECT_NAME = "MoneyStringLocalizationCheckTest";

    private static final String LOCALIZATION_NOT_USED = "CommonForm.Form.Form";

    private static final String LOCALIZATION_USED = "CommonForm.FormCorrect.Form";

    private static final String CHECK_ID = "money-string-localization";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testNotUseNstr() throws Exception
    {
        IBmObject object = getTopObjectByFqn(LOCALIZATION_NOT_USED, getProject());
        assertNotNull(object);
        assertTrue(object instanceof Form);
        SimpleStatement statement = getFirstStatement((Form)object);
        Marker marker = getFirstMarker(CHECK_ID, statement, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testUseNstr() throws Exception
    {
        IBmObject object = getTopObjectByFqn(LOCALIZATION_USED, getProject());
        assertNotNull(object);
        assertTrue(object instanceof Form);
        SimpleStatement statement = getFirstStatement((Form)object);
        Marker marker = getFirstMarker(CHECK_ID, statement, getProject());
        assertNull(marker);
    }

    private SimpleStatement getFirstStatement(Form form)
    {
        Module module = form.getModule();
        assertNotNull(module);
        List<Method> methods = module.allMethods();
        assertNotNull(methods);
        Method method = methods.get(0);
        List<Statement> statements = method.allStatements();
        assertNotNull(statements);
        return (SimpleStatement)statements.get(0);
    }
}
