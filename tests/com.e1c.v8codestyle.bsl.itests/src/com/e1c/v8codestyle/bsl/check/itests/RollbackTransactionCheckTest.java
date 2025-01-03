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
import com.e1c.v8codestyle.bsl.check.RollbackTransactionCheck;

/**
 * Tests for {@link RollbackTransactionCheck} check.
 *
 * @author Artem Iliukhin
 */
public class RollbackTransactionCheckTest
    extends AbstractSingleModuleTestBase
{

    /**
     * Instantiates a new commit transaction check test.
     */
    public RollbackTransactionCheckTest()
    {
        super(RollbackTransactionCheck.class);
    }

    @Test
    public void testRollbackTransactionMustBeInTtryCatch() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "rollback-transaction-must-be-in-try-catch.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Messages.RollbackTransactionCheck_Rollback_transaction_must_be_in_try_catch, marker.getMessage());
    }

    @Test
    public void testNoBeginTransactionForRollbackTransaction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-begin-transaction-for-rollback-transaction.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Messages.RollbackTransactionCheck_No_begin_transaction_for_rollback_transaction,
            marker.getMessage());
    }

    @Test
    public void testNoCommitTransactionForBeginTransaction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-commit-transaction-for-begin-transaction.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Messages.RollbackTransactionCheck_No_commit_transaction_for_begin_transaction,
            marker.getMessage());
    }

    @Test
    public void testShouldBeNoExecutableCodebetweenExceptionAndRollback() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "should-be-no-executable-code-between-exception-and-rollback.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Messages.RollbackTransactionCheck_Should_be_no_executable_code_between_exception_and_rollback,
            marker.getMessage());
    }
}
