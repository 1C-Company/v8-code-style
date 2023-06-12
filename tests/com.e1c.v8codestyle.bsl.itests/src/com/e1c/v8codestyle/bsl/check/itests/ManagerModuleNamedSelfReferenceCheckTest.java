/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.ManagerModuleNamedSelfReferenceCheck;

/**
 * Test for the class {@link ManagerModuleNamedSelfReferenceCheck}
 *
 * @author Maxim Galios
 *
 */
public class ManagerModuleNamedSelfReferenceCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "ManagerModuleNamedSelfReferenceCheck";

    private static final String MANAGER_MODULE_FILE_NAME = "/src/Catalogs/ProductionStatus/ManagerModule.bsl";

    public ManagerModuleNamedSelfReferenceCheckTest()
    {
        super(ManagerModuleNamedSelfReferenceCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleId()
    {
        return Path.ROOT.append(getTestConfigurationName()).append(MANAGER_MODULE_FILE_NAME).toString();
    }

    /**
     * Check self reference in manager module
     *
     * @throws Exception
     */
    @Test
    public void testManagerModule() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        assertEquals(4, markers.size());

        assertEquals("6", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("6", markers.get(1).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("7", markers.get(2).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("7", markers.get(3).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
