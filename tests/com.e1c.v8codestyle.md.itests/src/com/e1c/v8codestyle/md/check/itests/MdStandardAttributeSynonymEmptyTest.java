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
 *     Bombin Valentin - issue #119
 *******************************************************************************/
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.StandardAttribute;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;

/**
 * The test for class {@link MdStandardAttributeSynonymEmpty}.
 *
 * @author Bombin Valentin
 * @author Dmitriy Marmyshev
 */
public class MdStandardAttributeSynonymEmptyTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "md-standard-attribute-synonym-empty"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "MdStandardAttributeSynonymEmpty";

    /**
     * Test MD-Object has synonym property for attribute parent or owner
     *
     * @throws Exception the exception
     */
    @Test
    public void testPositiveTest() throws Exception
    {
        IDtProject dtProject = getProject();

        long id = getTopObjectIdByFqn("Catalog.PositiveParentTest", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.PositiveOwnerTest", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

    }

    @Test
    public void testNegativeOwner() throws Exception
    {
        IDtProject dtProject = getProject();

        Long id = getTopObjectIdByFqn("Catalog.NegativeOwnerTest", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        Catalog catalog = (Catalog)getTopObjectByFqn("Catalog.NegativeOwnerTestWithComment", dtProject);
        StandardAttribute attribute = catalog.getStandardAttributes().get(0);
        marker = getFirstMarker(CHECK_ID, attribute, dtProject);
        assertNotNull(marker);

    }

    @Test
    public void testNegativeParent() throws Exception
    {
        IDtProject dtProject = getProject();

        Long id = getTopObjectIdByFqn("Catalog.NegativeParentTest", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        Catalog catalog = (Catalog)getTopObjectByFqn("Catalog.NegativeParentTestWithComment", dtProject);
        StandardAttribute attribute = catalog.getStandardAttributes().get(0);
        marker = getFirstMarker(CHECK_ID, attribute, dtProject);
        assertNotNull(marker);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

}
