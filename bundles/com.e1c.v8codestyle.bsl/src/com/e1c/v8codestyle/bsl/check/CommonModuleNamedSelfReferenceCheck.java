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
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check self references by name in common modules.
 * Modules with "ПовтИсп/Cached" in name can be self referenced.
 *
 * @author Maxim Galios
 *
 */
public class CommonModuleNamedSelfReferenceCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "common-module-named-self-reference"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CommonModuleNamedSelfReferenceCheck_title)
            .description(Messages.CommonModuleNamedSelfReferenceCheck_description)
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
        if (monitor.isCanceled() || !(featureAccessSource instanceof StaticFeatureAccess))
        {
            return;
        }

        StaticFeatureAccess source = (StaticFeatureAccess)featureAccessSource;

        if (isReferenceExcessive(source))
        {
            resultAceptor.addIssue(Messages.CommonModuleNamedSelfReferenceCheck_issue, source);
        }
    }

    private boolean isReferenceExcessive(StaticFeatureAccess source)
    {
        Module module = EcoreUtil2.getContainerOfType(source, Module.class);
        if (module.getModuleType() != ModuleType.COMMON_MODULE)
        {
            return false;
        }
        CommonModule commonModule = (CommonModule)module.getOwner();
        return (commonModule.getReturnValuesReuse() == ReturnValuesReuse.DONT_USE)
            && StringUtils.equals(commonModule.getName(), source.getName());
    }
}
