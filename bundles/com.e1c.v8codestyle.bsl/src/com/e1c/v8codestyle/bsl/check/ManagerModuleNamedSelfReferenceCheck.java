/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * CHeck self reference by name in manager modules.
 *
 * @author Maxim Galios
 *
 */
public class ManagerModuleNamedSelfReferenceCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "manager-module-named-self-reference"; //$NON-NLS-1$

    private IQualifiedNameProvider bslQualifiedNameProvider;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ManagerModuleNamedSelfReferenceCheck_title)
            .description(Messages.ManagerModuleNamedSelfReferenceCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Expression featureAccessSource = ((DynamicFeatureAccess)object).getSource();
        Module module = EcoreUtil2.getContainerOfType(featureAccessSource, Module.class);

        if (monitor.isCanceled() || !(featureAccessSource instanceof DynamicFeatureAccess)
            || module.getModuleType() != ModuleType.MANAGER_MODULE)
        {
            return;
        }

        DynamicFeatureAccess source = (DynamicFeatureAccess)featureAccessSource;

        Expression managerTypeExpression = source.getSource();
        if (monitor.isCanceled() || !(managerTypeExpression instanceof StaticFeatureAccess))
        {
            return;
        }

        StaticFeatureAccess managerType = (StaticFeatureAccess)managerTypeExpression;

        if (isManagerTypeValid(managerType) && isReferenceExcessive(source, module))
        {
            resultAceptor.addIssue(Messages.ManagerModuleNamedSelfReferenceCheck_issue, source);
        }
    }

    private boolean isManagerTypeValid(StaticFeatureAccess managerType)
    {
        CaseInsensitiveString managerTypeName = new CaseInsensitiveString(managerType.getName());
        return LinkPart.MD_OBJECT_MANAGERS.containsKey(managerTypeName)
            || LinkPart.MD_OBJECT_MANAGERS_RU.containsKey(managerTypeName);
    }

    private boolean isReferenceExcessive(DynamicFeatureAccess source, Module module)
    {
        return StringUtils.equals(getNameProvider().getFullyQualifiedName(module).getSegment(1),
            source.getName());
    }

    private IQualifiedNameProvider getNameProvider()
    {
        if (bslQualifiedNameProvider == null)
        {
            IResourceServiceProvider resourceServiceProvider =
                IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
            bslQualifiedNameProvider = resourceServiceProvider.get(IQualifiedNameProvider.class);
        }
        return bslQualifiedNameProvider;
    }
}
