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
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.VISIBLE__USER_VISIBLE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.ADJUSTABLE_BOOLEAN;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.ADJUSTABLE_BOOLEAN__COMMON;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.metadata.mdclass.AdjustableBoolean;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;

/**
 * Check if User Visibility is disabled for the Ref field in dynamic list.
 *
 * @author Olga Bozhko
 */
public class FormListRefUserVisibilityEnabledCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-list-ref-user-visibility-enabled"; //$NON-NLS-1$

    private static final String REF_SEGMENT = "Ref"; //$NON-NLS-1$

    private static final String REF_SEGMENT_RU = "Ссылка"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormListRefUserVisibilityEnabledCheck_title)
            .description(Messages.FormListRefUserVisibilityEnabledCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new SkipBaseFormExtension())
            .extension(new StandardCheckExtension(702, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FORM)
            .containment(ADJUSTABLE_BOOLEAN)
            .features(ADJUSTABLE_BOOLEAN__COMMON);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        AdjustableBoolean adjBoolean = (AdjustableBoolean)object;
        if (adjBoolean.isCommon() && adjBoolean.eContainer() instanceof FormField
            && adjBoolean.eContainmentFeature().equals(VISIBLE__USER_VISIBLE)
            && isRefPath(((FormField)adjBoolean.eContainer()).getDataPath()))
        {
            Table table = EcoreUtil2.getContainerOfType(adjBoolean, Table.class);
            if (table == null)
            {
                return;
            }

            FormField formField = (FormField)(adjBoolean.eContainer());
            resultAceptor.addIssue(
                MessageFormat.format(
                    Messages.FormListRefUserVisibilityEnabledCheck_User_visibility_is_not_disabled_for_the_Ref_field,
                    formField.getName(), table.getName()),
                ADJUSTABLE_BOOLEAN__COMMON);
        }

    }

    private boolean isRefPath(AbstractDataPath dataPath)
    {
        EList<String> segments = dataPath.getSegments();
        return segments.size() == 2
            && (REF_SEGMENT.equalsIgnoreCase(segments.get(1)) || REF_SEGMENT_RU.equalsIgnoreCase(segments.get(1)));
    }
}
