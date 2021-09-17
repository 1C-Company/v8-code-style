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
 *******************************************************************************/
package com.e1c.v8codestyle.md.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.DefaultDataLockControlMode;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.ConfigurationDataLock;

/**
 * Tests for {@link ConfigurationDataLock} check.
 *
 * @author Dmitriy Marmyshev
 */
public class ConfigurationDataLockTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "configuration-data-lock-mode";

    private static final String PROJECT_NAME = "DataLock";

    /**
     * Test configuration data lock is not equals managed.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDataLock() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Configuration", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testManagedDataLockMode() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
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
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }
}
