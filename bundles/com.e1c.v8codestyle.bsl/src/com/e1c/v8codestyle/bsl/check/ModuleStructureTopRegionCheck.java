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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.REGION_PREPROCESSOR;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks that region named as module structure name is on top level of module.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class ModuleStructureTopRegionCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "module-structure-top-region"; //$NON-NLS-1$

    private final IModuleStructureProvider moduleStructureProvider;

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ModuleStructureTopRegionCheck(IModuleStructureProvider moduleStructureProvider,
        IV8ProjectManager v8ProjectManager)
    {
        super();
        this.moduleStructureProvider = moduleStructureProvider;
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
        builder.title(Messages.ModuleStructureTopRegionCheck_title)
            .description(Messages.ModuleStructureTopRegionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(REGION_PREPROCESSOR);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        RegionPreprocessor region = (RegionPreprocessor)object;
        if (region.getName() == null)
        {
            return;
        }

        IV8Project project = v8ProjectManager.getProject(region);

        ModuleType moduleType = getModuleType(region);
        Collection<String> names =
            moduleStructureProvider.getModuleStructureRegions(moduleType, project.getScriptVariant());
        if (!names.contains(region.getName()))
        {
            return;
        }

        Optional<RegionPreprocessor> parent = getFirstParentRegion(region);
        if (parent.isPresent())
        {
            resultAceptor.addIssue(Messages.ModuleStructureTopRegionCheck_error_message, NAMED_ELEMENT__NAME);
        }
    }

}
