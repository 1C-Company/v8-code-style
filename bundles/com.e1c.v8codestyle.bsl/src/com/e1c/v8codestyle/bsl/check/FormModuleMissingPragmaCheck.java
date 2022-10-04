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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DECLARE_STATEMENT;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks that form or command module each method or module declared variables has compilation directives (pragmas).
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 */
public class FormModuleMissingPragmaCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "form-module-missing-pragma"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormModuleMissingPragmaCheck_title)
            .description(Messages.FormModuleMissingPragmaCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.ERROR)
            .extension(new StandardCheckExtension(467, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE, ModuleType.COMMAND_MODULE))
            .module()
            .checkedObjectType(METHOD, DECLARE_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof Method)
        {
            Method method = (Method)object;
            if (method.getPragmas().isEmpty())
            {
                resultAceptor.addIssue(Messages.FormModuleMissingPragmaCheck_Missing_compilation_directives, method,
                    NAMED_ELEMENT__NAME);
            }
        }
        else if (object instanceof DeclareStatement)
        {
            DeclareStatement declare = (DeclareStatement)object;
            Method method = EcoreUtil2.getContainerOfType((EObject)declare, Method.class);
            if (method == null && declare.getPragmas().isEmpty())
            {
                for (ExplicitVariable variable : declare.getVariables())
                {
                    resultAceptor.addIssue(Messages.FormModuleMissingPragmaCheck_Missing_compilation_directives,
                        variable, NAMED_ELEMENT__NAME);
                }
            }
        }
    }

}
