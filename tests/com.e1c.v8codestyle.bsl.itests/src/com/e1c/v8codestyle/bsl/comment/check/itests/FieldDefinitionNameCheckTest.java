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

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.FieldDefinitionNameCheck;

/**
 * Tests for {@link FieldDefinitionNameCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class FieldDefinitionNameCheckTest
    extends AbstractSingleModuleTestBase
{

    public FieldDefinitionNameCheckTest()
    {
        super(FieldDefinitionNameCheck.class);
    }

    /**
     * Test the documentation comment field name is correct name
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvalidFieldName() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-field-name.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("4", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test the documentation comment field name is unique name
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvalidFieldNameUnunique() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-field-name-ununique.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
