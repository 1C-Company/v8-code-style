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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FEATURE_ACCESS__NAME;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STATIC_FEATURE_ACCESS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check oudated self references in from modules. Outdated are references with "ЭтаФорма/ThisForm".
 *
 * @author Maxim Galios
 *
 */
public class FormSelfReferenceOutdatedCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "form-self-reference"; //$NON-NLS-1$

    private static final String THIS_FORM = "ThisForm"; //$NON-NLS-1$
    private static final String THIS_FORM_RU = "ЭтаФорма"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormSelfReferenceOutdatedCheck_Title)
            .description(Messages.FormSelfReferenceOutdatedCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(467, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(STATIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        StaticFeatureAccess source = (StaticFeatureAccess)object;

        if (isAliasOutdated(source))
        {
            resultAceptor.addIssue(Messages.FormSelfReferenceOutdatedCheck_Issue, source, FEATURE_ACCESS__NAME);
        }
    }

    private boolean isAliasOutdated(StaticFeatureAccess object)
    {
        String name = object.getName();
        if (THIS_FORM_RU.equalsIgnoreCase(name) || THIS_FORM.equalsIgnoreCase(name))
        {
            Module module = EcoreUtil2.getContainerOfType(object, Module.class);
            return module.getModuleType() == ModuleType.FORM_MODULE;
        }

        return false;
    }
}
