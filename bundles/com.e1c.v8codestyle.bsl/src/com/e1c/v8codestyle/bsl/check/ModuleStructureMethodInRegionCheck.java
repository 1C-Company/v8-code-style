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
import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
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
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks that the method is outside the regions.
 * Checks the standard interface regions for the existence of non-export methods
 * and the location of export methods outside the regions provided by the standard for export methods.
 *
 * @author Artem Iliukhin
 *
 */
public class ModuleStructureMethodInRegionCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "module-structure-method-in-regions"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_NESTING_OF_REGIONS = Boolean.toString(Boolean.TRUE);

    private static final String MULTILEVEL_NESTING_OF_REGIONS = "multilevelNestingOfRegions"; //$NON-NLS-1$

    private final IModuleStructureProvider moduleStructureProvider;

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ModuleStructureMethodInRegionCheck(IModuleStructureProvider moduleStructureProvider,
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
        builder.title(Messages.ModuleStructureMethodInRegionCheck_Title)
            .description(
                Messages.ModuleStructureMethodInRegionCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(455, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD)
            .parameter(MULTILEVEL_NESTING_OF_REGIONS, Boolean.class, DEFAULT_CHECK_NESTING_OF_REGIONS,
                Messages.ModuleStructureMethodInRegionCheck_Multilevel_nesting_of_regions);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        if (monitor.isCanceled())
        {
            return;
        }

        Method method = (Method)object;
        IV8Project project = v8ProjectManager.getProject(method);
        ScriptVariant scriptVariant = project.getScriptVariant();

        ModuleType moduleType = getModuleType(method);
        Collection<String> regionNames = moduleStructureProvider.getModuleStructureRegions(moduleType, scriptVariant);

        boolean multilevel = parameters.getBoolean(MULTILEVEL_NESTING_OF_REGIONS);

        Optional<RegionPreprocessor> region = multilevel ? getTopParentRegion(method) : getFirstParentRegion(method);

        // An export method located out of region in a form and command module is checked
        // by {@link ExportMethodInCommandFormModuleCheck}
        if (region.isEmpty())
        {
            addIssue(resultAceptor, method, String.join(", ", regionNames)); //$NON-NLS-1$
        }
        else if (moduleType != ModuleType.FORM_MODULE && moduleType != ModuleType.COMMAND_MODULE)
        {
            String publicName = ModuleStructureSection.PUBLIC.getName(scriptVariant);
            String internalName = ModuleStructureSection.INTERNAL.getName(scriptVariant);
            String privateName = ModuleStructureSection.PRIVATE.getName(scriptVariant);
            String regionName = region.get().getName();
            if (!method.isExport()
                && (publicName.equalsIgnoreCase(regionName) || internalName.equalsIgnoreCase(regionName)))
            {
                resultAceptor.addIssue(
                    MessageFormat.format(Messages.ModuleStructureMethodInRegionCheck_Only_export, regionName),
                    McorePackage.Literals.NAMED_ELEMENT__NAME);
            }
            else if (method.isExport() && !(publicName.equalsIgnoreCase(regionName)
                || internalName.equalsIgnoreCase(regionName) || privateName.equalsIgnoreCase(regionName)))
            {
                addIssue(resultAceptor, method, String.join(", ", publicName, internalName, privateName)); //$NON-NLS-1$
            }
        }
    }

    private void addIssue(ResultAcceptor resultAceptor, Method method, String regions)
    {
        resultAceptor.addIssue(MessageFormat.format(
            Messages.ModuleStructureMethodInRegionCheck_Method_should_be_placed_in_one_of_the_standard_regions,
            method.getName(), regions), McorePackage.Literals.NAMED_ELEMENT__NAME);
    }
}
