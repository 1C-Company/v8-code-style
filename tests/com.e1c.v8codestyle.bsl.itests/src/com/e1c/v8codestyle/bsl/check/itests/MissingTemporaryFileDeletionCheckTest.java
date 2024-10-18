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
 *     Denis Maslennikov - issue #409
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckParameterSettings;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.v8codestyle.bsl.check.MissingTemporaryFileDeletionCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Test for class {@link MissingTemporaryFileDeletionCheck}.
 *
 * @author Denis Maslennikov
 */
public class MissingTemporaryFileDeletionCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String FOLDER = FOLDER_RESOURCE + "temporary-file-deletion/";
    private static final String DELETE_FILE_METHODS_PARAM = "deleteFileMethods"; //$NON-NLS-1$
    private static final String TEST_CUSTOM_DELETE_FILE_METHODS_PARAM_LEVEL0 = "МоеУдалениеФайла,MyFileDeletion"; //$NON-NLS-1$
    private static final String TEST_CUSTOM_DELETE_FILE_METHODS_PARAM_LEVEL1 =
        "МойОбщийМодуль.МоеУдалениеФайла,MyCommonModule.MyFileDeletion"; //$NON-NLS-1$
    private static final String TEST_CUSTOM_DELETE_FILE_METHODS_PARAM_LEVEL2 =
        "Справочники.Товары.МоеУдалениеФайла,Catalog.Goods.MyFileDeletion"; //$NON-NLS-1$

    public MissingTemporaryFileDeletionCheckTest()
    {
        super(MissingTemporaryFileDeletionCheck.class);
    }

    /**
     * Test missing temporary file deletion.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNonCompliant() throws Exception
    {
        updateModule(FOLDER + "non-compliant.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(3, markers.size());

        Set<Integer> lines = new LinkedHashSet<>();
        for (Marker marker : markers)
        {
            assertEquals(Messages.MissingTemporaryFileDeletionCheck_Missing_Temporary_File_Deletion,
                marker.getMessage());
            lines.add(marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        }
        assertEquals(Set.of(3, 12, 22), lines);
    }

    /**
     * Test correct temporary file deletion by DeleteFiles method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteFiles() throws Exception
    {
        updateModule(FOLDER + "delete-files.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test correct temporary file deletion by BeginDeletingFiles method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBeginDeletingFiles() throws Exception
    {
        updateModule(FOLDER + "begin-deleting-files.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test correct temporary file deletion by MoveFile method.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMoveFile() throws Exception
    {
        updateModule(FOLDER + "move-file.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test correct temporary file deletion by custom method
     * from the list in the plugin parameters.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCustomDeletionMethodLevel0() throws Exception
    {
        setPluginParameters(DELETE_FILE_METHODS_PARAM, TEST_CUSTOM_DELETE_FILE_METHODS_PARAM_LEVEL0);
        updateModule(FOLDER + "custom-deletion-method-level-0.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test correct temporary file deletion by custom method with the first level of nesting
     * from the list in the plugin parameters.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCustomDeletionMethodLevel1() throws Exception
    {
        setPluginParameters(DELETE_FILE_METHODS_PARAM, TEST_CUSTOM_DELETE_FILE_METHODS_PARAM_LEVEL1);
        updateModule(FOLDER + "custom-deletion-method-level-1.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test correct temporary file deletion by custom method with with the second level of nesting
     * from the list in the plugin parameters.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCustomDeletionMethodLevel2() throws Exception
    {
        setPluginParameters(DELETE_FILE_METHODS_PARAM, TEST_CUSTOM_DELETE_FILE_METHODS_PARAM_LEVEL2);
        updateModule(FOLDER + "custom-deletion-method-level-2.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * The method changes default parameter of plugin.
     *
     * @throws Exception the exception
     */
    private void setPluginParameters(String name, String value) throws Exception
    {
        IProject project = getProject().getWorkspaceProject();
        CheckUid cuid = new CheckUid(getCheckId(), BslPlugin.PLUGIN_ID);
        ICheckSettings settings = checkRepository.getSettings(cuid, project);
        ICheckParameterSettings parameter = settings.getParameters().get(name);
        parameter.setValue(value);
        checkRepository.applyChanges(Collections.singleton(settings), project);
    }
}
