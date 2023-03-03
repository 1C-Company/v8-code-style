/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *     Vadim Goncharov - issue #262
 *******************************************************************************/

package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.DYNAMIC_LIST_EXT_INFO;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.DYNAMIC_LIST_EXT_INFO__LIST_SETTINGS;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.dcs.model.settings.DataCompositionConditionalAppearance;
import com._1c.g5.v8.dt.dcs.model.settings.DataCompositionSettings;
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
 * The check find form attributes of type "Dynamic List" that use conditional appearance.
 * @author Vadim Goncharov
 */
public class DynamicListConditionalAppearanceUseCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-dynamic-list-conditional-appearance-use"; //$NON-NLS-1$

    public DynamicListConditionalAppearanceUseCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DynamicListConditionalAppearanceUseCheck_title)
            .description(Messages.DynamicListConditionalAppearanceUseCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(710, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipBaseFormExtension())
            .topObject(FORM)
            .containment(DYNAMIC_LIST_EXT_INFO)
            .features(DYNAMIC_LIST_EXT_INFO__LIST_SETTINGS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DynamicListExtInfo dl = (DynamicListExtInfo)object;
        DataCompositionSettings listSettings = dl.getListSettings();
        DataCompositionConditionalAppearance conditionalAppearance = listSettings.getConditionalAppearance();
        if (!monitor.isCanceled() && conditionalAppearance.getItems() != null
            && !conditionalAppearance.getItems().isEmpty())
        {
            FormAttribute formAttribute = EcoreUtil2.getContainerOfType(dl, FormAttribute.class);
            if (formAttribute == null)
            {
                return;
            }

            resultAceptor.addIssue(MessageFormat.format(
                Messages.DynamicListConditionalAppearanceUseCheck_Dynamic_list_use_conditional_appearance,
                formAttribute.getName()), formAttribute);

        }
    }

}
