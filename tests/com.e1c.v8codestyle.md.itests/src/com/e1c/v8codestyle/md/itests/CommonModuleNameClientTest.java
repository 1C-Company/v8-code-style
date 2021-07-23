/**
 *
 */
package com.e1c.v8codestyle.md.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.CommonModuleNameClient;
import com.e1c.v8codestyle.md.check.CommonModuleType;

/**
 * Tests for {@link CommonModuleNameClient} check.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class CommonModuleNameClientTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "common-module-name-client";

    private static final String PROJECT_NAME = "CommonModuleName";

    @Test
    public void testCommonModuleNameClient() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleName";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_CLIENT, null);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNameClientCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleName";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_CLIENT, "CommonModuleClient");

        fqn = "CommonModule.CommonModuleClient";
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    private void updateCommonModule(IDtProject dtProject, String fqn, Map<EStructuralFeature, Boolean> types,
        String newName)
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
                if (newName != null && object instanceof CommonModule)
                {
                    CommonModule module = (CommonModule)object;
                    module.setName(newName);
                    transaction.updateTopObjectFqn(object, module.eClass().getName() + "." + newName);
                }
                return null;
            }
        });
        waitForDD(dtProject);
    }

}
