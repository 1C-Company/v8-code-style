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
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.NotifyDescriptionToServerProcedureCheck;

/**
 * The test of {@link NotifyDescriptionToServerProcedureCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class NotifyDescriptionToServerProcedureCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "CommonForm";

    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/Form/Module.bsl";

    public NotifyDescriptionToServerProcedureCheckTest()
    {
        super(NotifyDescriptionToServerProcedureCheck.class);
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

    @Override
    protected Module getModule()
    {
        // do not need this method
        return null;
    }

    /**
     * Test notify description to local server procedure in form module.
     *
     * @throws Exception the exception
     */
    @Test
    public void testLocalServerProcedure() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "notify-description-to-server-procedure.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());
        List<Integer> errorLines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE))
            .map(Integer.class::cast)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(5, 7), errorLines);
    }

}
