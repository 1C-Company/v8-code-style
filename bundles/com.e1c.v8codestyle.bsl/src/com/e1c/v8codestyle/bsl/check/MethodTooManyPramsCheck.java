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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

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
 * Checks method parameters that should not be more the 7 parameters in total, or not more then 3 of parameters
 * with default values.
 *
 * @author Dmitriy Marmyshev
 */
public class MethodTooManyPramsCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "method-too-many-params"; //$NON-NLS-1$

    private static final String PARAM_MAX_PARAMS = "maxParams"; //$NON-NLS-1$

    private static final String DEFAULT_MAX_PARAMS = "7"; //$NON-NLS-1$

    private static final String PARAM_MAX_DEFAULT_VALUE_PARAMS = "maxDefaultValueParams"; //$NON-NLS-1$

    private static final String DEFAULT_MAX_DEFAULT_VALUE_PARAMS = "3"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MethodTooManyPramsCheck_title)
            .description(Messages.MethodTooManyPramsCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD)
            .parameter(PARAM_MAX_PARAMS, Integer.class, DEFAULT_MAX_PARAMS,
                Messages.MethodTooManyPramsCheck_Max_parameters)
            .parameter(PARAM_MAX_DEFAULT_VALUE_PARAMS, Integer.class, DEFAULT_MAX_DEFAULT_VALUE_PARAMS,
                Messages.MethodTooManyPramsCheck_Max_parameters_with_default_value);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        int maxParams = parameters.getInt(PARAM_MAX_PARAMS);
        int maxDefaultValueParams = parameters.getInt(PARAM_MAX_DEFAULT_VALUE_PARAMS);

        if (maxDefaultValueParams < 1 && maxParams < 1)
        {
            return;
        }

        Method method = (Method)object;

        List<FormalParam> params = method.getFormalParams();
        int defValueParams = 0;

        int start = 0;
        if (maxDefaultValueParams < 1)
        {
            start = maxParams;
        }

        for (int i = start; i < params.size(); i++)
        {
            FormalParam param = params.get(i);
            if (maxParams > 0 && i > maxParams)
            {
                String message = MessageFormat.format(Messages.MethodTooManyPramsCheck_Method_has_more_than__N__params,
                    String.valueOf(maxParams));
                resultAceptor.addIssue(message, param, NAMED_ELEMENT__NAME);
            }
            if (param.getDefaultValue() != null)
            {
                defValueParams++;
            }

            if (maxDefaultValueParams > 0 && defValueParams > maxDefaultValueParams)
            {
                String message = MessageFormat.format(
                    Messages.MethodTooManyPramsCheck_Method_has_more_than__N__params_with_default_value,
                    String.valueOf(maxDefaultValueParams));
                resultAceptor.addIssue(message, param, NAMED_ELEMENT__NAME);
            }
        }
    }

}
