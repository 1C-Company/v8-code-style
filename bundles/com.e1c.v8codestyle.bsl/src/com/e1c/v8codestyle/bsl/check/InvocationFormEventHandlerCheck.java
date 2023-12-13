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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Procedure;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check program invocation of form event handler
 *
 * @author Artem Iliukhin
 */
public final class InvocationFormEventHandlerCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "invocation-form-event-handler"; //$NON-NLS-1$

    @Inject
    public InvocationFormEventHandlerCheck()
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
        builder.title(Messages.InvocationFormEventHandlerCheck_Program_invocation_of_form_event_handler_title)
            .description(Messages.InvocationFormEventHandlerCheck_Program_invocation_of_form_event_handler_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.TRIVIAL)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(455, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        FeatureAccess method = ((Invocation)object).getMethodAccess();
        if (!(method instanceof StaticFeatureAccess))
        {
            return;
        }

        List<FeatureEntry> featureEntries = ((StaticFeatureAccess)method).getFeatureEntries();
        if (featureEntries.isEmpty() || !(featureEntries.get(0).getFeature() instanceof Procedure))
        {
            return;
        }

        Procedure procedure = (Procedure)featureEntries.get(0).getFeature();
        if (procedure.isEvent())
        {
            resultAceptor.addIssue(
                Messages.InvocationFormEventHandlerCheck_Program_invocation_of_form_event_handler_result, object);
        }
    }
}
