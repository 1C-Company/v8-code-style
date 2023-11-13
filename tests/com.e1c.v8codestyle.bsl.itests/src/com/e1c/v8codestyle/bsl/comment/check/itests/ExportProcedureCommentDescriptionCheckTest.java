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
package com.e1c.v8codestyle.bsl.comment.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.ExportProcedureCommentDescriptionCheck;

/**
 * Tests for {@link ExportProcedureCommentDescriptionCheck} check.
 *
 * @author Olga Bozhko
 */
public class ExportProcedureCommentDescriptionCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String MESSAGE =
        "Missing Description section in the comment for export procedure (function) {0}";
    private static final String EXPORT_FUNCTION_NAME = "\"RolesAvailable\"";

    public ExportProcedureCommentDescriptionCheckTest()
    {
        super(ExportProcedureCommentDescriptionCheck.class);
    }

    /**
     * Test the documentation comment for export procedure/functiion contains description section
     *
     * @throws Exception the exception
     */
    @Test
    public void testFunctionHasDescriptionSection() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-export-function-with-description-section.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test the documentation comment for export procedure/function doesn't contain description section
     *
     * @throws Exception the exception
     */
    @Test
    @Ignore("https://github.com/1C-Company/v8-code-style/issues/1377")
    public void testFunctionHasNoDescriptionSection() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-export-function-no-description-section.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("14", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(MessageFormat.format(MESSAGE, EXPORT_FUNCTION_NAME), marker.getMessage());
    }
}
