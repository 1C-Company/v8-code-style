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
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.ExportProcedureMissingCommentCheck;

/**
 * Tests for {@link ExportProcedureMissingCommentCheck} check.
 *
 * @author Olga Bozhko
 */
public class ExportProcedureMissingCommentCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String EXPORT_PROCEDURE_NAME = "ExportProcedureWithoutComment";

    public ExportProcedureMissingCommentCheckTest()
    {
        super(ExportProcedureMissingCommentCheck.class);
    }

    /**
     * Test the comment is added to the export procedure
     *
     * @param @throws Exception the exceptions
     */
    @Test
    public void testExportMethodWithComment() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "export-procedure-with-comment.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test the comment is not added to the export procedure
     *
     * @param @throws Exception the exceptions
     */
    @Test
    public void testExportMethodWithoutComment() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "export-procedure-no-comment.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        assertEquals(Integer.valueOf(3), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(MessageFormat.format(Messages.ExportProcedureMissingCommentCheck_Export_procedure_missing_comment,
            EXPORT_PROCEDURE_NAME), markers.get(0).getMessage());
    }

    /**
     * Test the comment is not added to the export procedure located not in Public Region
     *
     * @param @throws Exception the exceptions
     */
    @Test
    public void testNonPublicExportMethodWithoutComment() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "non-public-export-procedure-no-comment.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }
}
