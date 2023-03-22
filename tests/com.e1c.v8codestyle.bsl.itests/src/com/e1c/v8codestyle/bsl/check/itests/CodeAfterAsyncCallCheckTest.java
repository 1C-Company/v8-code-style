/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.v8codestyle.bsl.check.CodeAfterAsyncCallCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link CodeAfterAsyncCallCheck} check.
 *
 * @author Artem Iliukhin
 */
public class CodeAfterAsyncCallCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String CHECK_ID = "code-after-async-call"; //$NON-NLS-1$
    private static final String PARAMETER_NAME = "notifyDescriptionIsDefined"; //$NON-NLS-1$

    /**
     * Instantiates a new code after async call check test.
     */
    public CodeAfterAsyncCallCheckTest()
    {
        super(CodeAfterAsyncCallCheck.class);
    }

    @Test
    public void testCodeAfterExistence() throws Exception
    {
        IDtProject dtProject = getProject();
        IProject project = dtProject.getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(new CheckUid(CHECK_ID, BslPlugin.PLUGIN_ID), project);
        settings.getParameters().get(PARAMETER_NAME).setValue(Boolean.toString(Boolean.FALSE));
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(dtProject);

        updateModule(FOLDER_RESOURCE + "code-after-async-call-existence.bsl");

        Marker marker = getFirstMarker(CHECK_ID, getModuleId(), getProject());
        assertNotNull(marker);
        assertEquals("7", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    @Test
    public void testCallBackDescriptionCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "async-call-back-descr.bsl");

        Marker marker = getFirstMarker(CHECK_ID, getModuleId(), getProject());
        assertNull(marker);
    }

    @Test
    public void testPromiseCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "async-call-promise.bsl");

        Marker marker = getFirstMarker(CHECK_ID, getModuleId(), getProject());
        assertNull(marker);
    }

    @Test
    public void testSpreadsheetDocumentNonCompliant() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "async-call-spread-sh-doc.bsl");

        Marker marker = getFirstMarker(CHECK_ID, getModuleId(), getProject());
        assertNotNull(marker);
        assertEquals("8", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
