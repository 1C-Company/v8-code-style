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
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.v8codestyle.bsl.check.EventHandlerBooleanParamCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Test {@link EventHandlerBooleanParamCheck} check that find problems of use boolean parameter.
 * This test disable check's parameter {@code checkEventOnly}.
 *
 * @author Dmitriy Marmyshev
 */
public class EventHandlerBooleanParamCheckNotEventOnlyTest
    extends EventHandlerBooleanParamCheckTest
{
    private static final String PARAM_CHECK_EVENT_ONLY = "checkEventOnly"; //$NON-NLS-1$

    @Before
    public void disableCheckParameterEventOnly() throws CoreException
    {

        IDtProject dtProject = dtProjectManager.getDtProject(getTestConfigurationName());
        IProject project = dtProject.getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(cuid(CHECK_ID), project);
        settings.getParameters().get(PARAM_CHECK_EVENT_ONLY).setValue(Boolean.FALSE.toString());
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(dtProject);
    }

    /**
     * Check boolean parameter in Common module event handlers of event subscription that use is correct.
     * Also checks usual method which parameter name equals with checking name.
     *
     * @throws Exception the exception
     */
    @Override
    @Test
    public void testCommonModuleEvents() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(getTestConfigurationName());
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_COMMON_MODULE, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals("6", marker.getExtraInfo().get("line"));

        // Noncompliant works only if enabled in check's parameters
        noncompliantMethod = methods.get(1);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals("12", marker.getExtraInfo().get("line"));

        Method compliantMethod = methods.get(2);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    private CheckUid cuid(String checkId)
    {
        return new CheckUid(checkId, BslPlugin.PLUGIN_ID);
    }

}
