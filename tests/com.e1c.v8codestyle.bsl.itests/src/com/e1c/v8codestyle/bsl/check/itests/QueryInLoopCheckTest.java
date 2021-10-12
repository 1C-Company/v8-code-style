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

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.v8codestyle.bsl.check.QueryInLoopCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link QueryInLoopCheck} check.
 *
 * @author Aleksandr Kapralov
 */
public class QueryInLoopCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PARAM_CHECK_QUERIY_IN_INFINITE_LOOP = "checkQueryInInfiniteLoop"; //$NON-NLS-1$

    public QueryInLoopCheckTest()
    {
        super(QueryInLoopCheck.class);
    }

    @Test
    public void testQueryInLoop() throws Exception
    {
        Module module = updateAndGetModule(FOLDER_RESOURCE + "query-in-loop.bsl");

        List<Marker> markers = getModuleMarkers();

        Set<String> uriErrors = new HashSet<>();

        for (Method method : module.allMethods())
        {
            List<FeatureAccess> featureAccessList = EcoreUtil2.eAllOfType(method, FeatureAccess.class);

            switch (method.getName())
            {
            case "QueryCorrect":
            case "QueryExecutionCorrect":
            case "MethodCallsQueryCorrect":
            case "MethodCallsIncorrectMethodCorrect":
            case "GetNewQuery":
                {
                    // Those methods doesn't have errors
                    break;
                }

            case "ForEachStatementIncorrect":
                {
                    assertEquals(14, featureAccessList.size());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(8)).toString());
                    break;
                }
            case "ForToStatementIncorrect":
                {
                    assertEquals(12, featureAccessList.size());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(7)).toString());
                    break;
                }
            case "WhileStatementIncorrect":
                {
                    assertEquals(9, featureAccessList.size());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(3)).toString());
                    break;
                }
            case "LoopCallsMethodIncorrect":
                {
                    assertEquals(9, featureAccessList.size());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(4)).toString());
                    break;
                }
            case "LoopCallsMethodWithOtherMethodIncorrect":
                {
                    assertEquals(6, featureAccessList.size());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(3)).toString());
                    break;
                }
            case "LoopWithConditionsIncorrect":
                {
                    assertEquals(12, featureAccessList.size());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(2)).toString());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(6)).toString());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(8)).toString());
                    break;
                }
            case "QueryTypeFromFunctionIncorrect":
                {
                    assertEquals(9, featureAccessList.size());
                    uriErrors.add(EcoreUtil.getURI(featureAccessList.get(2)).toString());
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
            String uriToProblem = marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY);
            assertTrue(uriErrors.contains(uriToProblem));
            uriErrors.remove(uriToProblem);
        }

        assertEquals(0, uriErrors.size());
    }

    private CheckUid cuid(String checkId)
    {
        return new CheckUid(checkId, BslPlugin.PLUGIN_ID);
    }

    @Test
    public void testInfiniteLoop() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "query-in-loop-infinite.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());

        IProject project = getProject().getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(cuid(getCheckId()), project);
        settings.getParameters().get(PARAM_CHECK_QUERIY_IN_INFINITE_LOOP).setValue(Boolean.toString(true));
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(getProject());
        updateModule(FOLDER_RESOURCE + "query-in-loop-infinite.bsl");

        markers = getModuleMarkers();
        assertEquals(1, markers.size());

    }
}
