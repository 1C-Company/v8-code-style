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
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.CommonModuleTypes;
import com.e1c.v8codestyle.md.check.CommonModuleNameServerCallCheck;

/**
 * Tests for {@link CommonModuleNameServerCallCheck} check.
 *
 * @author Artem Iliukhin
 */
public class CommonModuleNameServerCallCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "common-module-name-server-call";

    private static final String PROJECT_NAME = "CommonModuleName";

    private static final String MODULE_DEFAULT_FQN = "CommonModule.CommonModuleName";

    @Test
    public void testCommonModuleNameServerCallWithoutPostfixCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleServerNoCall";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleTypes.SERVER_CALL, ReturnValuesReuse.DONT_USE,
            fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNameServerCallWithPostfixCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleServerCall";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleTypes.SERVER_CALL, ReturnValuesReuse.DONT_USE,
            fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertEquals(marker, null);
    }

    private void updateCommonModule(IDtProject dtProject, String fqn, CommonModuleTypes type,
        ReturnValuesReuse returnValueReuse, String newFqn)
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

                module.setReturnValuesReuse(returnValueReuse);

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
