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
 *     Sergey Kozynskiy - issue #431
 *******************************************************************************/
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdScheduledJobDescriptionCheck;

/**
 * The test for class {@link MdScheduledJobDescriptionCheck}.
 *
 * @author Sergey Kozynskiy
 *
 */
public class ScheduledJobDescriptionCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "mdo-scheduled-job-description"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "ScheduledJobs";

    /**
     * Scheduled job description is empty if Scheduled job is predefined
     *
     * @throws Exception the exception
     */
    @Test
    public void MdScheduledJobDescriptionIsEmpty() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("ScheduledJob.PredefinedJob", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        id = getTopObjectIdByFqn("ScheduledJob.NotPredefinedJob", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }
}
