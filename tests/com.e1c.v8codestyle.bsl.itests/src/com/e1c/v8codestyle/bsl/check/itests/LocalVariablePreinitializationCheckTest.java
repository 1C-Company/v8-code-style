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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.LocalVariablePreinitializationCheck;

/**
 * The test for {@link LocalVariablePreinitializationCheck}
 * 
 * @author Artem Samohvalov
 */
public class LocalVariablePreinitializationCheckTest
    extends AbstractSingleModuleTestBase
{

    public LocalVariablePreinitializationCheckTest()
    {
        super(LocalVariablePreinitializationCheck.class);
    }

    /**
     * Test no error.
     * Has preinitialization.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoError() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "local-variable-pre-init.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test error.
     * Hasn't preinitialization.
     *
     * @throws Exception the exception
     */
    @Test
    public void testError() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-local-variable-pre-init.bsl");

        List<Integer> errorLines = List.of(7, 19, 30, 42, 53, 59, 65, 77, 88);

        List<Marker> markers = getModuleMarkers();
        assertEquals(errorLines.size(), markers.size());

        for (Marker marker : markers)
        {
            int val = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            assertTrue(errorLines.contains(val));
        }
    }
}
