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

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.FormAttribute;
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
 * Check if Use Always flag is enabled for the Reference attribute in dynamic list.
 *
 * @author Olga Bozhko
 */
public class FormListRefUseAlwaysFlagDisabledCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-list-ref-use-always-flag-disabled"; //$NON-NLS-1$
    private static final List<String> REF_ABSTRACT_DATA_PATH = List.of("Ref", "Список"); //$NON-NLS-1$ //$NON-NLS-2$

    private static final Predicate<? super DbViewFieldDef> NAME_CHECK =
        name -> name.getName().equals(REF_ABSTRACT_DATA_PATH.get(0));

    private static Predicate<AbstractDataPath> pathCheck = path -> {
        EList<String> segments = path.getSegments();

        if (segments.size() != 2)
        {
            return false;
        }

        return segments.get(1).equals(REF_ABSTRACT_DATA_PATH.get(0))
            || segments.get(1).equals(REF_ABSTRACT_DATA_PATH.get(1));
    };

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
            .severity(IssueSeverity.MINOR)
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
        FormAttribute formAttribute = (FormAttribute)object;
        if (formAttribute.getExtInfo() instanceof DynamicListExtInfo)
        {
            DbViewTableDef tableDef = (DbViewTableDef)((DynamicListExtInfo)formAttribute.getExtInfo()).getMainTable();
            if (tableDef != null && tableDef.getFields().stream().anyMatch(NAME_CHECK)
                && formAttribute.getNotDefaultUseAlwaysAttributes().stream().noneMatch(pathCheck))
            {
                resultAceptor.addIssue(
                    Messages.FormListRefUseAlwaysFlagDisabledCheck_UseAlways_flag_is_disabled_for_the_Ref_field,
                    FORM_ATTRIBUTE__NOT_DEFAULT_USE_ALWAYS_ATTRIBUTES);
            }
        }
    }
}
