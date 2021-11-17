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
 *     Bombin Valentin - issue #462
 *******************************************************************************/
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com._1c.g5.v8.bm.core.BmObject;
import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdCyrillicYoLetterInProperty;

/**
 * The test for class {@link MdCyrillicYoLetterInProperty}.
 *
 * @author Bombin Valentin
 */
public class MdCyrillicYoLetterInPropertyTestEn
    extends CheckTestBase
{

    private static final String CHECK_ID = "md-cyrillic-yo-letter"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "MdCyrillicYoLetterInPropertyEn";

    /**
     * Test MD-Object has cyryllic yo letter for English default lang
     *
     * @throws Exception the exception
     */
    @Test
    public void testPositiveTest() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.PositiveCatalog", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.NegativeCatalog_Name_HasЁ", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.NegativeCatalog_Comment", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.PositiveCatalog_Attribute", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.NegativeCatalog_Attribute_Name", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.NegativeCatalog_Attribute_Comment", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.PositiveCatalog_TabularSection", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.NegativeCatalog_TabularSection_Name", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.NegativeCatalog_TabularSection_Comment", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Document.PositiveDocument", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Document.NegativeDocument_Name_HasЁ", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Document.NegativeDocument_Comment", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

    }

    @Test
    public void testNegativeTest() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.NegativeCatalog_Synonym", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        id = getFirstAttributeId_Catalog(dtProject, "Catalog.NegativeCatalog_Attribute_Synonym");
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        id = getFirstTabularId_Catalog(dtProject, "Catalog.NegativeCatalog_TabularSection_Synonym");
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        id = getTopObjectIdByFqn("Document.NegativeDocument_Synonym", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

    }

    /**
     * @param dtProject
     * @param name
     * @return
     */
    private long getFirstAttributeId_Catalog(IDtProject dtProject, String name)
    {
        long id;
        BmObject object = (BmObject)getTopObjectByFqn(name, dtProject);
        assertTrue(object instanceof Catalog);
        Catalog catalog = (Catalog)object;
        id = ((IBmObject)catalog.getAttributes().get(0)).bmGetId();
        return id;
    }

    /**
     * @param dtProject
     * @param name
     * @return
     */
    private long getFirstTabularId_Catalog(IDtProject dtProject, String name)
    {
        long id;
        BmObject object = (BmObject)getTopObjectByFqn(name, dtProject);
        assertTrue(object instanceof Catalog);
        Catalog catalog = (Catalog)object;
        id = ((IBmObject)catalog.getTabularSections().get(0)).bmGetId();
        return id;
    }
}
