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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.LockOutOfTryCheck;

/**
 * Tests for {@link LockOutOfTryCheck} check.
 *
 * @author Artem Iliukhin
 */
public class LockOutOfTryCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String LOCK_OUT_OF_TRY = "Method Lock() out of try block";

    /**
     * Test {@link LockOutOfTryCheck}.
     *
     * @param checkClass
     */
    public LockOutOfTryCheckTest()
    {
        super(LockOutOfTryCheck.class);
    }

    @Test
    public void testLockOutOfTry() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "lock-out-of-try.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(LOCK_OUT_OF_TRY, marker.getMessage());
    }

    @Test
    public void testLockOutOfTry2() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "lock-out-of-try2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(LOCK_OUT_OF_TRY, marker.getMessage());
    }
}
