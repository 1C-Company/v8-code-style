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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
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
import com.e1c.v8codestyle.internal.autosort.cli.SortCommand;
import com.google.inject.Inject;

/**
 * Tests for {@link SortCommand}.
 */
@RunWith(JUnitGuiceRunner.class)
@GuiceModules(modules = { ExternalDependenciesModule.class })
public class SortCommandTest
{
    private static final String CLASS_NAME =
        "com.e1c.v8codestyle.internal.autosort.ExecutableExtensionFactory:com.e1c.v8codestyle.internal.autosort.cli.SortCommand";

    private static final String PROJECT_NAME = "Sort";

    @Rule
    public TestingWorkspace testingWorkspace = new TestingWorkspace(true, true);

    @Inject
    private IDtProjectManager dtProjectManager;

    @Inject
    private IBmModelManager bmModelManager;

    private SortCommand command;

    @Before
    public void setUp() throws Exception
    {
        command = createSortCommand();
    }

    @Test
    public void testSortExisting() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        IStatus status = command.sortExistingProjects(new String[] { PROJECT_NAME });

        assertTrue(status.isOK());
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
        IStatus status = command.sortExistingProjects(new String[] { "OtherProject" });

        assertFalse(status.isOK());
    }

    @Test
    public void testImportAndSort() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        IStatus status = command.importAndSortProjects(new String[] { project.getLocation().toString() });

        assertTrue(status.isOK());
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


    protected IBmObject getTopObjectByFqn(final String fqn, IDtProject dtProject)
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

    private SortCommand createSortCommand() throws Exception
    {
        IConfigurationElement[] elements = Platform.getExtensionRegistry()
            .getExtensionPoint("com.e1c.g5.v8.dt.cli.api", "cliCommand")
            .getConfigurationElements();
        for (int i = 0; i < elements.length; i++)
        {
            IConfigurationElement element = elements[i];
            if (!CLASS_NAME.equals(element.getAttribute("class")))
            {
                continue;
            }

            return (SortCommand)element.createExecutableExtension("class");
        }
        return null;
    }

}
