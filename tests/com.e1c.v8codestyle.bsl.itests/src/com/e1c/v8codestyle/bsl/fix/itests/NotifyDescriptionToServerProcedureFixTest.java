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
package com.e1c.v8codestyle.bsl.fix.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.metadata.mdclass.AbstractForm;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.NotifyDescriptionToServerProcedureCheck;

/**
 *  Tests for NotifyDescriptionToServerProcedureFix fix.
 *
 *  @author Artem Samohvalov
 */
public class NotifyDescriptionToServerProcedureFixTest
    extends AbstractQuickFixTest
{
    public NotifyDescriptionToServerProcedureFixTest()
    {
        super(NotifyDescriptionToServerProcedureCheck.class);
    }

    @Test
    public void testApplyFix() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "fix-notify-description-to-server-procedure.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        performFix(marker, Messages.NotifyDescriptionToServerProcedureFix_Description);
        PlatformUI.getWorkbench().saveAllEditors(false);

        assertMarkerGone();
        assertHasMethod();
    }

    @Override
    protected String getModuleFileName()
    {
        return "/src/CommonForms/Form/Module.bsl";
    }

    @Override
    protected String getTestConfigurationName()
    {
        return "CommonForm";
    }

    @Override
    protected Module getModule()
    {
        IBmObject mdObject = getTopObjectByFqn("CommonForm.Form.Form", getProject());
        assertTrue(mdObject instanceof AbstractForm);
        Module module = ((AbstractForm)mdObject).getModule();
        assertNotNull(module);

        return module;
    }

    private void assertHasMethod()
    {
        Module module = getModule();
        // find created method in module
        assertTrue(module.allMethods().stream().anyMatch(m -> "Aaaaa".equals(m.getName())));
    }
}
