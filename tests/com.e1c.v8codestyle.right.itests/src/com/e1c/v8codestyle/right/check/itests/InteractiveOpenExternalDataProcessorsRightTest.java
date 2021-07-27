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
 *     Aleksandr Kapralov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.right.check.itests;

import org.junit.Test;

import com._1c.g5.v8.dt.rights.model.util.RightName;
import com.e1c.v8codestyle.internal.right.itests.CheckRights;
import com.e1c.v8codestyle.right.check.InteractiveOpenExternalDataProcessorsRight;

/**
 * Tests for {@link InteractiveOpenExternalDataProcessorsRight} check.
 *
 * @author Aleksandr Kapralov
 */
public class InteractiveOpenExternalDataProcessorsRightTest
    extends CheckRights
{

    private static final String PROJECT_NAME = "StandartRoles";

    private static final String ROLE_FQN = "Role.StandartRole.Rights";
    private static final String CONFIGURATION_FQN = "Configuration";

    private static final String CHECK_ID = "interactive-open-external-data-processors-right"; //$NON-NLS-1$

    private static final String STANDART_ROLE = RightName.INTERACTIVE_OPEN_EXT_DATA_PROCESSORS.getName();

    @Test
    public void testStandartRoleCorrect() throws Exception
    {
        checkRoleCorrect(CHECK_ID, PROJECT_NAME, ROLE_FQN, CONFIGURATION_FQN, STANDART_ROLE,
            "InteractiveOpenExternalReportsAndDataProcessors");
    }

    @Test
    public void testCustomRoleIncorrect() throws Exception
    {
        checkRoleIncorrect(CHECK_ID, PROJECT_NAME, ROLE_FQN, CONFIGURATION_FQN, STANDART_ROLE, null);
    }

    @Test
    public void testFullAccessRoleIncorrect() throws Exception
    {
        checkRoleIncorrect(CHECK_ID, PROJECT_NAME, ROLE_FQN, CONFIGURATION_FQN, STANDART_ROLE, "FullAccess");
    }

    @Test
    public void testSystemAdministratorRoleIncorrect() throws Exception
    {
        checkRoleIncorrect(CHECK_ID, PROJECT_NAME, ROLE_FQN, CONFIGURATION_FQN, STANDART_ROLE, "SystemAdministrator");
    }

}
