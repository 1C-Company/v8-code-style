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
import com.e1c.v8codestyle.bsl.check.CommitTransactionCheck;

/**
 * Tests for {@link CommitTransactionCheck} check.
 *
 * @author Artem Iliukhin
 */
public class CommitTransactionCheckTest
    extends AbstractSingleModuleTestBase
{

    /**
     * Instantiates a new commit transaction check test.
     */
    public CommitTransactionCheckTest()
    {
        super(CommitTransactionCheck.class);
    }

    @Test
    public void testCommitTransactionMustBeInTtryCatch() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "commit-transaction-must-be-in-try-catch.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Messages.CommitTransactionCheck_Commit_transaction_must_be_in_try_catch, marker.getMessage());
    }

    @Test
    public void testNoBeginTransactionForCommitTransaction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-begin-transaction-for-commit-transaction.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Messages.CommitTransactionCheck_No_begin_transaction_for_commit_transaction, marker.getMessage());
    }

    @Test
    public void testNoRollbackTransactionForBeginTransaction() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-rollback-transaction-for-begin-transaction.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Messages.CommitTransactionCheck_No_rollback_transaction_for_begin_transaction,
            marker.getMessage());
    }

    @Test
    public void testShouldBeNoExecutableCodebetweenCommitAndException() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "should-be-no-executable-code-between-commit-and-exception.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Messages.CommitTransactionCheck_Should_be_no_executable_code_between_commit_and_exception,
            marker.getMessage());
    }

}
