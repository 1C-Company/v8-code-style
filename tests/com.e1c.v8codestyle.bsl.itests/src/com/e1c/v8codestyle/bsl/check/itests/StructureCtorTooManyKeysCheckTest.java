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
/**
 *
 */
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.xtext.EcoreUtil2;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;

/**
 * Tests for {@link ConfigurationDataLock} check.
 *
 * @author Dmitriy Marmyshev
 */
public class StructureCtorTooManyKeysCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "structure-consructor-too-many-keys"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "StructureCtorTooManyKeys";

    /**
     * Test the second string literal has error
     *
     * @throws Exception the exception
     */
    @Test
    public void testStructureConstructorKeys() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn("CommonModule.CommonModule", dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<StringLiteral> literals = EcoreUtil2.eAllOfType(module, StringLiteral.class);
        assertEquals(3, literals.size());

        String id = module.eResource().getURI().toString();
        Marker[] markers = markerManager.getMarkers(dtProject.getWorkspaceProject(), id);
        assertNotNull(markers);

        // check incorrect string literal
        assertEquals(1, markers.length);

        Marker marker = markers[0];
        assertEquals("11", marker.getExtraInfo().get("line"));
        String uriToProblem = EcoreUtil2.getURI(literals.get(1)).toString();
        assertEquals(uriToProblem, marker.getExtraInfo().get("uriToProblem"));
        CheckUid checkUid =
            this.checkRepository.getUidForShortUid(marker.getCheckId(), dtProject.getWorkspaceProject());
        assertNotNull(checkUid);
        assertTrue(CHECK_ID.equals(checkUid.getCheckId()));

    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }
}
