/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks that variable is self assign.
 *
 *  @author Ivan Sergeev
 */
public class FindRowServerCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "find-rows-server"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FindRowServerCheck_Title)
            .description(Messages.FindRowServerCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(628, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof Invocation invocation)
        {
            String name = invocation.getMethodAccess().getName();
            if ("НайтиСтроки".equalsIgnoreCase(name) || "FindRows".equalsIgnoreCase(name)) //$NON-NLS-1$ //$NON-NLS-2$
            {
                Method method = EcoreUtil2.getContainerOfType(invocation, Method.class);
                Environments envi = method.getEnvironments();
                if (!envi.contains(Environment.SERVER))
                {
                    resultAceptor.addIssue(Messages.FindRowServerCheck_Issue);
                }
            }
        }
    }
}
