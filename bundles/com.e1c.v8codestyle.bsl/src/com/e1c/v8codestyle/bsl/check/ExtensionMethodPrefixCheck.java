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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Method;
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
 * The procedure (function) in the module of the extension object does not have a prefix corresponding
 * to the prefix of the extension itself.
 *
 * @author Artem Iliukhin
 */
public class ExtensionMethodPrefixCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "extension-method-prefix-check"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    /**
     * Instantiates a new extension method prefix check.
     *
     * @param v8ProjectManager the v 8 project manager
     */
    @Inject
    public ExtensionMethodPrefixCheck(IV8ProjectManager v8ProjectManager)
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
        builder.title(Messages.ExtensionMethodPrefixCheck_Title)
            .description(Messages.ExtensionMethodPrefixCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(new AdoptedModuleOwnerExtension())
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;
        IV8Project extension = v8ProjectManager.getProject(method);
        if (extension instanceof IExtensionProject && ((IExtensionProject)extension).getParent() != null)
        {
            String prefix = getNamePrefix((IExtensionProject)extension);
            String methodName = method.getName();

            if (monitor.isCanceled())
            {
                return;
            }

            if (!StringUtils.isEmpty(prefix) && !methodName.startsWith(prefix))
            {
                resultAceptor.addIssue(MessageFormat
                    .format(Messages.ExtensionMethodPrefixCheck_Ext_method_0_should_have_1_prefix, methodName, prefix),
                    McorePackage.Literals.NAMED_ELEMENT__NAME);
            }
        }
    }

    private String getNamePrefix(IExtensionProject extension)
    {
        return extension.getConfiguration().getNamePrefix();
    }
}
