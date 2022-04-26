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
 *     Denis Maslennikov - issue #409
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.MissingTemporaryFileDeletionCheck;

/**
 * Test for class {@link MissingTemporaryFileDeletionCheck}.
 *
 * @author Denis Maslennikov
 */
public class MissingTemporaryFileDeletionCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "MissingTemporaryFileDeletion";

    private static final String MESSAGE = "Missing temporary file deletion after use."; //$NON-NLS-1$

    public MissingTemporaryFileDeletionCheckTest()
    {
        super(MissingTemporaryFileDeletionCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test missing temporary file deletion.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMissingTemporaryFileDeletion() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        Marker marker = markers.get(0);
        assertEquals(MESSAGE, marker.getMessage());
    }
}
