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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.metadata.mdclass.AbstractForm;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.UsingFormDataToValueCheck;

/**
 * The test for {@link UsingFormDataToValueCheck} check.
 *
 * @author Artem Iliukhin
 */
public class UsingFormDataToValueCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "CommonForm";

    private static final String FQN = "CommonForm.Form.Form";

    private static final String COMMON_FORM_FILE_NAME = "/src/CommonForms/Form/Module.bsl";

    /**
     * Instantiates a new form data to value check test.
     */
    public UsingFormDataToValueCheckTest()
    {
        super(UsingFormDataToValueCheck.class);
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
        IBmObject mdObject = getTopObjectByFqn(FQN, getProject());
        assertTrue(mdObject instanceof AbstractForm);
        Module module = ((AbstractForm)mdObject).getModule();
        assertNotNull(module);

        return module;
    }

    @Test
    public void testFormModuleHasPragma() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "using-form-data-to-value.bsl");

        Marker marker = getModuleFirstMarker();
        assertEquals(Integer.valueOf(2), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
