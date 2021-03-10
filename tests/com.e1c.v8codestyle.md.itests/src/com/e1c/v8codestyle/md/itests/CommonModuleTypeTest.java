/**
 *
 */
package com.e1c.v8codestyle.md.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.testing.check.CheckTestBase;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.md.check.CommonModuleType;

/**
 * Tests for {@link CommonModuleType} check.
 *
 * @author Dmitriy Marmyshev
 *
 */
@SuppressWarnings("nls")
public class CommonModuleTypeTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "CommonModuleType";

    /**
     * Test common module type - server.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServer() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.Common", dtProject);
        Marker marker = getFirstMarker(CommonModuleType.CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test common module type - server is correct.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServerCorrect() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        String fqn = "CommonModule.Common";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_SERVER);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CommonModuleType.CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test common module type - server call.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServerCall() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.CommonServerCall", dtProject);
        Marker marker = getFirstMarker(CommonModuleType.CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test common module type - server call is correct.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServerCallCorrect() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonServerCall";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_SERVER_CALL);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CommonModuleType.CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test common module type - client.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeClient() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.CommonClient", dtProject);
        Marker marker = getFirstMarker(CommonModuleType.CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test common module type - client is correct.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeClientCorrect() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonClient";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_CLIENT);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CommonModuleType.CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test common module type - server client.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServerClient() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.CommonServerClient", dtProject);
        Marker marker = getFirstMarker(CommonModuleType.CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test common module type - server client is correct.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServerClientCorrect() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonServerClient";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_CLIENT_SERVER);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CommonModuleType.CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    private void updateCommonModule(IDtProject dtProject, String fqn, Map<EStructuralFeature, Boolean> types)
    {
        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change type")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject object = transaction.getTopObjectByFqn(fqn);
                for (Entry<EStructuralFeature, Boolean> entry : types.entrySet())
                {
                    object.eSet(entry.getKey(), entry.getValue());
                }
                return null;
            }
        });
        waitForDD(dtProject);
    }
}
