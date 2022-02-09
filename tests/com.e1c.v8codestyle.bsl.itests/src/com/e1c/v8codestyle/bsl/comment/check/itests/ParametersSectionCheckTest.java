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
package com.e1c.v8codestyle.bsl.comment.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckParameterSettings;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.ParametersSectionCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link ParametersSectionCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class ParametersSectionCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PARAMETER_CHECK_ONLY_EXPORT = "checkOnlyExport"; //$NON-NLS-1$

    public ParametersSectionCheckTest()
    {
        super(ParametersSectionCheck.class);
    }

    /**
     * Test the documentation comment parameter section should contains all parameters of only export method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOnlyExportMethodParametersShouldCheck() throws Exception
    {
        IProject project = getProject().getWorkspaceProject();
        CheckUid cuid = new CheckUid(getCheckId(), BslPlugin.PLUGIN_ID);
        ICheckSettings settings = checkRepository.getSettings(cuid, project);
        ICheckParameterSettings parameter = settings.getParameters().get(PARAMETER_CHECK_ONLY_EXPORT);
        parameter.setValue(Boolean.TRUE.toString());
        checkRepository.applyChanges(Collections.singleton(settings), project);

        updateModule(FOLDER_RESOURCE + "doc-comment-parameter-section.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        Marker marker = markers.get(0);
        assertEquals("4", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        marker = markers.get(1);
        assertEquals("10", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }

    /**
     * Test the documentation comment parameter section should contains all parameters of all methods.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAllMethodParametersShouldCheck() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-parameter-section.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(3, markers.size());
        Marker marker = markers.get(0);
        assertEquals("4", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        marker = markers.get(1);
        assertEquals("10", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        marker = markers.get(2);
        assertEquals("22", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
