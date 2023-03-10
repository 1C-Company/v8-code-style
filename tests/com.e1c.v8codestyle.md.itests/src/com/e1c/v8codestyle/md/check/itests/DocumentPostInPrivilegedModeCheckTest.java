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

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.DocumentPostInPrivilegedModeCheck;

/**
 * The test for class {@link DocumentPostInPrivilegedModeCheck}.
 *
 * @author Vadim Goncharov
 */
public class DocumentPostInPrivilegedModeCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "document-post-in-privileged-mode";

    private static final String PROJECT_NAME = "DocumentPostInPrivilegedMode";

    /**
     * Test Documents, that allow post and don't post/unpost in privileged mode.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDocumentNotPostUnpostInPrivilegedMode() throws Exception
    {

        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        
        // Allow post, not unpost in priv. mode
        long id = getTopObjectIdByFqn("Document.TestDocument1", project);
        Marker marker = getFirstMarker(CHECK_ID, id, project);
        assertNotNull(marker);
        
        // Allow post, not post in priv. mode
        id = getTopObjectIdByFqn("Document.TestDocument2", project);
        marker = getFirstMarker(CHECK_ID, id, project);
        assertNotNull(marker);

    }
    
    /**
     * Test Documents, that don't allow post or allow post and post/unpost in privileged mode.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDocumentPostUnpostInProvolegedMode() throws Exception
    {
        
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        
        // Deny post
        long id = getTopObjectIdByFqn("Document.TestDocument3", project);
        Marker marker = getFirstMarker(CHECK_ID, id, project);
        assertNull(marker);

        // Allow post, post & unpost in priv. mode
        id = getTopObjectIdByFqn("Document.TestDocument4", project);
        marker = getFirstMarker(CHECK_ID, id, project);
        assertNull(marker);
    }

}
