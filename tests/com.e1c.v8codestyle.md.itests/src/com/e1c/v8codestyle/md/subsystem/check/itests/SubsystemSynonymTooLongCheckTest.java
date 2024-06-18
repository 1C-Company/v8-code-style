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
 *     Denis Maslennikov- issue #37
 *******************************************************************************/
package com.e1c.v8codestyle.md.subsystem.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Subsystem;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.subsystem.check.SubsystemSynonymTooLongCheck;

/**
 * Tests for {@link SubsystemSynonymTooLongCheck} class
 *
 * @author Denis Maslennikov
 */
public class SubsystemSynonymTooLongCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "subsystem-synonym-too-long"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "SubsystemSynonymTooLong";

    private boolean cuid(String checkId, IDtProject dtProject)
    {
        CheckUid checkUid = checkRepository.getUidForShortUid(checkId, dtProject);
        return checkUid != null && CHECK_ID.equals(checkUid.getCheckId());
    }

    private long countMarkers(IDtProject dtProject, IBmObject object)
    {
        return Arrays.asList(markerManager.getMarkers(dtProject.getWorkspaceProject(), object.bmGetId()))
            .stream()
            .filter(marker -> cuid(marker.getCheckId(), dtProject))
            .count();
    }

    /**
     * Test that top-level subsystem has two markers when it included to command interface,
     * synonym lengths for languages "ru", "en", "de" is more than maximal length.
     * Note: "de" is set as excluded filter.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSubsystemSynonymTooLongRuEnDe() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn("Subsystem.TopLongRuEnDeIncluded", dtProject);

        if (object instanceof Subsystem)
        {
            assertEquals(countMarkers(dtProject, object), 2);
        }
    }

    /**
     * Test that top-level subsystem has one markers when it included to command interface,
     * synonym length for language "en" is more than maximal length.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSubsystemSynonymTooLongEn() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn("Subsystem.TopLongEnIncluded", dtProject);

        if (object instanceof Subsystem)
        {
            assertEquals(countMarkers(dtProject, object), 1);
        }
    }

    /**
     * Test that the subsystem does not have marker, but it included to command interface,
     * synonym length only for for language "de" is more than maximal length.
     * Note: "de" is set as excluded filter.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSubsystemSynonymTooLongIgnoreDe() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Subsystem.TopLongDeIncluded", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test that the subsystem does not have marker in the following cases:
     * - subsystem is top-level, synonym length for all languages is less than maximal length;
     * - subsystem is not top-level, synonym length for "en" is more than maximal length;
     * - subsystem in not included in command interface;
     *
     * @throws Exception the exception
     */
    @Test
    public void testSubsystemSynonymTooLongIgnore() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Subsystem.TopShortEnRuDe", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Subsystem.TopLongEnIncluded.Subsystem.SubLong", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Subsystem.TopLongNotIncluded", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }
}
