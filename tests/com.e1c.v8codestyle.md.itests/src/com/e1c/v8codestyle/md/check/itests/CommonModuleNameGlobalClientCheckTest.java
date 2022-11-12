/**
 * 
 */
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import com.e1c.v8codestyle.md.CommonModuleTypes;
import com.e1c.v8codestyle.md.check.CommonModuleNameGlobalClientCheck;

/**
 * Tests for {@link CommonModuleNameGlobalClientCheck} check.
 * 
 * @author Artem Iliukhin
 *
 */
public class CommonModuleNameGlobalClientCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "common-module-name-global-client";

    private static final String PROJECT_NAME = "CommonModuleName";

    private static final String MODULE_DEFAULT_FQN = "CommonModule.CommonModuleName";

    @Test
    public void testCommonModuleNameClientGlobal() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleGlobalClient";
        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleTypes.CLIENT_GLOBAL, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);

        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNameClientGlobalCompliant() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleGlobal";
        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleTypes.CLIENT_GLOBAL, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);

        assertNull(marker);
    }

    private void updateCommonModule(IDtProject dtProject, String fqn, CommonModuleTypes type, String newFqn)
    {
        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change type")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject object = transaction.getTopObjectByFqn(fqn);

                for (Entry<EStructuralFeature, Object> entry : type.getFeatureValues(false).entrySet())
                {
                    object.eSet(entry.getKey(), entry.getValue());
                }

                if (!(object instanceof CommonModule))
                {
                    return null;
                }

                CommonModule module = (CommonModule)object;

                if (newFqn != null)
                {
                    String[] fqnArray = newFqn.split("[.]");
                    if (fqnArray.length == 2)
                    {
                        module.setName(fqnArray[1]);
                        transaction.updateTopObjectFqn(object, newFqn);
                    }
                }

                return null;
            }
        });
        waitForDD(dtProject);
    }
}
