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
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.McorePackage;
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
 * Checks the standard interface regions for the existence of non-export methods
 * and the location of export methods outside the regions provided by the standard for export methods.
 *
 * @author Artem Iliukhin
 *
 */
public class ModuleStructureInterfaceRegionsCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "module-structure-interface-regions"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ModuleStructureInterfaceRegionsCheck(IV8ProjectManager v8ProjectManager)
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
        builder.title(Messages.ModuleStructureInterfaceRegionsCheck_Title)
            .description(Messages.ModuleStructureInterfaceRegionsCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;

        IV8Project project = v8ProjectManager.getProject(method);
        ScriptVariant scriptVariant = project.getScriptVariant();

        Optional<RegionPreprocessor> region = getUpperRegion(method);
        if (region.isEmpty())
        {
            return;
        }

        String publicnName = ModuleStructureSection.PUBLIC.getName(scriptVariant);
        String internalName = ModuleStructureSection.INTERNAL.getName(scriptVariant);
        String privateName = ModuleStructureSection.PRIVATE.getName(scriptVariant);
        String regionName = region.get().getName();
        if ((publicnName.equals(regionName) || internalName.equals(regionName)) && !method.isExport())
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleStructureInterfaceRegionsCheck_Only_export_methods__0, regionName),
                McorePackage.Literals.NAMED_ELEMENT__NAME);
            return;
        }

        if (method.isExport()
            && !(publicnName.equals(regionName) || internalName.equals(regionName) || privateName.equals(regionName)))
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleStructureInterfaceRegionsCheck_Export_method__0__in_regions,
                    method.getName()),
                McorePackage.Literals.NAMED_ELEMENT__NAME);
        }
    }
}
