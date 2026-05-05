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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.NumberLiteral;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks that variable is self assign.
 *
 *  @author Ivan Sergeev
 */
public class SecuritySoftwareCallCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "security-software-call"; //$NON-NLS-1$
    private static final String COM_APPLICATION = "application"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.SecuritySoftwareCall_Title)
            .description(Messages.SecuritySoftwareCall_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.SECURITY)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(SIMPLE_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        SimpleStatement statement = (SimpleStatement)object;
        if (statement.getRight() instanceof OperatorStyleCreator right)
        {
            if (McoreUtil.getTypeName(right.getType()).equalsIgnoreCase("comobject")) //$NON-NLS-1$
            {
                if (!right.getParams().isEmpty())
                {
                    String type = getStringContent(right.getParams().get(0));
                    if (type == null)
                    {
                        return;
                    }
                    if (type.toLowerCase().contains(COM_APPLICATION))
                    {
                        Method method = EcoreUtil2.getContainerOfType(statement, Method.class);
                        if (statement.getLeft() instanceof StaticFeatureAccess sfa)
                        {
                            String nameStatement = sfa.getName();
                            boolean macrosDisable = checkSting(method, nameStatement);
                            if (!macrosDisable)
                            {
                                resultAceptor.addIssue(Messages.SecuritySoftwareCall_Issue);
                            }
                        }
                    }
                }
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
        return null;
    }

    private boolean checkSting(Method method, String nameCall)
    {
        List<Statement> statements = method.allStatements();
        for (Statement statement : statements)
        {
            if (statement instanceof SimpleStatement simpleStatement)
            {
                if (simpleStatement.getRight() instanceof NumberLiteral right)
                {
                    String textInv = NodeModelUtils.findActualNodeFor(simpleStatement).getText();
                    if (textInv.toLowerCase().contains(nameCall.toLowerCase())
                        && textInv.toLowerCase().contains("automationsecurity")) //$NON-NLS-1$
                    {
                        List<String> values = right.getValue();
                        for (String value : values)
                        {
                            if ("3".equals(value)) //$NON-NLS-1$
                            {
                                return true;
                            }
                        }
                    }
                }
                else if (simpleStatement.getLeft() instanceof Invocation left)
                {
                    String textInv = NodeModelUtils.findActualNodeFor(left).getText();
                    if (textInv.toLowerCase().contains(nameCall.toLowerCase())
                        && textInv.toLowerCase().contains("disableautomacros")) //$NON-NLS-1$
                    {
                        if (left.getParams().size() == 1 && left.getParams().get(0) instanceof NumberLiteral number)
                        {
                            List<String> values = number.getValue();
                            for (String value : values)
                            {
                                if ("1".equals(value)) //$NON-NLS-1$
                                {
                                    return true;
                                }
                            }
                        }
                        else if (left.getParams().get(0) instanceof StaticFeatureAccess sfa)
                        {
                            for (Statement statParam : statements)
                            {
                                if (statParam instanceof SimpleStatement simpStatement)
                                {
                                    if (simpStatement.getLeft() instanceof StaticFeatureAccess leftParam
                                        && simpStatement.getRight() instanceof NumberLiteral numberParam)
                                    {
                                        if (leftParam.getName().equalsIgnoreCase(sfa.getName()))
                                        {
                                            List<String> values = numberParam.getValue();
                                            for (String value : values)
                                            {
                                                if ("1".equals(value)) //$NON-NLS-1$
                                                {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
