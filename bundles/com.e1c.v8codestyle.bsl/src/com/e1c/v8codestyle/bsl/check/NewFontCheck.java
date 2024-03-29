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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FUNCTION_STYLE_CREATOR;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.OPERATOR_STYLE_CREATOR;
import static com._1c.g5.v8.dt.platform.IEObjectTypeNames.FONT;
import static com._1c.g5.v8.dt.platform.IEObjectTypeNames.FONT_RU;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FunctionStyleCreator;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks use constructor New Font.
 *
 * @author Artem Iliukhin
 */
public final class NewFontCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "new-font"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.NewFontCheck_Title)
            .description(Messages.NewFontCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(667, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(OPERATOR_STYLE_CREATOR)
            .checkedObjectType(FUNCTION_STYLE_CREATOR);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof OperatorStyleCreator && !((OperatorStyleCreator)object).getParams().isEmpty())
        {
            Type type = ((OperatorStyleCreator)object).getType();
            String name = McoreUtil.getTypeName(type);
            check(object, resultAceptor, name);
        }
        else if (object instanceof FunctionStyleCreator && ((FunctionStyleCreator)object).getParamsExpression() != null)
        {
            Expression typeNameExpression = ((FunctionStyleCreator)object).getTypeNameExpression();
            if (typeNameExpression instanceof Invocation)
            {
                List<Expression> params = ((Invocation)typeNameExpression).getParams();
                if (!params.isEmpty() && params.get(0) instanceof StringLiteral
                    && ((StringLiteral)params.get(0)).lines(true).size() == 1)
                {
                    String name = ((StringLiteral)params.get(0)).lines(true).get(0);
                    check(object, resultAceptor, name);
                }
            }
            // TODO: After implementing the issue #G5V8DT-22450 of supporting dfa for the Type,
            // support scenarios when a variable or method is passed as a parameter that returns Type Font #1128
        }
    }

    private void check(Object object, ResultAcceptor resultAceptor, String name)
    {
        if (FONT.equalsIgnoreCase(name) || FONT_RU.equalsIgnoreCase(name))
        {
            resultAceptor.addIssue(Messages.NewFontCheck_Issue, object);
        }
    }
}
