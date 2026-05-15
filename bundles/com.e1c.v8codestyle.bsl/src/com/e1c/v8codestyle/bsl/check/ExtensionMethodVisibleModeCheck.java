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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.common.IModuleExtensionService;
import com._1c.g5.v8.dt.bsl.common.IModuleExtensionServiceProvider;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check method in extention visible mode.
 *
 *  @author Ivan Sergeev
 */
public class ExtensionMethodVisibleModeCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "extension-method-visible-mode"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ExtensionMethodVisibleModeCheck(IV8ProjectManager v8ProjectManager)
    {
        super();
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
        builder.title(Messages.ExtensionMethodVisibleModeCheck_Title)
            .description(Messages.ExtensionMethodVisibleModeCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;
        IV8Project extension = v8ProjectManager.getProject(module);
        if (extension instanceof IExtensionProject)
        {
            EList<Method> methodsList = module.allMethods();
            for (Method method : methodsList)
            {
                IModuleExtensionService service = IModuleExtensionServiceProvider.INSTANCE.getModuleExtensionService();
                Map<Pragma, Method> pragmaSourceMethod = service.getSourceMethod(method);
                if (!pragmaSourceMethod.isEmpty())
                {
                    Collection<Method> methods = pragmaSourceMethod.values();
                    Method sourceMethod = methods.iterator().next();
                    Environments extentionMethodEnv =
                        EcoreUtil2.getContainerOfType(method, Environmental.class).environments();
                    Environments sourceMethodEnv =
                        EcoreUtil2.getContainerOfType(sourceMethod, Environmental.class).environments();
                    if (!sourceMethodEnv.containsAll(extentionMethodEnv))
                    {
                        resultAceptor.addIssue(Messages.ExtensionMethodVisibleModeCheck_Issue, method,
                            NAMED_ELEMENT__NAME);
                    }
                }
            }
        }
    }
}
