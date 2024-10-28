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
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.comment.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.DocCommentUseMinusCheck;

/**
 * Tests for {@link DocCommentUseMinusCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class DocCommentUseMinusCheckTest
    extends AbstractSingleModuleTestBase
{

    public DocCommentUseMinusCheckTest()
    {
        super(DocCommentUseMinusCheck.class);
    }

    /**
     * Test the documentation comment used correct hyphen-minus
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvalidMinusUsed() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-use-minus.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(3, 4), errorLines);

    }
}
