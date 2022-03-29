/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogAttribute;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.DbObjectRefNonRefTypesCheck;

/**
 * Tests for {@link DbObjectRefNonRefTypesCheck} check
 *
 * @author Artem Iliukhin
 *
 */
public final class DbObjectAnyRefNonRefTypeCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "db-object-anyref-type"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "MdCompositeTypeCheck";

    /**
     * Test that attribute has anyRef type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAnyRef() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn("Catalog.Test", dtProject);
        if (object instanceof Catalog)
        {
            CatalogAttribute att = ((Catalog)object).getAttributes().get(0);
            Marker marker = getFirstMarker(CHECK_ID, att, dtProject);
            assertNotNull(marker);
        }
    }

}
