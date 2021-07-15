/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *     Aleksandr Kapralov - issue #14
 *******************************************************************************/
package com.e1c.v8codestyle.md.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
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
import com.e1c.v8codestyle.md.check.CommonModuleNameGlobal;
import com.e1c.v8codestyle.md.check.CommonModuleType;

/**
 * Tests for {@link CommonModuleNameGlobal} check.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class CommonModuleNameGlobalTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "common-module-name-global"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "CommonModuleName";

    private static final String MODULE_DEFAULT_FQN = "CommonModule.CommonModuleName";

    @Test
    public void testCommonModuleNameClientGlobal() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_GLOBAL, null);

        long id = getTopObjectIdByFqn(MODULE_DEFAULT_FQN, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNameClientGlobalCorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleClientGlobal";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_GLOBAL, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    @Test
    public void testCommonModuleNameClientGlobalWithPostfixCorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleClientGlobalPredefined";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_GLOBAL, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    @Test
    public void testCommonModuleNameClientGlobalWithPrefixIncorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.GlobalCommonModule";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_GLOBAL, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNameServerGlobal() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_SERVER_GLOBAL, null);

        long id = getTopObjectIdByFqn(MODULE_DEFAULT_FQN, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNameServerGlobalCorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleServerGlobal";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_SERVER_GLOBAL, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    @Test
    public void testCommonModuleNameServerGlobalWithPostfixCorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleServerGlobalPredefined";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_SERVER_GLOBAL, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    @Test
    public void testCommonModuleNameServerGlobalWithPrefixIncorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.GlobalCommonModule";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_SERVER_GLOBAL, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
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
                    object.eSet(entry.getKey(), entry.getValue());

                if (newName != null && object instanceof CommonModule)
                {
                    CommonModule module = (CommonModule)object;
                    module.setName(newName.split("[.]")[1]);
                    transaction.updateTopObjectFqn(object, newName);
                }
                return null;
            }
        });
        waitForDD(dtProject);
    }

}
