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
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.ColumnGroupExtInfo;
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormGroup;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.metadata.dbview.DbViewFieldDef;
import com._1c.g5.v8.dt.metadata.dbview.DbViewTableDef;
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
public class FormListFieldRefNotAddedCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-list-field-ref-not-added"; //$NON-NLS-1$
    private static final List<String> LIST_ABSTRACT_DATA_PATH = List.of("List", "Список"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final List<String> REF_ABSTRACT_DATA_PATH = List.of("Ref", "Ссылка"); //$NON-NLS-1$ //$NON-NLS-2$

    private static final Predicate<? super DbViewFieldDef> FIELD_NAME_CHECK =
        name -> (name.getName() != null) && name.getName().equals(REF_ABSTRACT_DATA_PATH.get(0));

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormListFieldRefNotAddedCheck_title)
            .description(Messages.FormListFieldRefNotAddedCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
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
        if (object instanceof Table
            || (object instanceof FormGroup && ((FormGroup)object).getExtInfo() instanceof ColumnGroupExtInfo
                && ((Form)((FormGroup)object).bmGetTopObject()).getItems().get(1) instanceof Table))
        {
            Table table = object instanceof Table ? (Table)object
                : (Table)((Form)((FormGroup)object).bmGetTopObject()).getItems().get(1);
            FormAttribute attr = (FormAttribute)table.bmGetTopObject()
                .eContents()
                .stream()
                .filter(FormAttribute.class::isInstance)
                .findAny()
                .orElse(null);
            if (attr != null)
            {
                DbViewTableDef tableDef = (DbViewTableDef)((DynamicListExtInfo)attr.getExtInfo()).getMainTable();
                if (tableDef.getFields() != null && tableDef.getFields().stream().anyMatch(FIELD_NAME_CHECK)
                    && !pathCheck(table.getItems()))
                {

                    resultAceptor.addIssue(
                        Messages.FormListFieldRefNotAddedCheck_The_Ref_field_is_not_added_to_dynamic_list,
                        FORM_ITEM_CONTAINER__ITEMS);

                }
            }
        }

    }

    private static boolean pathCheck(EList<FormItem> items)
    {
        if (!items.isEmpty())
        {
            for (int i = 0; i < items.size(); i++)
            {
                if (items.get(i) instanceof FormField && !items.get(i)
                    .eContents()
                    .stream()
                    .filter(AbstractDataPath.class::isInstance)
                    .filter(item -> itemPathCheck((AbstractDataPath)item))
                    .collect(Collectors.toList())
                    .isEmpty())
                {
                    return true;
                }
                if (items.get(i) instanceof FormGroup && pathCheck(((FormGroup)items.get(i)).getItems()))
                {
                    return true;

                }
            }
        }
        return false;
    }

    private static boolean itemPathCheck(AbstractDataPath path)
    {
        EList<String> segments = path.getSegments();

        return segments.size() == 2 && LIST_ABSTRACT_DATA_PATH.contains(segments.get(0))
            && REF_ABSTRACT_DATA_PATH.contains(segments.get(1));
    }
}
