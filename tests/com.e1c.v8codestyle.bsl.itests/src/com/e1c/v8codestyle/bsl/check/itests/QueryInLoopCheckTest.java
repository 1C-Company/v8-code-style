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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.e1c.g5.v8.dt.check.WrongParameterException;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.QueryInLoopCheck;

/**
 * Tests for {@link QueryInLoopCheck} check.
 *
 * @author Aleksandr Kapralov
 */
public class QueryInLoopCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String PROJECT_NAME = "QueryInLoop";

    private static final String CHECK_ID = "query-in-loop"; //$NON-NLS-1$

    @Test
    public void testQueryInLoop() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
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
                    // Методы без ошибок, пропускаем
                    break;
                }

            case "ForEachStatementIncorrect":
                {
                    assertEquals(4, statements.size());
                    uriErrors.add(EcoreUtil.getURI(statements.get(3)).toString());
                    break;
                }
            case "ForToStatementIncorrect":
                {
                    assertEquals(4, statements.size());
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
                    assertEquals(3, statements.size());
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
                    throw new WrongParameterException(
                        MessageFormat.format("Unknown method name {0}", method.getName()));
                }
            }
        }

        for (Marker marker : markers)
        {
            CheckUid checkUid = checkRepository.getUidForShortUid(marker.getCheckId(), dtProject.getWorkspaceProject());
            assertNotNull(checkUid);
            assertTrue(CHECK_ID.equals(checkUid.getCheckId()));

            String uriToProblem = marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY);
            assertTrue(uriErrors.contains(uriToProblem));
            uriErrors.remove(uriToProblem);
        }

        assertEquals(0, uriErrors.size());
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }
}
