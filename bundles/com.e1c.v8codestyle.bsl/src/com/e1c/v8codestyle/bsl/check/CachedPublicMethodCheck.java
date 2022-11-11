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

import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks cached public method
 *
 * @author Artem Iliukhin
 */
public class CachedPublicMethodCheck
    extends AbstractModuleStructureCheck
{

    private static final String CHECK_ID = "public-method-caching"; //$NON-NLS-1$
    private final IV8ProjectManager v8ProjectManager;

    /**
     * Creates new instance which helps to check cached public method
     * 
     * @param v8ProjectManager
     */
    @Inject
    public CachedPublicMethodCheck(IV8ProjectManager v8ProjectManager)
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
        builder.title(Messages.CachedPublicCheck_Title)
            .description(Messages.CachedPublicCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(644, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.COMMON_MODULE))
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor result, ICheckParameters parameters, IProgressMonitor monitor)
    {
        Method method = (Method)object;
        if (!method.isExport())
        {
            return;
        }

        Optional<RegionPreprocessor> region = getTopParentRegion(method);
        IV8Project project = v8ProjectManager.getProject(method);
        ScriptVariant scriptVariant = project.getScriptVariant();
        if (region.isEmpty() || !ModuleStructureSection.PUBLIC.getName(scriptVariant).equals(region.get().getName()))
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(region.get(), Module.class);
        if (module == null)
        {
            return;
        }

        CommonModule commonModule = (CommonModule)module.getOwner();
        if (commonModule.getReturnValuesReuse() == ReturnValuesReuse.DONT_USE)
        {
            return;
        }
        result.addIssue(Messages.CachedPublicCheck_Issue, method, BslPackage.Literals.METHOD__EXPORT);
    }
}
