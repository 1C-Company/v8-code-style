/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogAttribute;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.md.check.DbObjectMaxNumberLengthCheck;

/**
 * Tests for {@link DbObjectMaxNumberLengthCheck} check
 *
 * @author Dmitriy Marmyshev
 *
 */
public class DbObjectMaxNumberLengthCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "db-object-max-number-length"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "MdNumberMaxLength";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Check that numeric DB field maximum length is more then 31
     */
    @Test
    public void testNumberMaxLength()
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn("Catalog.Test", dtProject);
        if (object instanceof Catalog)
        {
            CatalogAttribute att = ((Catalog)object).getAttributes().get(0);
            Marker marker = getFirstMarker(CHECK_ID, att.getType().getNumberQualifiers(), dtProject);
            assertNull(marker);

            att = ((Catalog)object).getAttributes().get(1);
            marker = getFirstMarker(CHECK_ID, att.getType().getNumberQualifiers(), dtProject);
            assertNotNull(marker);

            att = ((Catalog)object).getAttributes().get(2);
            marker = getFirstMarker(CHECK_ID, att.getType(), dtProject);
            assertNull(marker);

            att = ((Catalog)object).getAttributes().get(3);
            marker = getFirstMarker(CHECK_ID, att.getType(), dtProject);
            assertNotNull(marker);
        }
    }

}
