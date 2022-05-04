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
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.VISIBLE;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.VISIBLE__USER_VISIBLE;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.Visible;
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
    private static final List<String> LIST_SEGMENT = List.of("List", "Список"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final List<String> REF_SEGMENT = List.of("Ref", "Ссылка"); //$NON-NLS-1$ //$NON-NLS-2$

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
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FORM)
            .containment(VISIBLE)
            .features(VISIBLE__USER_VISIBLE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof Visible))
        {
            return;
        }

        Visible visible = (Visible)object;

        if (pathCheck(visible.eContents()) && visible.getUserVisible().isCommon())
        {
            resultAceptor.addIssue(
                Messages.FormListRefUserVisibilityEnabledCheck_User_visibility_is_not_disabled_for_the_Ref_field,
                visible);
        }
    }

    private boolean pathCheck(EList<EObject> eContents)
    {
        if (eContents.size() < 2 || !(eContents.get(1) instanceof AbstractDataPath))
        {
            return false;
        }

        EList<String> segments = ((AbstractDataPath)eContents.get(1)).getSegments();

        return segments.size() == 2 && LIST_SEGMENT.contains(segments.get(0)) && REF_SEGMENT.contains(segments.get(1));
    }
}
