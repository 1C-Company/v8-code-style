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

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
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
     * @throws CoreException
     */
    @Test
    public void testEmptyExceptStatement() throws CoreException
    {
        var dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);
        
        var mdObject = getTopObjectByFqn(FQN_MODULE_GENERAL, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        
        var module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);
        
        var methods = module.allMethods();
        assertEquals(2, methods.size());
        
        var badMethod = methods.get(0);
        var methodStatements = badMethod.allStatements();
        assertEquals(1, methodStatements.size());
        
        var statement = methodStatements.get(0);
        assertTrue(statement instanceof TryExceptStatement);
        
        var tryExceptStatement = TryExceptStatement.class.cast(statement);
        
        getFirstMarker(CHECK_ID, tryExceptStatement, dtProject);
        var marker = getFirstMarker(CHECK_ID, tryExceptStatement, dtProject);
        assertNotNull(marker);
        assertEquals("2", marker.getExtraInfo().get("line"));
    }
}
