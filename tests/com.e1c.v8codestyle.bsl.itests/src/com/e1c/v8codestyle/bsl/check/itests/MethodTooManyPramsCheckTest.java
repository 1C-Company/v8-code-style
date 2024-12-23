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
import static org.junit.Assert.assertFalse;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.MethodTooManyPramsCheck;

/**
 * The test of {@link MethodTooManyPramsCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class MethodTooManyPramsCheckTest
    extends AbstractSingleModuleTestBase
{

    public MethodTooManyPramsCheckTest()
    {
        super(MethodTooManyPramsCheck.class);
    }

    /**
     * Test method has too many parameters.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMethodHasTooManyParameters() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "method-too-many-params.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(12, markers.size());

        Set<Integer> errorLines = new HashSet<>();
        for (Marker marker : markers)
        {
            Integer line = marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE);
            switch (line)
            {
            // Noncompliant1
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
                // Noncompliant2
            case 24:
                // Noncompliant3: 29, 30, 31 - not error yet
                // Noncompliant4
            case 40:
                errorLines.add(line);
                break;

            default:
                String message =
                    MessageFormat.format("Unexpected erron on line: {0} and message: {1}", line, marker.getMessage());
                assertFalse(message, true);
                break;
            }
        }

        // check found all errors
        assertEquals(8, errorLines.size());

    }

}
