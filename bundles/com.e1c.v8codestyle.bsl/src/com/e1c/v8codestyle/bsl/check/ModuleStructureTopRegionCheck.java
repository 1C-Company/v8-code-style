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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
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
 * Checks that region named as module structure name is on top level of module.
 * Checks order of standard regions.
 * Checks duplicate of standard regions.
 *
 * @author Dmitriy Marmyshev
 * @author Artem Iliukhin
 *
 */
public class ModuleStructureTopRegionCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "module-structure-top-region"; //$NON-NLS-1$

    private static final String CHECK_DUPLICATES_OF_STANDARD_REGIONS = "checkDuplicates"; //$NON-NLS-1$

    private static final String CHECK_ORDER_OF_STANDARD_REGIONS = "checkOrder"; //$NON-NLS-1$

    private static final String PARAMETER_EXCLUDE_REGION_LIST = "excludeRegionName"; //$NON-NLS-1$

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
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(455, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE)
            .parameter(CHECK_DUPLICATES_OF_STANDARD_REGIONS, Boolean.class, Boolean.TRUE.toString(),
                Messages.ModuleStructureTopRegionCheck_Check_duplicates_of_standard_regions)
            .parameter(CHECK_ORDER_OF_STANDARD_REGIONS, Boolean.class, Boolean.TRUE.toString(),
                Messages.ModuleStructureTopRegionCheck_Check_order_of_standard_regions)
            .parameter(PARAMETER_EXCLUDE_REGION_LIST, String.class, StringUtils.EMPTY,
                Messages.ModuleStructureTopRegionCheck_Exclude_region_name);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        if (monitor.isCanceled())
        {
            return;
        }

        Module module = (Module)object;

        List<RegionPreprocessor> allRegions = BslUtil.getAllRegionPreprocessors(module);
        if (allRegions.isEmpty())
        {
            return;
        }

        ScriptVariant scriptVariant = v8ProjectManager.getProject(module).getScriptVariant();
        Collection<String> names =
            moduleStructureProvider.getModuleStructureRegions(module.getModuleType(), scriptVariant);

        check(resultAceptor, allRegions, names, parameters, scriptVariant, monitor);

    }

    private void check(ResultAcceptor resultAceptor, List<RegionPreprocessor> allRegions, Collection<String> baseNames,
        ICheckParameters parameters, ScriptVariant scriptVariant, IProgressMonitor monitor)
    {

        Map<String, List<RegionPreprocessor>> countRegions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        List<String> topRegions = new ArrayList<>();
        countDoublesAndFindTopRegions(allRegions, topRegions, countRegions);
        LinkedList<String> baseOrdered = new LinkedList<>(baseNames);

        String tableName = ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant);
        String commandName = ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant);
        String[] regionNames = parameters.getString(PARAMETER_EXCLUDE_REGION_LIST).split(","); //$NON-NLS-1$

        boolean checkDuplicates = parameters.getBoolean(CHECK_DUPLICATES_OF_STANDARD_REGIONS);
        boolean checkOrder = parameters.getBoolean(CHECK_ORDER_OF_STANDARD_REGIONS);
        for (RegionPreprocessor region : allRegions)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            String regionName = region.getName();
            if (isExcludeName(regionName, regionNames))
            {
                continue;
            }

            boolean isStandard = baseNames.stream().anyMatch(regionName::equalsIgnoreCase)
                || startsWithIgnoreCase(regionName, tableName);
            boolean isTop = topRegions.stream().anyMatch(regionName::equalsIgnoreCase);
            boolean isDuplicate = countRegions.get(regionName).size() > 1;

            if (checkDuplicates && isStandard && isDuplicate)
            {
                addIssueDuplicates(resultAceptor, region);
            }

            if (!isTop && isStandard)
            {
                addIssueTop(resultAceptor, region);
            }

            if (isTop && baseNames.stream().noneMatch(regionName::equalsIgnoreCase)
                && !startsWithIgnoreCase(regionName, tableName))
            {
                addIssueStandard(resultAceptor, region);
            }

            if (isTop && isStandard && checkOrder && !isDuplicate)
            {
                addIssueOrder(resultAceptor, topRegions, baseOrdered, regionName, region, tableName, commandName);
            }

        }
    }

    private static boolean startsWithIgnoreCase(String str, String prefix)
    {
        if (str == null || prefix == null)
        {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length())
        {
            return false;
        }
        boolean ignoreCase = true;
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    private void check(ResultAcceptor resultAceptor, RegionPreprocessor region, List<String> topRegions,
        LinkedList<String> baseOrdered, String tableName, String commandName)
    {
        while (baseOrdered.peek() != null && !startsWithIgnoreCase(baseOrdered.peek(), tableName)
            && !commandName.equalsIgnoreCase(baseOrdered.peek()))
        {
            baseOrdered.poll();
        }

        if (baseOrdered.peek() != null && startsWithIgnoreCase(baseOrdered.peek(), tableName))
        {
            baseOrdered.poll();
        }
        else if (baseOrdered.isEmpty() && topRegions.size() > 1
            || baseOrdered.peek() != null && !commandName.equalsIgnoreCase(baseOrdered.peek()))
        {
            resultAceptor.addIssue(Messages.ModuleStructureTopRegionCheck_Region_has_the_wrong_order, region,
                NAMED_ELEMENT__NAME);
        }
    }

    private void check(ResultAcceptor resultAceptor, RegionPreprocessor region, LinkedList<String> baseOrdered,
        String regionName)
    {
        while (baseOrdered.peek() != null && !regionName.equalsIgnoreCase(baseOrdered.peek()))
        {
            baseOrdered.poll();
        }

        if (baseOrdered.peek() != null && regionName.equalsIgnoreCase(baseOrdered.peek()))
        {
            baseOrdered.poll();
        }
        else if (baseOrdered.isEmpty())
        {
            resultAceptor.addIssue(Messages.ModuleStructureTopRegionCheck_Region_has_the_wrong_order, region,
                NAMED_ELEMENT__NAME);
        }
    }


    private void addIssueOrder(ResultAcceptor resultAceptor, List<String> topRegions, LinkedList<String> baseOrdered,
        String regionName, RegionPreprocessor regionPreprocessor, String tableName, String commandName)
    {
        if (startsWithIgnoreCase(regionName, tableName))
        {
            check(resultAceptor, regionPreprocessor, topRegions, baseOrdered, tableName, commandName);
        }
        else
        {
            check(resultAceptor, regionPreprocessor, baseOrdered, regionName);
        }
    }

    private void addIssueStandard(ResultAcceptor resultAceptor, RegionPreprocessor region)
    {
        resultAceptor.addIssue(Messages.ModuleStructureTopRegionCheck_Region_is_not_standard_for_current_type_of_module,
            region, NAMED_ELEMENT__NAME);
    }


    private void addIssueTop(ResultAcceptor resultAceptor, RegionPreprocessor region)
    {
        resultAceptor.addIssue(Messages.ModuleStructureTopRegionCheck_error_message, region, NAMED_ELEMENT__NAME);
    }

    private void addIssueDuplicates(ResultAcceptor resultAceptor, RegionPreprocessor region)
    {
        resultAceptor.addIssue(Messages.ModuleStructureTopRegionCheck_Region_has_duplicate, region,
            NAMED_ELEMENT__NAME);
    }

    private void countDoublesAndFindTopRegions(List<RegionPreprocessor> allRegions, List<String> topRegions,
        Map<String, List<RegionPreprocessor>> countRegions)
    {
        for (RegionPreprocessor region : allRegions)
        {
            String regionName = region.getName();
            countRegions.putIfAbsent(regionName, new ArrayList<>());
            countRegions.get(regionName).add(region);
            if (getFirstParentRegion(region).isEmpty())
            {
                topRegions.add(regionName);
            }
        }
    }

    private boolean isExcludeName(String regionName, String[] regionNames)
    {
        for (String name : regionNames)
        {
            if (regionName.equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

}
