/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
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
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.INPUT_FIELD_EXT_INFO;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.INPUT_FIELD_EXT_INFO__CHOICE_LIST;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.INPUT_FIELD_EXT_INFO__LIST_CHOICE_MODE;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.form.model.InputFieldExtInfo;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Check input filed has correct list choice mode if choice list is not empty.
 *
 * @author Dmitriy Marmyshev
 */
public final class InputFieldListChoiceMode
    extends BasicCheck
{

    private static final String CHECK_ID = "input-field-list-choice-mode"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.InputFieldListChoiceMode_title)
            .description(Messages.InputFieldListChoiceMode_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .topObject(FORM)
            .containment(INPUT_FIELD_EXT_INFO)
            .features(INPUT_FIELD_EXT_INFO__LIST_CHOICE_MODE, INPUT_FIELD_EXT_INFO__CHOICE_LIST);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        InputFieldExtInfo extInfo = (InputFieldExtInfo)object;
        if (!extInfo.getChoiceList().isEmpty() && !extInfo.isListChoiceMode())
        {
            resultAceptor.addIssue(
                Messages.InputFieldListChoiceMode_Form_input_field_the_list_choice_mode_not_set_with_filled_choice_list,
                INPUT_FIELD_EXT_INFO__LIST_CHOICE_MODE);
        }
    }

}
