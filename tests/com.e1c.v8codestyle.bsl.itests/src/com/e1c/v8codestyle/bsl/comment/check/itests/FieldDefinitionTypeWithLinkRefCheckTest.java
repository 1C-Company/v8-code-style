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
import com.e1c.v8codestyle.bsl.comment.check.FieldDefinitionTypeWithLinkRefCheck;

/**
 * Tests for {@link FieldDefinitionTypeWithLinkRefCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class FieldDefinitionTypeWithLinkRefCheckTest
    extends AbstractSingleModuleTestBase
{

    public FieldDefinitionTypeWithLinkRefCheckTest()
    {
        super(FieldDefinitionTypeWithLinkRefCheck.class);
    }

    /**
     * Test the documentation comment that field definition has type section with complex type
     * and lint to the type constructor.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFieldDefHasTypeAndLink() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-complex-type-with-link.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals("3", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
