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
import com.e1c.v8codestyle.bsl.check.ChangeAndValidateInsteadOfAroundCheck;

/**
 * Tests for {@link ChangeAndValidateInsteadOfAroundCheck} check.
 *
 * @author Aleksandr Kapralov
 */
public class ChangeAndValidateInsteadOfAroundCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String EXTENSION_NAME = "ChangeAndValidateInsteadOfAroundExtension";

    public ChangeAndValidateInsteadOfAroundCheckTest()
    {
        super(ChangeAndValidateInsteadOfAroundCheck.class);
    }

    @Test
    public void testChangeAndValidateInsteadOfAround() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "change-and-validate-instead-of-around.bsl");

        IDtProject dtProject = openProjectAndWaitForValidationFinish(EXTENSION_NAME);
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
            case "Ext_MyFunction":
                {
                    // Those methods doesn't have errors
                    break;
                }

            case "Ext_MyProcedure":
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
            if (!getCheckId().equals(checkUid))
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
