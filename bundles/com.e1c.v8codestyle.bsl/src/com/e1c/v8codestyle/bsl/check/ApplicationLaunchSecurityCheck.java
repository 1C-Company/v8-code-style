/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.common.StringUtils;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks application launch security
 *
 *  @author Ivan Sergeev
 */
public class ApplicationLaunchSecurityCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "application-launch-security"; //$NON-NLS-1$

    private static final Set<String> IMMUTABLE_MAP_CALL =
        Set.of("командасистемы", "system", "запуститьприложение", "runapp", "начатьзапускприложения", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "beginrunningapplication", "перейтипонавигационнойссылке", "gotourl", "comобъект", "comobject"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    private static final Set<String> CHECK_SYMBOLS = Set.of("$", "`", "|", "||", ";", "&", "&&"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    private static final Set<String> COM_OBJECT = Set.of("wscript.shell", "shell.application"); //$NON-NLS-1$ //$NON-NLS-2$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ApplicationLaunchSecurityCheck_Title)
            .description(Messages.ApplicationLaunchSecurityCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.SECURITY)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(SIMPLE_STATEMENT)
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        EList<Expression> params = null;
        if (object instanceof SimpleStatement simpStatement)
        {
            if (simpStatement.getRight() instanceof OperatorStyleCreator right)
            {
                String nameObj = NodeModelUtils.findActualNodeFor(right).getText();
                for (String callName : IMMUTABLE_MAP_CALL)
                {
                    if (nameObj.toLowerCase().contains(callName))
                    {
                        if (!right.getParams().isEmpty())
                        {
                            String type = getStringContent(right.getParams().get(0));
                            if (COM_OBJECT.contains(type.toLowerCase()))
                            {
                                Method method = EcoreUtil2.getContainerOfType(simpStatement, Method.class);
                                if (simpStatement.getLeft() instanceof StaticFeatureAccess sfa)
                                {
                                    String nameStatement = sfa.getName();
                                    params = checkSting(method, nameStatement);
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (object instanceof Invocation invocation)
        {
            FeatureAccess featureAccess = invocation.getMethodAccess();
            String name = featureAccess.getName();
            if (IMMUTABLE_MAP_CALL.contains(name.toLowerCase()))
            {
                params = invocation.getParams();
            }
        }
        if (params == null)
        {
            return;
        }
        for (Expression parameter : params)
        {
            String content = getStringContent(parameter);

            if (containSymbol(content))
            {
                resultAceptor.addIssue(Messages.ApplicationLaunchSecurityCheck_Issue, parameter);
            }
        }
    }

    private String getStringContent(Expression parameter)
    {
        if (parameter instanceof StringLiteral literal)
        {
            return String.join(StringUtils.EMPTY, literal.lines(true));
        }
        else if (parameter instanceof StaticFeatureAccess staticFeatureAccess)
        {
            return staticFeatureAccess.getName();
        }
        else if (parameter instanceof Invocation invocation)
        {
            String text = NodeModelUtils.findActualNodeFor(invocation).getText();
            return text;
        }
        return null;
    }

    private boolean containSymbol(String content)
    {
        if (content == null)
        {
            return false;
        }
        for (String symbol : CHECK_SYMBOLS)
        {
            if (content.contains(symbol))
            {
                return true;
            }
        }
        return false;
    }

    private EList<Expression> checkSting(Method method, String nameCall)
    {
        List<Statement> statements = method.allStatements();
        for (Statement statement : statements)
        {
            if (statement instanceof SimpleStatement simpleStatement)
            {
                if (simpleStatement.getRight() instanceof Invocation right)
                {
                    String textInv = NodeModelUtils.findActualNodeFor(right).getText();
                    if (textInv.toLowerCase().contains(nameCall.toLowerCase()))
                    {
                        return right.getParams();
                    }
                }
                else if (simpleStatement.getLeft() instanceof Invocation left)
                {
                    String textInv = NodeModelUtils.findActualNodeFor(left).getText();
                    if (textInv.toLowerCase().contains(nameCall.toLowerCase()))
                    {
                        return left.getParams();
                    }
                }
            }
        }
        return null;
    }
}
