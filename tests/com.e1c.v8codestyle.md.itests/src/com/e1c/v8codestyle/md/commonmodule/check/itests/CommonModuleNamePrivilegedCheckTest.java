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
package com.e1c.v8codestyle.md.commonmodule.check.itests;

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
import com.e1c.v8codestyle.md.commonmodule.check.CommonModuleNamePrivilegedCheck;

/**
 * Tests for {@link CommonModuleNamePrivilegedCheck} check.
 *
 * @author Artem Iliukhin
 */
public class CommonModuleNamePrivilegedCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "common-module-name-full-access";

    private static final String PROJECT_NAME = "CommonModuleName";

    private static final String MODULE_DEFAULT_FQN = "CommonModule.CommonModuleName";

    @Test
    public void testCommonModuleNamePrivileged() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleTypes.SERVER, true, null);

        long id = getTopObjectIdByFqn(MODULE_DEFAULT_FQN, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNamePrivilegedCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleFullAccess";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleTypes.SERVER, true, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    private void updateCommonModule(IDtProject dtProject, String fqn, CommonModuleTypes type, boolean isPrivileged,
        String newFqn)
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

                module.setPrivileged(isPrivileged);

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
