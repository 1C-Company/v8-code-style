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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.NotifyCallAfterObjectChangeCheck;

/**
 * The test for {@link NotifyCallAfterObjectChangeCheck}
 * 
 * @author Artem Samohvalov
 */
public class NotifyCallAfterObjectChangeCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "ObjectChangeNotifyCallCheckTest";
    private static final String MODULE_FILE_NAME = "/src/Catalogs/TestCatalog/Forms/ItemForm/Module.bsl";

    public NotifyCallAfterObjectChangeCheckTest()
    {
        super(NotifyCallAfterObjectChangeCheck.class);
    }

    /**
     * Test error
     * Test no notify call and object write.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoNotifyCallAndObjectWrite() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-notify-call-object-write.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());

        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test error
     * Test no notify call and object delete.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoNotifyCallAndObjectDelete() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-notify-call-object-delete.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());

        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test error.
     * Test notify call that is not the last statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotifyCallIsNotAtTheEnd() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "notify-call-not-at-the-end.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());

        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test no error
     * Test notify call and object write.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotifyCallAndObjectWrite() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "notify-call-object-write.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test no error
     * Test notify call and object delete.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotifyCallAndObjectDelete() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "notify-call-object-delete.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test no error.
     * Test server method without object write or delete.
     *
     * @throws Exception the exception
     */
    @Test
    public void testServerMethodDoesNotChangeObject() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "server-method-without-object-change.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test no error.
     * Test notify call with set deletion mark
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotifyCallWithSetDeletionMark() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "notify-call-object-seted-deletion-mark.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test no error.
     * Test method without server execution directive.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMethodWithoutServerDirective() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-notify-call-at-client-procedure.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    /**
     * Test error.
     * Test client and server method with object write.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClientAtServerMethodAndObjectWrite() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "client-at-server-object-write.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());

        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test error.
     * Test several server method calls when one of them changes the object.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSeveralServerMethodsWithObjectChange() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "several-server-methods-object-change.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());

        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test error.
     * Test no notify call with set deletion mark
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoNotifyCallWithSetDeletionMark() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "no-notify-call-object-seted-deletion-mark.bsl");

        List<Marker> markers = getModuleMarkers();
        assertFalse(markers.isEmpty());

        Marker marker = markers.get(0);
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
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
