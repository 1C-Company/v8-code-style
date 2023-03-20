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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STATIC_FEATURE_ACCESS;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checking the use of the FormDataToValue method that is not recommended.
 *
 * @author Artem Iliukhin
 */
public class UsingFormDataToValueCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "using-form-data-to-value"; //$NON-NLS-1$
    private static final String NAME = "FormDataToValue"; //$NON-NLS-1$
    private static final String NAME_RU = "ДанныеФормыВЗначение"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormDataToValueCheck_Title)
            .description(Messages.FormDataToValueCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.TRIVIAL)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(409, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(STATIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        StaticFeatureAccess featureAccess = (StaticFeatureAccess)object;
        String name = featureAccess.getName();
        if (NAME.equalsIgnoreCase(name) || NAME_RU.equalsIgnoreCase(name))
        {
            resultAceptor.addIssue(Messages.FormDataToValueCheck_Issue, object);
        }
    }
}
