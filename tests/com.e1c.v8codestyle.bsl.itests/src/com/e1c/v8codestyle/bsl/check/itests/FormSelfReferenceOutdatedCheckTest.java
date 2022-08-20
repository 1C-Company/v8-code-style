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

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.FormSelfReferenceOutdatedCheck;

/**
 * The test for class {@link FormSelfReferenceOutdatedCheck}
 * @author Maxim Galios
 *
 */
public class FormSelfReferenceOutdatedCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "FormSelfReferenceOutdatedCheck";

    private static final String FORM_MODULE_FILE_NAME = "/src/CommonForms/MyForm/Module.bsl";

    public FormSelfReferenceOutdatedCheckTest()
    {
        super(FormSelfReferenceOutdatedCheck.class);
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleId()
    {
        return Path.ROOT.append(getTestConfigurationName()).append(FORM_MODULE_FILE_NAME).toString();
    }

    /**
     * Test ЭтаФорма/ThisForm references presence in form module
     *
     * @throws Exception
     */
    @Test
    public void testFormModule() throws Exception
    {
        List<Marker> markers = getModuleMarkers();
        assertEquals(3, markers.size());

        assertEquals("11", markers.get(0).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("12", markers.get(1).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals("13", markers.get(2).getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
    }
}
