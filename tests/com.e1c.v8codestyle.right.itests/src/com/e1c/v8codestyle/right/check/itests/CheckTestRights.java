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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

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
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.wiring.ServiceAccess;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;

/**
 * @author Aleksandr Kapralov
 *
 */
public class CheckTestRights
    extends CheckTestBase
{

    protected void checkRoleCorrect(String checkId, String projectName, String workspaceRightsFqn, String mdObjectFqn,
        String rightName, String newRoleName) throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(projectName);
        assertNotNull(dtProject);

        ObjectRights objectRights = checkRole(dtProject, workspaceRightsFqn, mdObjectFqn, rightName, newRoleName);

        checkMarkerCorrect(objectRights, checkId, dtProject);
    }

    protected void checkRoleIncorrect(String checkId, String projectName, String workspaceRightsFqn, String mdObjectFqn,
        String rightName, String newRoleName) throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(projectName);
        assertNotNull(dtProject);

        ObjectRights objectRights = checkRole(dtProject, workspaceRightsFqn, mdObjectFqn, rightName, newRoleName);

        checkMarkerIncorrect(objectRights, checkId, dtProject);
    }

    private ObjectRights checkRole(IDtProject dtProject, String workspaceRightsFqn, String mdObjectFqn,
        String rightName, String newRoleName)
    {
        updateRole(dtProject, workspaceRightsFqn, mdObjectFqn, rightName, newRoleName);

        String roleRightsFqn = workspaceRightsFqn;
        if (newRoleName != null)
        {
            roleRightsFqn = String.join(".", "Role", newRoleName, "Rights");
        }
        IBmObject top = getTopObjectByFqn(roleRightsFqn, dtProject);
        assertTrue(top instanceof RoleDescription);

        RoleDescription description = (RoleDescription)top;
        EObject mdObject = getTopObjectByFqn(mdObjectFqn, dtProject);
        return RightsModelUtil.getOrCreateObjectRights(mdObject, description);
    }

    private void checkMarkerCorrect(ObjectRights objectRights, String checkId, IDtProject dtProject)
    {
        for (ObjectRight objectRight : objectRights.getRights())
        {
            Marker marker = getFirstMarker(checkId, objectRight, dtProject);
            assertNull(marker);
        }
    }

    private void checkMarkerIncorrect(ObjectRights objectRights, String checkId, IDtProject dtProject)
    {
        for (ObjectRight objectRight : objectRights.getRights())
        {
            Marker marker = getFirstMarker(checkId, objectRight, dtProject);
            assertNotNull(marker);
        }
    }

    private void updateRole(IDtProject dtProject, String roleRightsFqn, String objectFqn, String rightName,
        String newRoleName)
    {
        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change type")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject object = transaction.getTopObjectByFqn(roleRightsFqn);
                if (!(object instanceof RoleDescription))
                {
                    return null;
                }

                RoleDescription description = (RoleDescription)object;
                Role role = RightsModelUtil.getOwner(description, model);

                EObject configuration = transaction.getTopObjectByFqn(objectFqn);

                RightValue defaultRightValue = RightsModelUtil.getDefaultRightValue(configuration, role);

                ObjectRights objectRights = RightsModelUtil.getOrCreateObjectRights(configuration, description);

                for (ObjectRight objectRight : ECollections.newBasicEList(objectRights.getRights()))
                {
                    RightsModelUtil.changeObjectRight(defaultRightValue, defaultRightValue, objectRights,
                        objectRight.getRight());
                }

                Right right = addRight(rightName, dtProject.getWorkspaceProject());
                if (right != null)
                {
                    RightsModelUtil.changeObjectRight(RightsModelUtil.getRightValue(true), defaultRightValue,
                        objectRights, right);
                }

                RightsModelUtil.removeEmptyObjectRights(description, objectRights);

                if (newRoleName != null)
                {
                    role.setName(newRoleName);
                    transaction.updateTopObjectFqn(object, String.join(".", role.eClass().getName(), newRoleName));
                    transaction.updateTopObjectFqn(object,
                        String.join(".", role.eClass().getName(), newRoleName, "Rights"));
                }

                return null;
            }
        });
        waitForDD(dtProject);
    }

    private Right addRight(String rightName, IProject project)
    {
        IRuntimeVersionSupport runtimeVersionSupport = ServiceAccess.get(IRuntimeVersionSupport.class);
        Version version = runtimeVersionSupport.getRuntimeVersion(project);
        IEObjectProvider rightsProvider = IEObjectProvider.Registry.INSTANCE.get(RightsPackage.Literals.RIGHT, version);
        Iterable<IEObjectDescription> descrs = rightsProvider.getEObjectDescriptions(input -> true);
        for (IEObjectDescription descr : descrs)
        {
            String name = descr.getName().toString();
            if (rightName.equals(name))
            {
                return (Right)descr.getEObjectOrProxy();
            }
        }
        return null;
    }

}
