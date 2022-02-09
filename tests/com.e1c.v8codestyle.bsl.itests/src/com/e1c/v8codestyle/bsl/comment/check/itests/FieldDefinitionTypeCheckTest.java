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
import com.e1c.v8codestyle.bsl.comment.check.FieldDefinitionTypeCheck;

/**
 * Tests for {@link FieldDefinitionTypeCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class FieldDefinitionTypeCheckTest
    extends AbstractSingleModuleTestBase
{

    public FieldDefinitionTypeCheckTest()
    {
        super(FieldDefinitionTypeCheck.class);
    }

    /**
     * Test the documentation comment field definition has type section
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvalidFieldType() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-field-type.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("4", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
