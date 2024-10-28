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
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdObjectNameUnallowedLetterCheck;

/**
 * Tests for {@link MdObjectNameUnallowedLetterCheck} check
 *
 * @author OlgaBozhko
 *
 */
public class MdObjectNameUnallowedLetterCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "mdo-ru-name-unallowed-letter"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "MdObjectNameUnallowedLetter";

    /**
     * Test that md object name, synonym and comment do not contain unallowed letter "ё" (Ru locale)
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdObjectNameNoUnallowedLetter() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.ТестовыйКаталог", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test that md object name contains unallowed letter "ё" (Ru locale)
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdObjectNameHasUnallowedLetter() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.ТестовыйКаталог_ё_имя", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test that md object synonym contains unallowed letter "ё" (Ru locale)
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdObjectSynonymHasUnallowedLetter() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.ТестовыйКаталог_синоним", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test that md object comment contains unallowed letter "ё" (Ru locale)
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdObjectCommentHasUnallowedLetter() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.ТестовыйКаталог_комментарий", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }
}
