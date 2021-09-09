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
package com.e1c.v8codestyle.md.itests;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdoObjectListPresentationCheck;

/**
 * The test for class {@link MdoObjectListPresentationCheck}.
 *
 * @author Dmitriy Marmyshev
 */
public class MdoObjectListPresentationCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "mdo-object-list-presentation"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "MdoObjectListPresentation";

    /**
     * Test MD-Object has object presentation and list presentation are empty.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdoObjectAndListPresentationIsEmpty() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.Products", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        id = getTopObjectIdByFqn("InformationRegister.Prices", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

}
