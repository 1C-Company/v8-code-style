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
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.ProgramInterfaceConstructFunctionExistenceCheck;

/**
 * The test for @link {ProgramInterfaceConstructFunctionExistenceCheck}
 * Test on common module
 * 
 * @author Artem Samohvalov
 */
public class ProgramInterfaceConstructFunctionExistenceCheckTest1
    extends AbstractSingleModuleTestBase
{
    public ProgramInterfaceConstructFunctionExistenceCheckTest1()
    {
        super(ProgramInterfaceConstructFunctionExistenceCheck.class);
    }

    /**
     * Test no error 
     * return struct function.
     *
     * @throws Exception the exception
     */
    @Test
    public void testReturnStruct() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "program-interface-function-return-struct.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test no error 
     * return struct function.
     *
     * @throws Exception the exception
     */
    @Test
    public void testReturnValueTree() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "program-interface-function-return-value-tree.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test no error 
     * return struct function.
     *
     * @throws Exception the exception
     */
    @Test
    public void testReturnValueTable() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "program-interface-function-return-value-table.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test no error 
     * return struct function.
     *
     * @throws Exception the exception
     */
    @Test
    public void testReturnStructNested() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "program-interface-function-return-struct-nested.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test error 
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoConstruct() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "program-interface-no-construct-function.bsl");

        List<Marker> markers = getModuleMarkers();

        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(1), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
