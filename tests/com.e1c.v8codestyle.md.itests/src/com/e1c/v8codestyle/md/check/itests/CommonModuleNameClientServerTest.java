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
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.internal.md.itests.CheckMd;
import com.e1c.v8codestyle.md.check.CommonModuleNameClientServer;
import com.e1c.v8codestyle.md.check.CommonModuleType;

/**
 * Tests for {@link CommonModuleNameClientServer} check.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class CommonModuleNameClientServerTest
    extends CheckMd
{

    private static final String CHECK_ID = "common-module-name-client-server";

    private static final String PROJECT_NAME = "CommonModuleName";

    private static final String MODULE_DEFAULT_FQN = "CommonModule.CommonModuleName";

    @Test
    public void testCommonModuleNameClientServer() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_SERVER,
            ReturnValuesReuse.DONT_USE, null);

        long id = getTopObjectIdByFqn(MODULE_DEFAULT_FQN, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNameClientServerCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleClientServer";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_SERVER,
            ReturnValuesReuse.DONT_USE, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    @Test
    public void testCommonModuleNameClientServerWithPostfixCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.CommonModuleClientServerPredefined";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_SERVER,
            ReturnValuesReuse.DONT_USE, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

    @Test
    public void testCommonModuleNameClientServerWithPrefixIncorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String fqn = "CommonModule.ClientServerCommonModule";

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_SERVER,
            ReturnValuesReuse.DONT_USE, fqn);

        long id = getTopObjectIdByFqn(fqn, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    @Test
    public void testCommonModuleNameClientServerReturnValueReuseCorrect() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        updateCommonModule(dtProject, MODULE_DEFAULT_FQN, CommonModuleType.TYPE_CLIENT_SERVER,
            ReturnValuesReuse.DURING_SESSION, null);

        long id = getTopObjectIdByFqn(MODULE_DEFAULT_FQN, dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
    }

}
