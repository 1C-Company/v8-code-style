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
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.SubsystemSynonymTooLongCheck;

/**
 * Tests for {@link SubsystemSynonymTooLongCheck} class
 *
 * @author Denis Maslennikov
 *
 */
public class SubsystemSynonymTooLongCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "subsystem-synonym-too-long"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "SubsystemSynonymTooLong";

    /**
     * Test that subsystem synonym length more than maximal length for top-level subsystems.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSubsystemSynonymTooLong() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Subsystem.TopLongIncluded", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test ignore if subsystem synonym length less than maximal length or not top-level subsystem.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSubsystemSynonymTooLongIgnore() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Subsystem.TopShort", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Subsystem.TopLongIncluded.Subsystem.SubLong", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test ignore if subsystem synonym length more than maximal length for excluded languages.
     *
     * @throws Exception the exceptionr
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
     * Test ignore subsystem not included in command interface
     *
     * @throws Exception the exceptionr
     */
    @Test
    public void testSubsystemNonIncludedToInterfaceIgnore() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Subsystem.TopLongNotIncluded", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

}
