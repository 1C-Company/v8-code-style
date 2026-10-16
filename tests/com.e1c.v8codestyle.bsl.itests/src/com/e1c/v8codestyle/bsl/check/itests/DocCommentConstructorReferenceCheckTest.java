/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.comment.check.DocCommentConstructorReferenceCheck;

/**
 * The test for {@link DocCommentConstructorReferenceCheck}
 */
public class DocCommentConstructorReferenceCheckTest
    extends AbstractSingleModuleTestBase
{

    public DocCommentConstructorReferenceCheckTest()
    {
        super(DocCommentConstructorReferenceCheck.class);
    }

    /**
     * Test no error.
     *
     * @throws Exception the exception
     */
    @Test
    public void testHasRefWithStruct() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-constructor-reference-in-params-struct.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test no error. no ref and no property call
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoRefAndNoPropertyCall() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-constructor-no-reference-in-params-struct-no-call.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test error. no ref with struct.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoRefWithStruct() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-constructor-no-reference-in-params-struct.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test error. no ref with value table.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoRefWithValueTable() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-constructor-no-reference-in-params-value-table.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test error. no ref with value tree.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoRefWithValueTree() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-constructor-no-reference-in-params-value-tree.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(3), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
