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

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.UnsafePasswordStorageCheck;

/**
 * The test for class {@link UnsafePasswordStorageCheck}.
 *
 * @author Artem Iliukhin
 */
public class UnsafePasswordStorageCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "unsafe-password-ib-storage"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "UnsafePasswordStorage";

    @Test
    public void unsafePasswordStorageConstantCheck() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Constant.Constant", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void unsafePasswordStorageDocumentCheck() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Document.Document", dtProject);
        Marker marker = getFirstNestedMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void unsafePasswordStorageCatalogCheck() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.Catalog", dtProject);
        Marker marker = getFirstNestedMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }
}
