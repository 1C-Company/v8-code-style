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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.FillCheckProcessingPropertiesArrayModificationCheck;

/**
 *  Tests for {@link FillCheckProcessingPropertiesArrayModificationCheck} check.
 *  Test check on another modules
 *
 *  @author Artem Samohvalov
 */
public class FillCheckProcessingPropertiesArrayModificationCheckTest2
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String FQN = "CommonModule.CommonModule";
    private static final String CHECK_ID = "fill-check-processing-properties-array-modification";
    private static final String PROJECT_NAME = "FillCheckProcessingPropertiesArrayModificationCheck";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test error
     *
     * @throws Exception the exception
     */
    @Test
    public void testDataExchangeLoad() throws Exception
    {
        IBmObject mdObject = getTopObjectByFqn(FQN, getProject());
        assertTrue(mdObject instanceof CommonModule);
        CommonModule commonModule = (CommonModule)mdObject;
        Module module = commonModule.getModule();
        assertNotNull(module);

        Optional<Method> method =
            module.allMethods().stream().filter(m -> m.getName().equalsIgnoreCase("FillCheckProcessing")).findAny();
        assertFalse(method.isEmpty());

        assertFalse(method.get().allStatements().isEmpty());
        Invocation invocation = (Invocation)(((SimpleStatement)method.get().allStatements().get(0)).getLeft());
        Invocation invocation2 = (Invocation)(((SimpleStatement)method.get().allStatements().get(1)).getLeft());

        Marker marker = getFirstMarker(CHECK_ID, invocation, getProject());
        Marker marker2 = getFirstMarker(CHECK_ID, invocation2, getProject());

        int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
        int val2 = marker2.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
        assertTrue(val == 3 || val == 4);
        assertTrue(val2 == 3 || val2 == 4);
    }

}
