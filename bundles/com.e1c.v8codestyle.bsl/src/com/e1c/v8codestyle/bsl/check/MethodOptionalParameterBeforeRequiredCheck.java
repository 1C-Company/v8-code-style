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
 *******************************************************************************/

package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Method;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check methods formal params, that optional parameter before required.
 * @author Vadim Goncharov
 */
public class MethodOptionalParameterBeforeRequiredCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "method-optional-parameter-before-required"; //$NON-NLS-1$

    public MethodOptionalParameterBeforeRequiredCheck()
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
        builder.title(Messages.OptionalParameterBeforeRequiredCheck_title)
            .description(Messages.OptionalParameterBeforeRequiredCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.TRIVIAL)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(640, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        Method method = (Method)object;
        EList<FormalParam> params = method.getFormalParams();
        if (monitor.isCanceled() || params.isEmpty())
        {
            return;
        }

        int indexOfOptionalParam = -1;
        int indexOfRequiredParam = -1;

        for (int i = 0; i < params.size(); i++)
        {

            if (monitor.isCanceled())
            {
                return;
            }

            if (params.get(i).getDefaultValue() == null)
            {
                indexOfRequiredParam = i;
            }

            if (params.get(i).getDefaultValue() != null && indexOfOptionalParam == -1)
            {
                indexOfOptionalParam = i;
            }

            if (indexOfOptionalParam != -1 && indexOfRequiredParam != -1 && indexOfOptionalParam < indexOfRequiredParam)
            {
                resultAcceptor.addIssue(
                    Messages.OptionalParameterBeforeRequiredCheck_Optional_parameter_before_required,
                    params.get(indexOfOptionalParam), NAMED_ELEMENT__NAME);
                break;
            }

        }

    }

}
