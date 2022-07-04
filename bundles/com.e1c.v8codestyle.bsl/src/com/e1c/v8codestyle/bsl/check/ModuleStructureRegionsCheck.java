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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItemDeclareStatement;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.McorePackage;
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
 * Checks that standard regions are defined in the module.
 *
 * @author Artem Iliukhin
 *
 */
public class ModuleStructureRegionsCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "module-structure-regions"; //$NON-NLS-1$

    private final IModuleStructureProvider moduleStructureProvider;

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ModuleStructureRegionsCheck(IModuleStructureProvider moduleStructureProvider,
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
        builder.title(Messages.ModuleStructureRegionCheck_title)
            .description(Messages.ModuleStructureRegionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .disable()
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;
        IV8Project project = v8ProjectManager.getProject(method);

        Module module = EcoreUtil2.getContainerOfType(method, Module.class);
        if (module == null)
        {
            return;
        }

        ModuleType moduleType = module.getModuleType();
        Collection<String> names =
            moduleStructureProvider.getModuleStructureRegions(moduleType, project.getScriptVariant());

        RegionPreprocessor region = EcoreUtil2.getContainerOfType(method, RegionPreprocessor.class);
        if (region == null)
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleStructureRegionCheck_error_message_0, method.getName()),
                McorePackage.Literals.NAMED_ELEMENT__NAME);
        }

        PreprocessorItem preprocessorItem = region.getItemAfter();
        if (preprocessorItem != null)
        {
            ICompositeNode node = NodeModelUtils.findActualNodeFor(preprocessorItem);
            if (node != null)
            {
                ICompositeNode nodeMethod = NodeModelUtils.findActualNodeFor(method);
                if (nodeMethod != null && nodeMethod.getTotalOffset() >= node.getTotalOffset())
                {
                    resultAceptor.addIssue(
                        MessageFormat.format(Messages.ModuleStructureRegionCheck_error_message_0, method.getName()),
                        McorePackage.Literals.NAMED_ELEMENT__NAME);
                }
            }
        }

        Optional<RegionPreprocessor> parent = getParentRegion(region);
        if (parent.isPresent())
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleStructureRegionCheck_error_message_0, names.toString()), module);
        }

        if (method.isExport())
        {

        }




//        ModuleType moduleType = module.getModuleType();
//        Collection<String> names =
//            moduleStructureProvider.getModuleStructureRegions(moduleType, project.getScriptVariant());
//
//        Set<RegionPreprocessor> regions = module.getDeclareStatements()
//            .stream()
//            .filter(RegionPreprocessor.class::isInstance)
//            .map(RegionPreprocessor.class::cast)
//            .collect(Collectors.toSet());
//
//        List<String> allRegions = new ArrayList<>();
//        collectAllRegions(regions, allRegions);
//
//        for (String name : names)
//        {
//            if (!allRegions.contains(name))
//            {
//                resultAceptor.addIssue(MessageFormat.format(Messages.ModuleStructureRegionCheck_error_message_0, name),
//                    module);
//            }
//        }
    }

    private void collectAllRegions(Set<RegionPreprocessor> regions, List<String> allRegions)
    {
        if (regions.isEmpty())
        {
            return;
        }

        for (RegionPreprocessor region : regions)
        {
            allRegions.add(region.getName());

            PreprocessorItem itemAfter = region.getItemAfter();
            if (itemAfter != null && itemAfter.hasDeclareStatements()
                && itemAfter instanceof PreprocessorItemDeclareStatement)
            {
                Set<RegionPreprocessor> regs = ((PreprocessorItemDeclareStatement)itemAfter).getDeclareStatements()
                    .stream()
                    .filter(RegionPreprocessor.class::isInstance)
                    .map(RegionPreprocessor.class::cast)
                    .collect(Collectors.toSet());
                collectAllRegions(regs, allRegions);
            }

        }

    }

}
