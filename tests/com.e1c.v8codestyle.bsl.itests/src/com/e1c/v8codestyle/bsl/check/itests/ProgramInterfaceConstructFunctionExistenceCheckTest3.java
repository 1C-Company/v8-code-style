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

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.ProgramInterfaceConstructFunctionExistenceCheck;

/**
 * The test for @link {ProgramInterfaceConstructFunctionExistenceCheck}
 * Test on localization common module
 * 
 * @author Artem Samohvalov
 */
public class ProgramInterfaceConstructFunctionExistenceCheckTest3
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "CommonModuleLocalization";
    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModuleLocalization/Module.bsl";

    public ProgramInterfaceConstructFunctionExistenceCheckTest3()
    {
        super(ProgramInterfaceConstructFunctionExistenceCheck.class);
    }

    /**
     * Test no error 
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoConstruct() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "program-interface-no-construct-function.bsl");

        List<Marker> markers = getModuleMarkers();

        assertEquals(0, markers.size());
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return COMMON_MODULE_FILE_NAME;
    }
}
