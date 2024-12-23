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

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.MultilineDescriptionParameterSuggestionCheck;

/**
 * Tests for {@link MultilineDescriptionParameterSuggestionCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class MultilineDescriptionParameterSuggestionCheckTest
    extends AbstractSingleModuleTestBase
{

    public MultilineDescriptionParameterSuggestionCheckTest()
    {
        super(MultilineDescriptionParameterSuggestionCheck.class);
    }

    /**
     * Test the documentation comment of method with parameters contains section for them.
     * If parameter name found in description - this means some wrong syntax in doc comment.
     *
     * @throws Exception the exception
     */
    @Test
    public void testParametersInDescription() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-parameter-in-description-suggestion.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

    }
}
