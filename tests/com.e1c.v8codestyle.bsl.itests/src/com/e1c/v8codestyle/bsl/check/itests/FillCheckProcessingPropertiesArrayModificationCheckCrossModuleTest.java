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

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.FillCheckProcessingPropertiesArrayModificationCheck;

/**
 *  Tests for {@link FillCheckProcessingPropertiesArrayModificationCheck} check.
 *  Test check on another modules
 *
 *  @author Artem Samohvalov
 */
public class FillCheckProcessingPropertiesArrayModificationCheckCrossModuleTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "FillCheckProcessingPropertiesArrayModificationCheck";

    public FillCheckProcessingPropertiesArrayModificationCheckCrossModuleTest()
    {
        super(FillCheckProcessingPropertiesArrayModificationCheck.class);
    }

    /**
     * Test error
     *
     * @throws Exception the exception
     */
    @Test
    public void testError() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        Set<Integer> errorLines = Set.of(3, 4);

        assertTrue(markers.stream()
            .allMatch(marker -> errorLines.contains(marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))));
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }
}
