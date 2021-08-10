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
 *     Aleksandr Kapralov - initial API and implementation
 *******************************************************************************/
/**
 *
 */
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.bsl.check.QueryInLoopCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link QueryInLoopCheck} check.
 *
 * @author Aleksandr Kapralov
 */
public class QueryInLoopCheckTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "QueryInLoop";

    private static final String CHECK_ID = "query-in-loop"; //$NON-NLS-1$

    @Test
    public void testQueryInLoop() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn("CommonModule.CommonModule", dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        String id = module.eResource().getURI().toPlatformString(true);
        Marker[] markers = markerManager.getMarkers(dtProject.getWorkspaceProject(), id);
        assertNotNull(markers);

        Set<String> uriErrors = new HashSet<>();

        for (Method method : module.allMethods())
        {
            List<SimpleStatement> statements = EcoreUtil2.eAllOfType(method, SimpleStatement.class);

            switch (method.getName())
            {
            case "QueryCorrect":
            case "QueryExecutionCorrect":
            case "MethodCallsQueryCorrect":
            case "MethodCallsIncorrectMethodCorrect":
                {
                    // Those methods doesn't have errors
                    break;
                }

            case "ForEachStatementIncorrect":
                {
                    assertEquals(5, statements.size());
                    uriErrors.add(EcoreUtil.getURI(statements.get(3)).toString());
                    break;
                }
            case "ForToStatementIncorrect":
                {
                    assertEquals(5, statements.size());
                    uriErrors.add(EcoreUtil.getURI(statements.get(3)).toString());
                    break;
                }
            case "WhileStatementIncorrect":
                {
                    assertEquals(3, statements.size());
                    uriErrors.add(EcoreUtil.getURI(statements.get(2)).toString());
                    break;
                }
            case "LoopCallsMethodIncorrect":
                {
                    assertEquals(4, statements.size());
                    uriErrors.add(EcoreUtil.getURI(statements.get(2)).toString());
                    break;
                }
            case "LoopCallsMethodWithOtherMethodIncorrect":
                {
                    assertEquals(3, statements.size());
                    uriErrors.add(EcoreUtil.getURI(statements.get(2)).toString());
                    break;
                }
            case "LoopWithConditionsIncorrect":
                {
                    assertEquals(3, statements.size());
                    uriErrors.add(EcoreUtil.getURI(statements.get(0)).toString());
                    uriErrors.add(EcoreUtil.getURI(statements.get(1)).toString());
                    uriErrors.add(EcoreUtil.getURI(statements.get(2)).toString());
                    break;
                }

            default:
                {
                    throw new IllegalStateException(MessageFormat.format("Unknown method name {0}", method.getName()));
                }
            }
        }

        for (Marker marker : markers)
        {
            String checkUid = getCheckIdFromMarker(marker, dtProject);
            assertNotNull(checkUid);
            assertTrue(CHECK_ID.equals(checkUid));

            String uriToProblem = marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY);
            assertTrue(uriErrors.contains(uriToProblem));
            uriErrors.remove(uriToProblem);
        }

        assertEquals(0, uriErrors.size());
    }

    private static final String PARAM_CHECK_QUERIES_FOR_INFINITE_LOOPS = "checkQueriesForInfiniteLoops"; //$NON-NLS-1$

    private CheckUid cuid(String checkId)
    {
        return new CheckUid(checkId, BslPlugin.PLUGIN_ID);
    }

    @Test
    public void testInfiniteLoop() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn("CommonModule.InfiniteLoop", dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        String id = module.eResource().getURI().toPlatformString(true);
        Marker[] markers = markerManager.getMarkers(dtProject.getWorkspaceProject(), id);
        assertNotNull(markers);

        assertEquals(0, markers.length);

        IProject project = dtProject.getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(cuid(CHECK_ID), project);
        settings.getParameters().get(PARAM_CHECK_QUERIES_FOR_INFINITE_LOOPS).setValue(Boolean.toString(true));
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(dtProject);

        markers = markerManager.getMarkers(dtProject.getWorkspaceProject(), id);
        assertNotNull(markers);

        assertEquals(1, markers.length);

    }
}
