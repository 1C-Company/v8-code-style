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
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.bsl.check.CanonicalPragmaCheck;

/**
 * Tests for {@link CanonicalPragmaCheck} check.
 *
 * @author Aleksandr Kapralov
 */
public class CanonicalPragmaCheckTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "CanonicalPragmaExtension";

    private static final String CHECK_ID = "bsl-canonical-pragma"; //$NON-NLS-1$

    @Test
    public void testCanonicalPragma() throws Exception
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
            List<Pragma> pragmaList = EcoreUtil2.eAllOfType(method, Pragma.class);

            switch (method.getName())
            {
            case "Ext_MyCorrectProcedureBefore":
            case "Ext_MyCorrectProcedureAfter":
            case "Ext_MyCorrectFunctionAround":
            case "Ext_MyCorrectFunctionChangeAndValidate":
            case "UnknownPragma":
                {
                    // Those methods doesn't have errors
                    break;
                }

            case "Ext_MyIncorrectProcedureBefore":
            case "Ext_MyIncorrectProcedureAfter":
            case "Ext_MyIncorrectFunctionAround":
            case "Ext_MyIncorrectFunctionChangeAndValidate":
                {
                    assertEquals(1, pragmaList.size());
                    uriErrors.add(EcoreUtil.getURI(pragmaList.get(0)).toString());
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
            if (!CHECK_ID.equals(checkUid))
            {
                continue;
            }

            String uriToProblem = marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY);
            assertTrue(uriErrors.contains(uriToProblem));
            uriErrors.remove(uriToProblem);
        }

        assertEquals(0, uriErrors.size());
    }
}
