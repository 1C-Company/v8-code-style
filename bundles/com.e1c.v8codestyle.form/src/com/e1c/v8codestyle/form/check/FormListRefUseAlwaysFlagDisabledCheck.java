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
package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_ATTRIBUTE;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_ATTRIBUTE__NOT_DEFAULT_USE_ALWAYS_ATTRIBUTES;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;

/**
 * Check if Use Always flag is enabled for the Reference attribute in dynamic list.
 *
 * @author Olga Bozhko
 */
public class FormListRefUseAlwaysFlagDisabledCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-list-ref-use-always-flag-disabled"; //$NON-NLS-1$
    private static final String REF_ABSTRACT_DATA_PATH = "/List/Ref"; //$NON-NLS-1$
    private static final String REF_ABSTRACT_DATA_PATH_RU = "/List/Ссылка"; //$NON-NLS-1$
    private static final String REF_ABSTRACT_DATA_PATH_RU_FULL = "/Список/Ссылка"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormListRefUseAlwaysFlagDisabledCheck_title)
            .description(Messages.FormListRefUseAlwaysFlagDisabledCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FORM)
            .containment(FORM_ATTRIBUTE)
            .features(FORM_ATTRIBUTE__NOT_DEFAULT_USE_ALWAYS_ATTRIBUTES);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        if (monitor.isCanceled() || !(object instanceof FormAttribute))
        {
            return;
        }

        FormAttribute formAttribute = (FormAttribute)object;
        if (formAttribute.getExtInfo() instanceof DynamicListExtInfo && formAttribute.getNotDefaultUseAlwaysAttributes()
            .stream()
            .noneMatch(
                p -> p.toString().equals(REF_ABSTRACT_DATA_PATH) || p.toString().equals(REF_ABSTRACT_DATA_PATH_RU)
                    || p.toString().equals(REF_ABSTRACT_DATA_PATH_RU_FULL)))
        {
            resultAceptor.addIssue(
                Messages.FormListRefUseAlwaysFlagDisabledCheck_UseAlways_flag_is_disabled_for_the_Ref_field,
                formAttribute);
        }

    }

}
