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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.g5.v8.dt.check.ICheck;

/**
 * The base of the tests that load the module code inline instead of a bundle resource.
 *
 * @author Artem Samohvalov
 */
public abstract class AbstractInlineSingleModuleTestBase
    extends AbstractSingleModuleTestBase
{
    /**
     * Instantiates a new abstract inline single module test base.
     *
     * @param checkClass the check class, cannot be {@code null}
     */
    protected AbstractInlineSingleModuleTestBase(Class<? extends ICheck<?>> checkClass)
    {
        super(checkClass);
    }

    /**
     * Loads the module code and waits until validation finished.
     *
     * @param bsl the code of the module, cannot be {@code null}
     * @throws CoreException the core exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void loadModuleCode(String bsl) throws CoreException, IOException
    {
        writeModuleContent(bsl.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Assert markers equal.
     *
     * @param errorLines the error lines
     */
    protected void assertMarkersEqual(List<Integer> errorLines)
    {
        assertEquals(errorLines.stream().sorted().toList(), getMarkerLines().stream().sorted().toList());
    }

    /**
     * Assert markers empty.
     */
    protected void assertMarkersEmpty()
    {
        List<Integer> markers = getMarkerLines();
        assertTrue("Expected no markers, but found: " + markers, markers.isEmpty());
    }

    /**
     * Assert marker equal.
     *
     * @param errorLine the error line
     */
    protected void assertMarkerEqual(int errorLine)
    {
        List<Integer> errorLines = getMarkerLines();
        assertEquals("Expected one marker, but found: " + errorLines, errorLines.size(), 1);
        assertEquals(Integer.valueOf(errorLine), errorLines.get(0));
    }

    /**
     * Gets the marker lines.
     *
     * @return the marker lines
     */
    protected List<Integer> getMarkerLines()
    {
        return getModuleMarkers().stream()
            .map(m -> m.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .filter(Integer.class::isInstance)
            .map(Integer.class::cast)
            .toList();
    }
}
