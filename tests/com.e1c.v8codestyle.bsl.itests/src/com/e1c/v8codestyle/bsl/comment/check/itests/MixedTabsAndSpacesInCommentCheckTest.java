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
package com.e1c.v8codestyle.bsl.comment.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.MixedTabsAndSpacesInCommentCheck;

/**
 * Test for {@link MixedTabsAndSpacesInCommentCheck}
 * 
 * @author Artem Samohvalov
 */
public class MixedTabsAndSpacesInCommentCheckTest
    extends AbstractSingleModuleTestBase
{

    public MixedTabsAndSpacesInCommentCheckTest()
    {
        super(MixedTabsAndSpacesInCommentCheck.class);
    }

    /**
     * Test no error
     * Test valid indents in sections.
     *
     * @throws Exception the exception
     */
    @Test
    public void testValidIndentsInSections() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "valid-indents-in-sections.bsl");

        List<Marker> markers = getModuleMarkers();

        assertTrue(markers.isEmpty());
    }

    /**
     * Test error
     * Test mixed tab and spaces in doc comment
     *
     * @throws Exception the exception
     */
    @Test
    public void testMixedTabAndSpacesInDocComment() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "mixed-tab-and-spaces-in-doc-comment.bsl");

        List<Marker> markers = getModuleMarkers();

        assertEquals(9, markers.size());

        for (Marker marker : markers)
        {
            int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            assertTrue(val == 3 || val == 4 || val == 11 || val == 17 || val == 27 || val == 37 || val == 38
                || val == 45 || val == 46);
        }
    }
}
