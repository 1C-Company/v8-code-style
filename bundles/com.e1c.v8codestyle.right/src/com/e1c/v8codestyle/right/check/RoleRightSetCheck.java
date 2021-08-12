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
package com.e1c.v8codestyle.right.check;

import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.OBJECT_RIGHT;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.OBJECT_RIGHT__RIGHT;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.OBJECT_RIGHT__VALUE;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.ROLE_DESCRIPTION;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.md.MdUtil;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.Role;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.rights.model.ObjectRight;
import com._1c.g5.v8.dt.rights.model.ObjectRights;
import com._1c.g5.v8.dt.rights.model.Right;
import com._1c.g5.v8.dt.rights.model.RightValue;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Abstract check that role has some right for any object.
 *
 * @author Dmitriy Marmyshev
 *
 */
public abstract class RoleRightSetCheck
    extends BasicCheck
{

    protected static final String EXCLUDE_ROLE_NAME_PATTERN_PARAMETER_NAME = "excludeRoleNamePattern"; //$NON-NLS-1$

    protected static final String EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME = "excludeObjectNamePattern"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    private final IBmModelManager bmModelManager;

    /**
     * Creates new instance which helps to check that role has specified right for an object.
     *
     * @param v8ProjectManager the V8 project manager, cannot be {@code null}.
     * @param bmModelManager  the BM model manager, cannot be {@code null}.
     */
    @Inject
    public RoleRightSetCheck(IV8ProjectManager v8ProjectManager, IBmModelManager bmModelManager)
    {
        this.v8ProjectManager = v8ProjectManager;
        this.bmModelManager = bmModelManager;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.SECURITY)
            .topObject(ROLE_DESCRIPTION)
            .containment(OBJECT_RIGHT)
            .features(OBJECT_RIGHT__RIGHT, OBJECT_RIGHT__VALUE)
            .parameter(EXCLUDE_ROLE_NAME_PATTERN_PARAMETER_NAME, String.class, StringUtils.EMPTY,
                Messages.RoleRightSetCheck_Exclude_Role_name_pattern)
            .parameter(EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME, String.class, StringUtils.EMPTY,
                Messages.RoleRightSetCheck_Exclude_Right_Object_name_pattern);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        ObjectRight objectRight = (ObjectRight)object;
        Right right = objectRight.getRight();

        if (right == null || !getRightName().getName().equals(right.getName()))
        {
            return;
        }

        Role role = null;

        RightValue rightValue = objectRight.getValue();
        if (rightValue == null)
        {
            role = getRole(objectRight);
            rightValue = RightsModelUtil.getDefaultRightValue(objectRight, role);
        }

        if (!RightsModelUtil.getBooleanRightValue(rightValue) || monitor.isCanceled())
        {
            return;
        }

        String excludeRoleNamePattern = parameters.getString(EXCLUDE_ROLE_NAME_PATTERN_PARAMETER_NAME);
        if (excludeRoleNamePattern != null && !excludeRoleNamePattern.isBlank())
        {
            if (role == null)
            {
                role = getRole(objectRight);
            }
            if (role == null || !role.getName().matches(excludeRoleNamePattern))
            {
                return;
            }
        }

        ObjectRights rights = EcoreUtil2.getContainerOfType(objectRight, ObjectRights.class);
        MdObject mdObject = rights.getObject() instanceof MdObject ? (MdObject)rights.getObject() : null;

        String excludeObjectNamePattern = parameters.getString(EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME);
        if (excludeObjectNamePattern != null && !excludeObjectNamePattern.isBlank() && mdObject != null
            && mdObject.getName().matches(excludeObjectNamePattern))
        {
            return;
        }

        String message = getIssueMessage(right, mdObject);
        resultAceptor.addIssue(message, OBJECT_RIGHT__RIGHT);
    }

    /**
     * Gets the object right name that need to check that exist in role rights.
     *
     * @return the right name constant, cannot return {@code null}.
     */
    protected abstract RightName getRightName();

    /**
     * Creates formated issue message for the right and the MD object.
     *
     * @param right the right that forbidden to set for the MD object, cannot be {@code null}.
     * @param mdObject the MD object that has forbidden right, cannot be {@code null}.
     * @return the formatted issue message that right set for the object, cannot return {@code null}.
     */
    protected String getIssueMessage(Right right, MdObject mdObject)
    {
        IV8Project project = mdObject == null ? null : v8ProjectManager.getProject(mdObject);
        String rightName = getRightName(right, project);
        String mdObjectName = getMdObjectName(mdObject, project);
        return MessageFormat.format(Messages.RoleRightSetCheck_Role_right__0__set_for__1, rightName, mdObjectName);
    }

    private Role getRole(ObjectRight objectRight)
    {
        IBmModel model = bmModelManager.getModel(objectRight);
        RoleDescription description = EcoreUtil2.getContainerOfType(objectRight, RoleDescription.class);
        Role role = RightsModelUtil.getOwner(description, model);
        return role;
    }

    private String getRightName(Right right, IV8Project project)
    {
        if (project != null && project.getScriptVariant() == ScriptVariant.RUSSIAN)
        {
            return right.getNameRu();
        }
        return right.getName();
    }

    private String getMdObjectName(MdObject mdObject, IV8Project project)
    {
        if (mdObject == null)
            return "Unknown"; //$NON-NLS-1$

        if (project != null && project.getScriptVariant() == ScriptVariant.RUSSIAN)
        {
            return MdUtil.getFullyQualifiedNameRu(mdObject).toString();
        }
        return MdUtil.getFullyQualifiedName(mdObject).toString();
    }

}
