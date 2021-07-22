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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Role;
import com._1c.g5.v8.dt.rights.model.ObjectRights;
import com._1c.g5.v8.dt.rights.model.Right;
import com._1c.g5.v8.dt.rights.model.RightsFactory;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.right.check.AdministrationRight;

/**
 * Tests for {@link AdministrationRight} check.
 *
 * @author Aleksandr Kapralov
 */
public class AdministrationRightTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "StandartRoles";

    private static final RightName[] STANDART_ROLES = new RightName[] { RightName.ADMINISTRATION,
        RightName.DATA_ADMINISTRATION, RightName.CONFIGURATION_EXTENSIONS_ADMINISTRATION, RightName.ACTIVE_USERS };

    @Test
    public void testStandartRoleCorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        updateRole(dtProject, "Role.StandartRole.Rights", STANDART_ROLES, "Administration");

        IBmObject top = getTopObjectByFqn("Role.Administration.Rights", dtProject);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        assertNotNull(markers);
        assertEquals(0, markers.length);
    }

    @Test
    public void testCustomRoleIncorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String standartFqn = "Role.CustomRole.Rights";

        updateRole(dtProject, standartFqn, STANDART_ROLES, null);

        IBmObject top = getTopObjectByFqn(standartFqn, dtProject);

        Marker[] markers = markerManager.getNestedMarkers(dtProject.getWorkspaceProject(), top.bmGetId());
        assertNotNull(markers);
        assertEquals(0, markers.length);
    }

    private void updateRole(IDtProject dtProject, String fqn, RightName[] rightNames, String newName)
    {
        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change type")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject object = transaction.getTopObjectByFqn(fqn);
                if (!(object instanceof RoleDescription))
                {
                    return null;
                }

                RoleDescription description = (RoleDescription)object;

                EObject configuration = transaction.getTopObjectByFqn("Configuration");

                ObjectRights objectRights = RightsModelUtil.getOrCreateObjectRights(configuration, description);
                objectRights.getRights().clear();

                for (RightName rightName : rightNames)
                {
                    Right right = RightsFactory.eINSTANCE.createRight();
                    right.setName(rightName.getName());
                    RightsModelUtil.changeObjectRight(RightsModelUtil.getRightValue(true),
                        RightsModelUtil.getRightValue(false), objectRights, right);
                }

                if (newName != null)
                {
                    Role role = RightsModelUtil.getOwner(description, model);
                    role.setName(newName);
                    transaction.updateTopObjectFqn(object, role.eClass().getName() + "." + newName);
                }

                return null;
            }
        });
        waitForDD(dtProject);
    }
}
