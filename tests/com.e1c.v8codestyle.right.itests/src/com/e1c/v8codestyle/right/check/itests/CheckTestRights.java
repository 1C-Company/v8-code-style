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
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com._1c.g5.wiring.ServiceAccess;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;

/**
 * @author Aleksandr Kapralov
 *
 */
public class CheckTestRights
    extends CheckTestBase
{

    protected void updateRole(IDtProject dtProject, String roleFqn, String objectFqn, RightName[] rightNames,
        String newName)
    {
        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change type")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject object = transaction.getTopObjectByFqn(roleFqn);
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
                    transaction.updateTopObjectFqn(object, String.join(".", role.eClass().getName(), newName));
                    transaction.updateTopObjectFqn(object,
                        String.join(".", role.eClass().getName(), newName, "Rights"));
                }

                return null;
            }
        });
        waitForDD(dtProject);
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

}
