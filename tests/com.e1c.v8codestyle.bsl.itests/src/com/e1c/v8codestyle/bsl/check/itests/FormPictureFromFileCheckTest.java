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
import java.util.stream.Collectors;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.FormPictureFromFileCheck;

/**
 * Tests for {@link FormPictureFromFileCheck} check.
 *
 * @author Ivan Sergeev
 */
public class FormPictureFromFileCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "CommonForm"; //$NON-NLS-1$

    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/Form/Module.bsl"; //$NON-NLS-1$

    public FormPictureFromFileCheckTest()
    {
        super(FormPictureFromFileCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return COMMON_FORM_FILE_NAME;
    }

    /**
     * Picture created from a file name, from binary data and from a stream and assigned
     * to the picture of a form command.
     *
     * @throws Exception the exception
     */
    @Test
    public void testPictureFromFile() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-picture-from-file.bsl"); //$NON-NLS-1$

        List<Marker> markers = getModuleMarkers();
        assertEquals(3, markers.size());
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(3, 4, 5), errorLines);
    }

    /**
     * Empty picture, picture assigned to a variable and non picture constructors are not reported.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoPictureFromFile() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "form-picture-from-file-negative.bsl"); //$NON-NLS-1$

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }
}
