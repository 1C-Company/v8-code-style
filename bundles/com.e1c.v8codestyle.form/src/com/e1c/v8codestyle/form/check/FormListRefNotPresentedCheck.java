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
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_ITEM_CONTAINER;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_ITEM_CONTAINER__ITEMS;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.form.model.DynamicListTableExtInfo;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.FormItemContainer;
import com._1c.g5.v8.dt.form.model.Table;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;

/**
 * Check if the Reference field is added to dynamic list.
 *
 * @author Olga Bozhko
 */
public class FormListRefNotPresentedCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-list-ref-not-presented-check"; //$NON-NLS-1$
    private static final List<String> REF_NAMES = List.of("Ref", "Ссылка"); //$NON-NLS-1$ //$NON-NLS-2$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormListRefNotPresentedCheck_title)
            .description(Messages.FormListRefNotPresentedCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FORM)
            .containment(FORM_ITEM_CONTAINER)
            .features(FORM_ITEM_CONTAINER__ITEMS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        if (monitor.isCanceled() || !(object instanceof Table))
        {
            return;
        }

        if (((Table)object).eContents().stream().filter(DynamicListTableExtInfo.class::isInstance).count() == 1)
        {
            FormItemContainer form = (Table)object;

            if (form.getItems().stream().noneMatch(nameCheck))
            {
                resultAceptor.addIssue(Messages.FormListRefNotPresentedCheck_The_Ref_field_is_not_added_to_dynamic_list,
                    form);
            }
        }

    }

    private Predicate<? super FormItem> nameCheck =
        item -> item.getName().contains(REF_NAMES.get(0)) || item.getName().contains(REF_NAMES.get(1));
}
