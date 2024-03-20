/**
 *
 */
package com.e1c.v8codestyle.autosort.itests;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import com.e1c.v8codestyle.autosort.ISortService;
import com.google.inject.Inject;

/**
 * @author Tihon Tihonin
 *
 */
@RunWith(JUnitGuiceRunner.class)
@GuiceModules(modules = { ExternalDependenciesModule.class })
public class SortSubsystemsTest
{
    private static final String PROJECT_NAME = "SubsystemsAutoSort";

    @Rule
    public TestingWorkspace testingWorkspace = new TestingWorkspace(true, true);

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

        sortService.startSortAllMetadata(dtProject.getWorkspaceProject());
        Thread.sleep(2000);
        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        assertTrue(object instanceof Configuration);

        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getSubsystems().isEmpty());

        assertEquals("Subsystem1", configuration.getSubsystems().get(0).getSubsystems().get(0).getName());
        assertEquals("Subsystem2", configuration.getSubsystems().get(0).getSubsystems().get(1).getName());
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
}
