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
 *     Denis Maslennikov  - issue #1090
 *******************************************************************************/
package com.e1c.v8codestyle.ql.check;

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.ABINARY_OPERATORS_EXPRESSION__RIGHT;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.ql.model.ABinaryOperatorsExpression;
import com._1c.g5.v8.dt.ql.model.StringLiteralExpression;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.ql.CorePlugin;

/**
 * This class checks using two constants in binary operations in queries.
 *
 * @author Denis Maslennikov
 */
public class ConstantsInBinaryOperationCheck
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-constants-in-binary-operation"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ConstantsInBinaryOperationCheck_title)
            .description(Messages.ConstantsInBinaryOperationCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.PORTABILITY)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .delegate(ABinaryOperatorsExpression.class);
    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor resultAceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {
        ABinaryOperatorsExpression binaryExpression = (ABinaryOperatorsExpression)object;
        if (monitor.isCanceled())
        {
            return;
        }

        if ((binaryExpression.getLeft() instanceof StringLiteralExpression)
            && (binaryExpression.getRight() instanceof StringLiteralExpression))
        {
            String message =
                Messages.ConstantsInBinaryOperationCheck_Using_binary_operations_with_constants_in_queries_is_forbidden;
            resultAceptor.addIssue(message, binaryExpression, ABINARY_OPERATORS_EXPRESSION__RIGHT);
        }
    }
}
