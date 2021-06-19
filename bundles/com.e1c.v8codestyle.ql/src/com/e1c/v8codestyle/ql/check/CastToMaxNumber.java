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
package com.e1c.v8codestyle.ql.check;

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.CASTING_NUMBER_TYPE__PRECISION;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.ql.model.CastingNumberType;
import com._1c.g5.v8.dt.ql.model.NumberLiteralExpression;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;

public class CastToMaxNumber
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-cast-to-max-number"; //$NON-NLS-1$

    private static final String MAX_NUMBER_LENGTH = "maxNumberLength"; //$NON-NLS-1$

    private static final String MAX_NUMBER_LENGTH_DEFAULT = "31"; //$NON-NLS-1$

    private static final String MAX_NUMBER_PRECISION = "maxNumberPrecision"; //$NON-NLS-1$

    private static final String MAX_NUMBER_PRECISION_DEFAULT = "-1"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CastToMaxNumber_title)
            .description(Messages.CastToMaxNumber_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .delegate(CastingNumberType.class);
        builder
            .parameter(MAX_NUMBER_LENGTH, Integer.class, MAX_NUMBER_LENGTH_DEFAULT,
                Messages.CastToMaxNumber_Maximum_cast_number_length)
            .parameter(MAX_NUMBER_PRECISION, Integer.class, MAX_NUMBER_PRECISION_DEFAULT,
                Messages.CastToMaxNumber_Maximum_cast_number_precision_or_N_to_skip_check);
    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor resultAceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {
        CastingNumberType casting = (CastingNumberType)object;

        int length = parameters.getInt(MAX_NUMBER_LENGTH);
        int precision = parameters.getInt(MAX_NUMBER_PRECISION);

        if (length > 0 && casting.getLength() instanceof NumberLiteralExpression)
        {
            NumberLiteralExpression lengthExpression = (NumberLiteralExpression)casting.getLength();
            int current = Integer.parseInt(lengthExpression.getIntPart());
            if (current > length)
            {
                String message = MessageFormat.format(
                    Messages.CastToMaxNumber_Query_cast_to_number_with_lenth__0__and_max_allowed__1, current, length);
                resultAceptor.addIssue(message, object, CASTING_NUMBER_TYPE__PRECISION);
            }
        }

        if (precision > -1 && casting.getPrecision() instanceof NumberLiteralExpression)
        {
            NumberLiteralExpression precisionExpression = (NumberLiteralExpression)casting.getPrecision();
            int current = Integer.parseInt(precisionExpression.getIntPart());
            if (current > precision)
            {
                String message = MessageFormat.format(
                    Messages.CastToMaxNumber_Query_cast_to_number_with_precision__0__and_max_allowed__1, current,
                    precision);
                resultAceptor.addIssue(message, object, CASTING_NUMBER_TYPE__PRECISION);
            }
        }
    }

}
