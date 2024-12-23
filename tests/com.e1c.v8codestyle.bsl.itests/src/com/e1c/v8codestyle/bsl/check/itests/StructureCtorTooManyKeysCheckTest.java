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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.StructureCtorTooManyKeysCheck;

/**
 * Tests for {@link StructureCtorTooManyKeysCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class StructureCtorTooManyKeysCheckTest
    extends AbstractSingleModuleTestBase
{

    public StructureCtorTooManyKeysCheckTest()
    {
        super(StructureCtorTooManyKeysCheck.class);
    }

    /**
     * Test the second string literal has error
     *
     * @throws Exception the exception
     */
    @Test
    public void testStructureConstructorKeys() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "structure-consructor-too-many-keys.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(11), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

    }
}
