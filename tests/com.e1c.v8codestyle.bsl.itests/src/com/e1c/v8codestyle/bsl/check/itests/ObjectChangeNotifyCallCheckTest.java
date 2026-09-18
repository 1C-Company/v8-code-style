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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.ObjectChangeNotifyCallCheck;

/**
 * Test for {@link ObjectChangeNotifyCallCheck}
 * 
 * @author Artem Samohvalov
 */
public class ObjectChangeNotifyCallCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "ObjectChangeNotifyCallCheckTest";
    private static final String MODULE_FILE_NAME = "/src/Catalogs/TestCatalog/Forms/ItemForm/Module.bsl";

    public ObjectChangeNotifyCallCheckTest()
    {
        super(ObjectChangeNotifyCallCheck.class);
    }

    /**
     * test error
     * 
     * @throws Exception
     */
    @Test
    public void testNoNotifiCall() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "object-change-no-notify-call.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());
    }

    /**
     * test no error
     * 
     * @throws Exception
     */
    @Test
    public void testNotifyCall() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "object-change-notify-call.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * test no error
     * 
     * @throws Exception
     */
    @Test
    public void testNotifiCallNested() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "object-change-notifi-call-nested.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return MODULE_FILE_NAME;
    }

    @Override
    protected String getModuleId()
    {
        return Path.ROOT.append(getTestConfigurationName()).append(getModuleFileName()).toString();
    }
}
