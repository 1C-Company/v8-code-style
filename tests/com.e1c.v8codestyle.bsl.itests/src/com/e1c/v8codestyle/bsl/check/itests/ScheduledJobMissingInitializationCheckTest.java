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
import java.util.Set;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.ScheduledJobMissingInitializationCheck;

/**
 * The test for check {@link ScheduledJobMissingInitializationCheck}
 * 
 * @author Artem Samohvalov
 */
public class ScheduledJobMissingInitializationCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "ScheduledJobMissingInitializationCheck";

    public ScheduledJobMissingInitializationCheckTest()
    {
        super(ScheduledJobMissingInitializationCheck.class);
    }

    /**
     * Test error.
     *
     * @throws Exception the exception
     */
    @Test
    public void test() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        Set<Integer> errorLines = Set.of(1, 14);

        assertEquals(errorLines.size(), markers.size());
        markers.forEach(m -> assertTrue(errorLines.contains(m.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))));
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }
}
