/**
 *
 */
package com.e1c.v8codestyle.md.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.DefaultDataLockControlMode;
import com._1c.g5.v8.dt.testing.check.CheckTestBase;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.md.check.ConfigurationDataLock;

/**
 * Tests for {@link ConfigurationDataLock} check.
 *
 * @author Dmitriy Marmyshev
 */
@SuppressWarnings("nls")
public class ConfigurationDataLockTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "DataLock";

    /**
     * Test configuration data lock is not equals managed.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDataLock() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Configuration", dtProject);
        Marker marker = getFirstMarker(ConfigurationDataLock.CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testManagedDataLockMode() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change mode")
        {

            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                Configuration configuration = (Configuration)transaction.getTopObjectByFqn("Configuration");
                configuration.setDataLockControlMode(DefaultDataLockControlMode.MANAGED);
                return null;
            }
        });
        waitForDD(dtProject);

        long id = getTopObjectIdByFqn("Configuration", dtProject);
        Marker marker = getFirstMarker(ConfigurationDataLock.CHECK_ID, id, dtProject);
        assertNull(marker);
    }
}
