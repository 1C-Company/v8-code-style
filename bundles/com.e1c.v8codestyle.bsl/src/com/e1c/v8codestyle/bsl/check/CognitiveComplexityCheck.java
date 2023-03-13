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
 *     Manaev Konstantin - issue #1117
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Method;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.CognitiveComplexityProcessor;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks the method that a cognitive complexity is less a threshold.
 *
 * @author Manaev Konstantin
 */
public final class CognitiveComplexityCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "cognitive-complexity"; //$NON-NLS-1$
    private static final String PARAM_COMPLEXTITY_THRESHOLD = "complexityThreshold"; //$NON-NLS-1$
    private static final String DEFAULT_COMPLEXITY_THRESHOLD = "15"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CognitiveComplexityCheck_title)
            .description(MessageFormat.format(Messages.CognitiveComplexityCheck_description, DEFAULT_COMPLEXITY_THRESHOLD))
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD)
            .parameter(PARAM_COMPLEXTITY_THRESHOLD, Integer.class, DEFAULT_COMPLEXITY_THRESHOLD,
                Messages.CognitiveComplexityCheck_param_threshold_name);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        Method method = (Method)object;
        if (method != null)
        {
            CognitiveComplexityProcessor processor = new CognitiveComplexityProcessor();
            int complexityValue = processor.compute(method, monitor);

            int complexityThreshold = parameters.getInt(PARAM_COMPLEXTITY_THRESHOLD);
            if (complexityValue > complexityThreshold)
            {
                resultAceptor.addIssue(MessageFormat.format(Messages.CognitiveComplexityCheck_issue_message,
                    Integer.toString(complexityValue), Integer.toString(complexityThreshold)), NAMED_ELEMENT__NAME);
            }
        }

    }

}
