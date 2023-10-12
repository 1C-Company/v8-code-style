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
package com.e1c.v8codestyle.autosort.itests;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IDtProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.testing.GuiceModules;
import com._1c.g5.v8.dt.testing.JUnitGuiceRunner;
import com._1c.g5.v8.dt.testing.TestingWorkspace;
import com.e1c.g5.v8.dt.cli.api.CliCommandException;
import com.e1c.g5.v8.dt.cli.api.ICliCommandExecutor;
import com.e1c.v8codestyle.internal.autosort.cli.SortCommand;
import com.google.inject.Inject;

/**
 * Tests for {@link SortCommand}.
 */
@RunWith(JUnitGuiceRunner.class)
@GuiceModules(modules = { ExternalDependenciesModule.class })
public class SortCommandTest
{

    private static final String PROJECT_NAME = "Sort";

    @Rule
    public TestingWorkspace testingWorkspace = new TestingWorkspace(true, true);

    @Inject
    private IDtProjectManager dtProjectManager;

    @Inject
    private IBmModelManager bmModelManager;

    @Inject
    private ICliCommandExecutor commandExecutor;

    @Test
    public void testSortExisting() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        commandExecutor.execute("sort-project --project-name-list [" + PROJECT_NAME + "]");

        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        assertTrue(object instanceof Configuration);

        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getCommonModules().isEmpty());

        assertEquals("АМ2_4Модуль", configuration.getCommonModules().get(0).getName());
        assertEquals("АМ_Модуль", configuration.getCommonModules().get(1).getName());
        assertEquals("АМодуль", configuration.getCommonModules().get(2).getName());
        assertEquals("БМодуль", configuration.getCommonModules().get(3).getName());
        assertEquals("ГМодуль", configuration.getCommonModules().get(4).getName());
        assertEquals("ОбщийМодуль", configuration.getCommonModules().get(5).getName());
    }

    @Test
    public void testSortNonExisting() throws Exception
    {
        try
        {
            commandExecutor.execute("sort-project --project-name-list [OtherProject]");
            fail("Successfully sorted a non-existent project");
        }
        catch (CliCommandException e)
        {
            assertTrue(e.getCause().getMessage().contains("OtherProject"));
        }
    }

    @Test
    public void testImportAndSort() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        commandExecutor.execute("sort-project --project-list [\"" + project.getLocation().toFile().toPath() + "\"]");

        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        assertTrue(object instanceof Configuration);

        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getCommonModules().isEmpty());

        assertEquals("АМ2_4Модуль", configuration.getCommonModules().get(0).getName());
        assertEquals("АМ_Модуль", configuration.getCommonModules().get(1).getName());
        assertEquals("АМодуль", configuration.getCommonModules().get(2).getName());
        assertEquals("БМодуль", configuration.getCommonModules().get(3).getName());
        assertEquals("ГМодуль", configuration.getCommonModules().get(4).getName());
        assertEquals("ОбщийМодуль", configuration.getCommonModules().get(5).getName());
    }

    private IBmObject getTopObjectByFqn(final String fqn, IDtProject dtProject)
    {
        IBmModel model = this.bmModelManager.getModel(dtProject);
        return model.executeReadonlyTask(new AbstractBmTask<IBmObject>("GetObject")
        {
            @Override
            public IBmObject execute(IBmTransaction transaction, IProgressMonitor progressMonitor)
            {
                return transaction.getTopObjectByFqn(fqn);
            }
        });
    }
}
