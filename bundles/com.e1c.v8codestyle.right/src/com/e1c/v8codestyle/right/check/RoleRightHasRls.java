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

import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.RLS;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.RLS__CONDITION;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.ROLE_DESCRIPTION;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.md.MdUtil;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.Role;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.rights.model.ObjectRight;
import com._1c.g5.v8.dt.rights.model.ObjectRights;
import com._1c.g5.v8.dt.rights.model.Rls;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Checks that role has right restriction (RLS) for roles with full-access level.
 *
 * @author Dmitriy Marmyshev
 */
public class RoleRightHasRls
    extends BasicCheck
{

    private static final String CHECK_ID = "role-right-has-rls"; //$NON-NLS-1$

    private static final String ROLE_NAME_PATTERN_PARAMETER_NAME = "roleNamePattern"; //$NON-NLS-1$

    private static final String ROLE_NAME_PATTERN_PARAMETER_DEFAULT =
        "ПолныеПрава|АдминистраторСистемы|FullAccess|SystemAdministrator"; //$NON-NLS-1$

    public static final String EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME = "excludeObjectNamePattern"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    private final IBmModelManager bmModelManager;

    /**
     * Creates new instance of check that find RLS set common roles.
     *
     * @param v8ProjectManager the V8 project manager, cannot be {@code null}.
     * @param bmModelManager  the BM model manager, cannot be {@code null}.
     */
    @Inject
    public RoleRightHasRls(IV8ProjectManager v8ProjectManager, IBmModelManager bmModelManager)
    {
        this.v8ProjectManager = v8ProjectManager;
        this.bmModelManager = bmModelManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Rls rls = (Rls)object;

        if (rls.getCondition() != null && !rls.getCondition().isBlank())
        {
            String roleNamePattern = parameters.getString(ROLE_NAME_PATTERN_PARAMETER_NAME);
            if (roleNamePattern != null && !roleNamePattern.isBlank())
            {
                IBmModel model = bmModelManager.getModel(rls);
                RoleDescription description = EcoreUtil2.getContainerOfType(rls, RoleDescription.class);
                Role role = RightsModelUtil.getOwner(description, model);
                if (role == null || !role.getName().matches(roleNamePattern))
                {
                    return;
                }
            }

            ObjectRight right = EcoreUtil2.getContainerOfType(rls, ObjectRight.class);
            ObjectRights rights = EcoreUtil2.getContainerOfType(right, ObjectRights.class);
            MdObject mdObject = rights.getObject() instanceof MdObject ? (MdObject)rights.getObject() : null;

            String excludeObjectNamePattern = parameters.getString(EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME);
            if (excludeObjectNamePattern != null && !excludeObjectNamePattern.isBlank() && mdObject != null
                && mdObject.getName().matches(excludeObjectNamePattern))
            {
                return;
            }
            IV8Project project = v8ProjectManager.getProject(rls);

            String rightName = getRightName(right, project);
            String mdName = getMdObjectName(mdObject, project);

            String message =
                MessageFormat.format(Messages.RoleRightHasRls_Role_Right__0__for__1__has_RLS, rightName, mdName);
            resultAceptor.addIssue(message, RLS__CONDITION);
        }

    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RoleRightHasRls_title)
            .description(Messages.RoleRightHasRls_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.WARNING)
            .topObject(ROLE_DESCRIPTION)
            .containment(RLS)
            .features(RLS__CONDITION)
            .parameter(ROLE_NAME_PATTERN_PARAMETER_NAME, String.class, ROLE_NAME_PATTERN_PARAMETER_DEFAULT,
                Messages.RoleRightHasRls_Role_name_pattern)
            .parameter(EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME, String.class, "", //$NON-NLS-1$
                Messages.RoleRightHasRls_Exclude_Right_Object_name_pattern);
    }

    private String getRightName(ObjectRight right, IV8Project project)
    {
        Assert.isNotNull(project);
        if (project.getScriptVariant() == ScriptVariant.RUSSIAN)
        {
            return right.getRight().getNameRu();
        }
        return right.getRight().getName();
    }

    private String getMdObjectName(MdObject mdObject, IV8Project project)
    {
        Assert.isNotNull(project);
        if (mdObject == null)
        {
            return "Unknown"; //$NON-NLS-1$
        }

        if (project.getScriptVariant() == ScriptVariant.RUSSIAN)
        {
            return MdUtil.getFullyQualifiedNameRu(mdObject).toString();
        }
        return MdUtil.getFullyQualifiedName(mdObject).toString();
    }

}
