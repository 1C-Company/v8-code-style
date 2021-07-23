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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Role;
import com._1c.g5.v8.dt.platform.IEObjectProvider;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.v8.dt.platform.version.Version;
import com._1c.g5.v8.dt.rights.model.ObjectRight;
import com._1c.g5.v8.dt.rights.model.ObjectRights;
import com._1c.g5.v8.dt.rights.model.Right;
import com._1c.g5.v8.dt.rights.model.RightValue;
import com._1c.g5.v8.dt.rights.model.RightsPackage;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.wiring.ServiceAccess;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.right.check.AllFunctionsModeRight;

/**
 * Tests for {@link AllFunctionsModeRight} check.
 *
 * @author Aleksandr Kapralov
 */
public class AllFunctionsModeRightTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "StandartRoles";

    private static final String CONFIGURATION_FQN = "Configuration";

    private static final String CHECK_ID = "all-functions-mode-right"; //$NON-NLS-1$

    private static final RightName[] STANDART_ROLES = new RightName[] { RightName.ALL_FUNCTIONS_MODE };

    @Test
    public void testStandartRoleCorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        updateRole(dtProject, "Role.StandartRole.Rights", STANDART_ROLES, "Administration");

        IBmObject top = getTopObjectByFqn("Role.Administration.Rights", dtProject);
        assertTrue(top instanceof RoleDescription);

        RoleDescription description = (RoleDescription)top;
        EObject configuration = getTopObjectByFqn(CONFIGURATION_FQN, dtProject);
        ObjectRights objectRights = RightsModelUtil.getOrCreateObjectRights(configuration, description);

        for (ObjectRight objectRight : objectRights.getRights())
        {
            Marker marker = getFirstMarker(CHECK_ID, objectRight, dtProject);
            assertNull(marker);
        }
    }

    @Test
    public void testCustomRoleIncorrect() throws CoreException
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        String standartFqn = "Role.CustomRole.Rights";

        updateRole(dtProject, standartFqn, STANDART_ROLES, null);

        IBmObject top = getTopObjectByFqn(standartFqn, dtProject);
        assertTrue(top instanceof RoleDescription);

        RoleDescription description = (RoleDescription)top;
        EObject configuration = getTopObjectByFqn(CONFIGURATION_FQN, dtProject);
        ObjectRights objectRights = RightsModelUtil.getOrCreateObjectRights(configuration, description);

        for (ObjectRight objectRight : objectRights.getRights())
        {
            Marker marker = getFirstMarker(CHECK_ID, objectRight, dtProject);
            assertNotNull(marker);
        }
    }

    private Right addRight(RightName rightName, IProject project)
    {
        IRuntimeVersionSupport runtimeVersionSupport = ServiceAccess.get(IRuntimeVersionSupport.class);
        Version version = runtimeVersionSupport.getRuntimeVersion(project);
        IEObjectProvider rightsProvider = IEObjectProvider.Registry.INSTANCE.get(RightsPackage.Literals.RIGHT, version);
        Iterable<IEObjectDescription> descrs = rightsProvider.getEObjectDescriptions(input -> true);
        for (IEObjectDescription descr : descrs)
        {
            String name = descr.getName().toString();
            if (rightName.getName().equals(name))
            {
                return (Right)descr.getEObjectOrProxy();
            }
        }
        return null;
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
                Role role = RightsModelUtil.getOwner(description, model);

                EObject configuration = transaction.getTopObjectByFqn(CONFIGURATION_FQN);

                RightValue defaultRightValue = RightsModelUtil.getDefaultRightValue(configuration, role);

                ObjectRights objectRights = RightsModelUtil.getOrCreateObjectRights(configuration, description);

                for (ObjectRight objectRight : ECollections.newBasicEList(objectRights.getRights()))
                {
                    RightsModelUtil.changeObjectRight(defaultRightValue, defaultRightValue, objectRights,
                        objectRight.getRight());
                }

                for (RightName rightName : rightNames)
                {
                    Right right = addRight(rightName, dtProject.getWorkspaceProject());
                    if (right == null)
                    {
                        continue;
                    }
                    RightsModelUtil.changeObjectRight(RightsModelUtil.getRightValue(true), defaultRightValue,
                        objectRights, right);
                }

                RightsModelUtil.removeEmptyObjectRights(description, objectRights);

                if (newName != null)
                {
                    role.setName(newName);
                    transaction.updateTopObjectFqn(object, role.eClass().getName() + "." + newName + ".Rights");
                }

                return null;
            }
        });
        waitForDD(dtProject);
    }
}
