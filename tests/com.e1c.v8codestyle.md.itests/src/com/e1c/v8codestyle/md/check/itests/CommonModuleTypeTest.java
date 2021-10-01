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
 *     Aleksandr Kapralov - issue #15
 *******************************************************************************/
package com.e1c.v8codestyle.md.check.itests;

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
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.CommonModuleType;

/**
 * Tests for {@link CommonModuleType} check.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class CommonModuleTypeTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "common-module-type";

    private static final String PROJECT_NAME = "CommonModuleType";

    /**
     * Test common module type - server.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServer() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.Common", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
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
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.Common";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_SERVER);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
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
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.CommonServerCall", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
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
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonServerCall";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_SERVER_CALL);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
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
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.CommonClient", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
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
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonClient";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_CLIENT);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
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
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.CommonServerClient", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
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
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonServerClient";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_CLIENT_SERVER);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test common module type - server global.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServerGlobal() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.CommonServerGlobal", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test common module type - server global is correct.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeServerGlobalCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonServerGlobal";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_SERVER_GLOBAL);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    /**
     * Test common module type - client global.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeClientGlobal() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("CommonModule.CommonClientGlobal", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test common module type - client global is correct.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeClientGlobalCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonClientGlobal";

        updateCommonModule(dtProject, fqn, CommonModuleType.TYPE_CLIENT_GLOBAL);
        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
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
