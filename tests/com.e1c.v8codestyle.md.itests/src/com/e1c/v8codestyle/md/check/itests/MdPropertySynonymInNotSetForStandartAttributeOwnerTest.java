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
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdPropertySynonymInNotSetForStandartAttributeOwner;

/**
 * The test for class {@link MdPropertySynonymInNotSetForStandartAttributeOwner}.
 *
 * @author Bombin Valentin
 */
public class MdPropertySynonymInNotSetForStandartAttributeOwnerTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "MdPropertySynonymInNotSetForStandartAttributeOwner"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "MdPropertySynonymInNotSetForStandartAttributeOwner";

    /**
     * Test MD-Object has synonym property for attribute owner 
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdPropertySynonymInNotSetForStandartAttributeOwner() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.MyOwner", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
        
        id= getTopObjectIdByFqn("Catalog.PositiveTest", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.NegativeTestWithComment", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        id = getTopObjectIdByFqn("Catalog.NegativeTestWithEmpty", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

}
