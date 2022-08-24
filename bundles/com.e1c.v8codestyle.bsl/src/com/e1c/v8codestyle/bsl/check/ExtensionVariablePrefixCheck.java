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
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.EXPLICIT_VARIABLE;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.IMPLICIT_VARIABLE;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.ImplicitVariable;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * The variable in the module of the extension object does not have a prefix corresponding
 * to the prefix of the extension itself
 *
 * @author Artem Iliukhin
 */
public class ExtensionVariablePrefixCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "extension-variable-prefix-check"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ExtensionVariablePrefixCheck(IV8ProjectManager v8ProjectManager)
    {
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ExtensionVariablePrefixCheck_Title)
            .description(Messages.ExtensionVariablePrefixCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(new OnlyAdoptedInExtensionObjectExtension())
            .module()
            .checkedObjectType(IMPLICIT_VARIABLE, EXPLICIT_VARIABLE, SIMPLE_STATEMENT, DECLARE_STATEMENT);
    }


    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        if (object instanceof ImplicitVariable || object instanceof ExplicitVariable)
        {
            checkVariable((Variable)object, resultAceptor, monitor);
        }
        else if (object instanceof SimpleStatement && ((SimpleStatement)object).getLeft() instanceof StaticFeatureAccess
            && ((StaticFeatureAccess)((SimpleStatement)object).getLeft()).getImplicitVariable() != null)
        {
            Variable variable = ((StaticFeatureAccess)((SimpleStatement)object).getLeft()).getImplicitVariable();
            checkVariable(variable, resultAceptor, monitor);
        }
        else if (object instanceof DeclareStatement)
        {
            DeclareStatement declare = (DeclareStatement)object;
            for (Variable variable : declare.getVariables())
            {
                if (monitor.isCanceled())
                {
                    return;
                }
                checkVariable(variable, resultAceptor, monitor);
            }
        }
    }

    private void checkVariable(Variable variable, ResultAcceptor resultAceptor, IProgressMonitor monitor)
    {
        IV8Project extension = v8ProjectManager.getProject(variable);

        if (extension instanceof IExtensionProject && ((IExtensionProject)extension).getParent() != null)
        {
            String prefix = getNamePrefix((IExtensionProject)extension);
            String variableName = variable.getName();

            if (monitor.isCanceled())
            {
                return;
            }

            if (!StringUtils.isEmpty(prefix) && !variableName.startsWith(prefix))
            {
                resultAceptor.addIssue(
                    MessageFormat.format(Messages.ExtensionVariablePrefixCheck_Variable_0_should_have_1_prefix,
                        variableName, prefix),
                    variable, McorePackage.Literals.NAMED_ELEMENT__NAME);
            }
        }
    }

    private String getNamePrefix(IExtensionProject extension)
    {
        return extension.getConfiguration().getNamePrefix();
    }
}
