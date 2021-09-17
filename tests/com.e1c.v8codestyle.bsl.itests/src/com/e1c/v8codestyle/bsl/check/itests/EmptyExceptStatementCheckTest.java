/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Viktor Gukov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.EList;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.bsl.check.EmptyExceptStatementCheck;

/**
 * Tests for {@link EmptyExceptStatementCheck} check.
 *
 * @author Viktor Gukov
 *
 */
public class EmptyExceptStatementCheckTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "EmptyExceptStatement";
    private static final String CHECK_ID = "empty-except-statement";
    private static final String FQN_MODULE_GENERAL = "CommonModule.ОбщийМодуль1";

    /**
     * Test common module methods for empty except statements
     * 
     * @throws Exception
     */
    @Test
    public void testEmptyExceptStatement() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_MODULE_GENERAL, dtProject);
        assertTrue(mdObject instanceof CommonModule);

        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        EList<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        Method badMethod = methods.get(0);
        EList<Statement> methodStatements = badMethod.allStatements();
        assertEquals(1, methodStatements.size());

        Statement statement = methodStatements.get(0);
        assertTrue(statement instanceof TryExceptStatement);

        TryExceptStatement tryExceptStatement = TryExceptStatement.class.cast(statement);

        getFirstMarker(CHECK_ID, tryExceptStatement, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, tryExceptStatement, dtProject);
        assertNotNull(marker);
        assertEquals("2", marker.getExtraInfo().get("line"));
    }
}
