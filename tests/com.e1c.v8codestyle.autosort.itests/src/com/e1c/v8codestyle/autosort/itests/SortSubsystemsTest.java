/*******************************************************************************
 * Copyright (C) 2024, 1C-Soft LLC and others.
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
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.ClassRule;
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
import com._1c.g5.v8.dt.platform.version.Version;
import com._1c.g5.v8.dt.testing.GuiceModules;
import com._1c.g5.v8.dt.testing.JUnitGuiceRunner;
import com._1c.g5.v8.dt.testing.TestingPlatformSupport;
import com._1c.g5.v8.dt.testing.TestingWorkspace;
import com.e1c.v8codestyle.autosort.ISortService;
import com.google.inject.Inject;

/**
 * Test ensuring subsystems are sorted properly when top metadata sorting enabled.
 *
 * @author Tihon Tihonin
 */
@RunWith(JUnitGuiceRunner.class)
@GuiceModules(modules = { ExternalDependenciesModule.class })
public class SortSubsystemsTest
{
    private static final String PROJECT_NAME = "SubsystemsAutoSort";

    @Rule
    public TestingWorkspace testingWorkspace = new TestingWorkspace(true, true);

    @ClassRule
    public static final TestingPlatformSupport testingPlatformSupport = new TestingPlatformSupport(Version.V8_3_19);

    @Inject
    private ISortService sortService;

    @Inject
    public IDtProjectManager dtProjectManager;

    @Inject
    public IBmModelManager bmModelManager;

    @Test
    public void testSortSubsystems() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        Configuration configuration = (Configuration)object;

        assertEquals("Subsystem2", configuration.getSubsystems().get(0).getSubsystems().get(0).getName());
        assertEquals("Subsystem1", configuration.getSubsystems().get(0).getSubsystems().get(1).getName());

        sortService.sortAllMetadata(dtProject, new NullProgressMonitor());

        assertEquals("Subsystem1", configuration.getSubsystems().get(0).getSubsystems().get(0).getName());
        assertEquals("Subsystem2", configuration.getSubsystems().get(0).getSubsystems().get(1).getName());
    }

    protected IBmObject getTopObjectByFqn(final String fqn, IDtProject dtProject)
    {
        IBmModel model = bmModelManager.getModel(dtProject);
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
