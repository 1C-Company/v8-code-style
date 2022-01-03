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
    private static final String LOCK_ROLLBACK_TRANSACTION =
        "In the Exception block, you must first call the RollbackTransaction() method";
    private static final String LOCK_COMMIT_TRANSACTION =
        "CommitTransaction() method should be the last in the Try block before the Exception statement to ensure that no exception occurs after CommitTransaction()";
    private static final String LOCK_BEGIN_TRANSACTION =
        "BeginTransaction() method must be outside the Try-Exception block immediately before the Try statement";

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
    public void testBeginTransaction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "lock-begin-transaction.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(LOCK_BEGIN_TRANSACTION, marker.getMessage());
    }

    @Test
    public void testCommitTransaction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "lock-commit-transaction.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(LOCK_COMMIT_TRANSACTION, marker.getMessage());
    }

    @Test
    public void testRollbackTransaction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "lock-rollback-transaction.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(LOCK_ROLLBACK_TRANSACTION, marker.getMessage());
    }

}
