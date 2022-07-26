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
 *     Denis Maslennikov  - issue #86
 *******************************************************************************/
package com.e1c.v8codestyle.ql.check;

import static com._1c.g5.v8.dt.ql.model.QlPackage.Literals.LIKE_EXPRESSION__LITERAL;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.ql.model.AbstractExpression;
import com._1c.g5.v8.dt.ql.model.CommonExpression;
import com._1c.g5.v8.dt.ql.model.LikeExpression;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.ql.CorePlugin;

/**
 * This class checks if right operand of the LIKE comparison operation is a table field (a catalog attribute).
 *
 * @author Denis Maslennikov
 */
public class IncorrectLikeRightOperandCheck
    extends QlBasicDelegateCheck
{

    private static final String CHECK_ID = "ql-incorrect-like-right-operand"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.IncorrectLikeRightOperandCheck_title)
            .description(Messages.IncorrectLikeRightOperandCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.PERFORMANCE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .delegate(LikeExpression.class);
    }

    @Override
    protected void checkQlObject(EObject object, QueryOwner owner, IQlResultAcceptor resultAceptor,
        ICheckParameters parameters, IProgressMonitor monitor)
    {

        LikeExpression likeExpression = (LikeExpression)object;
        AbstractExpression literal = likeExpression.getLiteral();
        List<CommonExpression> commonExpressions = EcoreUtil2.getAllContentsOfType(literal, CommonExpression.class);

        if (monitor.isCanceled() || commonExpressions.isEmpty())
        {
            return;
        }

        String message =
            Messages.IncorrectLikeRightOperandCheck_The_right_operand_of_the_LIKE_comparison_operation_is_query_field;
        resultAceptor.addIssue(message, likeExpression, LIKE_EXPRESSION__LITERAL);
    }
}
