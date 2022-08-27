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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DECLARE_STATEMENT;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STATIC_FEATURE_ACCESS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.VARIABLE;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Checks if variable (parameter in method, declared or initialized variable)
 * has a name with length less than or equal to user`s parameter value choice (1 by default).
 *
 * @author Vitaly Prolomov
 *
 */
public class SingleLetterVariableNameCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "single-letter-variable-name"; //$NON-NLS-1$

    private static final String PARAM_CHECKED_LENGTH = "1"; //$NON-NLS-1$

    public SingleLetterVariableNameCheck()
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
        builder.title(Messages.SingleLetterVariableNameCheck_title)
            .description(Messages.SingleLetterVariableNameCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .module()
            .checkedObjectType(VARIABLE, DECLARE_STATEMENT, STATIC_FEATURE_ACCESS)
            .parameter(PARAM_CHECKED_LENGTH, Integer.class, "1", //$NON-NLS-1$
                Messages.SingleLetterVariableNameCheck_checked_length);

    }

    /**
     * Checks 3 different cases: parameters in methods, declared and initialized
     * variables for having length of the name less than or equal to parameter,
     * indicating length, selected by user (1 by default)
     */
    @Override
    protected void check(Object object, ResultAcceptor acceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {

        String message = Messages.SingleLetterVariableNameCheck_message;

        if (monitor.isCanceled())
        {
            return;
        }

        int checkedLength = parameters.getInt(PARAM_CHECKED_LENGTH);

        if (object instanceof Variable && ((Variable)object).getName().length() <= checkedLength)
        {
            acceptor.addIssue(message, object);
        }
        else if (object instanceof StaticFeatureAccess)
        {
            Variable variable = ((StaticFeatureAccess)object).getImplicitVariable();

            if (variable == null)
            {
                return;
            }
            // Counters inside loops could have short names (according to check requirements)
            var varTypeClass = variable.eContainer().eContainer();
            if (varTypeClass instanceof ForStatement)
            {
                return;
            }
            if (variable.getName().length() <= checkedLength)
            {
                acceptor.addIssue(message, object);
            }
        }
        else if (object instanceof DeclareStatement)
        {
            DeclareStatement ds = (DeclareStatement)object;
            List<ExplicitVariable> evList = ds.getVariables();

            for (ExplicitVariable expVar : evList)
            {
                if (expVar != null && expVar.getName().length() <= checkedLength)
                {
                    acceptor.addIssue(message, object);
                }
            }

        }
    }

}
