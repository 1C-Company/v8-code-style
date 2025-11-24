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
 *     Vadim Goncharov - issue #385
 *******************************************************************************/

package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.VARIABLE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check that variable name is correct.
 * @author Vadim Goncharov
 */
public class VariableNameInvalidCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "bsl-variable-name-invalid"; //$NON-NLS-1$

    private static final String MIN_NAME_LENGTH_PARAM_NAME = "minNameLength"; //$NON-NLS-1$

    private static final Integer MIN_NAME_LENGTH_DEFAULT = 3;

    private static final String UNDERLINE_SYM = "_"; //$NON-NLS-1$

    /**
     * Instantiates a new instance of filter by variable name.
     */
    public VariableNameInvalidCheck()
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
        builder.title(Messages.VariableNameInvalidCheck_title)
            .description(Messages.VariableNameInvalidCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(454, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(VARIABLE)
            .parameter(MIN_NAME_LENGTH_PARAM_NAME, Integer.class, MIN_NAME_LENGTH_DEFAULT.toString(),
                Messages.VariableNameInvalidCheck_param_MIN_NAME_LENGTH_PARAM_title);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Variable variable = (Variable)object;
        //if name of variable is null therefore syntax error
        if (variable.getName() == null)
        {
            return;
        }

        int minLength = parameters.getInt(MIN_NAME_LENGTH_PARAM_NAME);
        if (minLength <= 0)
        {
            minLength = MIN_NAME_LENGTH_DEFAULT;
        }

        if (variable.eContainer() instanceof StaticFeatureAccess && isForStatementAccessVariable(variable))
        {
            return;
        }
        checkVariable(variable, minLength, resultAceptor);

    }

    private void checkVariable(Variable variable, int minLength, ResultAcceptor resultAceptor)
    {

        String name = variable.getName();
        int nameLength = name.length();
        String msgTemplate = Messages.VariableNameInvalidCheck_variable_name_is_invalid;
        String msg = null;

        if (nameLength < minLength)
        {
            msg = MessageFormat.format(msgTemplate, name, MessageFormat
                .format(Messages.VariableNameInvalidCheck_message_variable_length_is_less_than, minLength));
            resultAceptor.addIssue(msg, variable, NAMED_ELEMENT__NAME);
        }

        if (name.startsWith(UNDERLINE_SYM))
        {
            msg = MessageFormat.format(msgTemplate, name,
                Messages.VariableNameInvalidCheck_variable_name_starts_with_an_underline);
            resultAceptor.addIssue(msg, variable, NAMED_ELEMENT__NAME);
        }
        else if (!Character.isUpperCase(name.charAt(0)))
        {
            msg = MessageFormat.format(msgTemplate, name,
                Messages.VariableNameInvalidCheck_variable_name_must_start_with_a_capital_letter);
            resultAceptor.addIssue(msg, variable, NAMED_ELEMENT__NAME);
        }

    }

    private boolean isForStatementAccessVariable(Variable variable)
    {
        return EcoreUtil2.getContainerOfType(variable, ForStatement.class) != null;
    }

}
