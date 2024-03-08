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
package com.e1c.v8codestyle.md.commonmodule.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__GLOBAL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.e1c.v8codestyle.md.check.SkipAdoptedInExtensionMdObjectExtension;
import com.google.inject.Inject;

/**
 * Checks client global common module name has not "Client" suffix
 *
 * @author Artem Iliukhin
 */
public class CommonModuleNameGlobalClientCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "common-module-name-global-client"; //$NON-NLS-1$
    private static final String NAME_SUFFIX_RU = "Клиент"; //$NON-NLS-1$
    private static final String NAME_SUFFIX_EN = "Client"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    /**
     * Instantiates a new common module name global client check.
     *
     * @param v8ProjectManager
     */
    @Inject
    public CommonModuleNameGlobalClientCheck(IV8ProjectManager v8ProjectManager)
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
        builder.title(Messages.CommonModuleNameGlobalClientCheck_Title)
            .description(Messages.CommonModuleNameGlobalClientCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.CRITICAL)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(469, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(SkipAdoptedInExtensionMdObjectExtension.instance())
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(MD_OBJECT__NAME, COMMON_MODULE__GLOBAL);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        CommonModule commonModule = (CommonModule)object;
        if (!commonModule.isGlobal())
        {
            return;
        }

        IV8Project project = v8ProjectManager.getProject(commonModule);
        ScriptVariant variant = project == null ? ScriptVariant.ENGLISH : project.getScriptVariant();

        String name = commonModule.getName();
        String suffix = ScriptVariant.ENGLISH == variant ? NAME_SUFFIX_EN : NAME_SUFFIX_RU;
        if (name.contains(suffix))
        {
            String message = MessageFormat.format(Messages.CommonModuleNameGlobalClientCheck_Message, suffix);
            resultAceptor.addIssue(message, MD_OBJECT__NAME);
        }
    }
}
