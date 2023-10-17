/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.internal.bsl.ui.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

import com._1c.g5.v8.dt.bsl.common.IBslModuleRegionsService;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.common.Pair;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;

/**
 * Implementation of {@link IBslModuleRegionsService}
 *
 * @author Kuznetsov Nikita
 */
public class BslModuleRegionsService
    implements IBslModuleRegionsService
{
    private static final String FORM_NAME = "Form"; //$NON-NLS-1$
    private static final String GRAPHICAL_SCHEME_NAME = "GraphicalScheme"; //$NON-NLS-1$

    @Override
    public Pair<Integer, String> getRegionInformation(IXtextDocument document,
        List<RegionPreprocessor> regionPreprocessors, String suffix, int defaultOffset, ScriptVariant scriptVariant,
        String eventOwnerRootName, boolean isMain, boolean isCommand, boolean isTable)
    {
        String declaredRegionName =
            getDeclaredRegionName(eventOwnerRootName, isMain, isCommand, isTable, scriptVariant);
        Map<String, ModuleRegionInformation> regionOffsets =
            getRegionOffsets(document, regionPreprocessors, declaredRegionName, scriptVariant);
        int offset = getRegionOffset(regionOffsets, declaredRegionName, suffix, defaultOffset, scriptVariant);
        String regionName = null;
        if (!isRegionExists(regionOffsets, declaredRegionName, suffix))
        {
            regionName = suffix.isEmpty() ? declaredRegionName : (declaredRegionName + suffix);
        }
        return new Pair<>(offset, regionName);
    }

    @Override
    public String wrapByRegion(String content, String regionName, String beginRegion, String endRegion, String space,
        String lineSeparator)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(lineSeparator).append(beginRegion).append(space).append(regionName);
        builder.append(content);
        builder.append(endRegion);
        builder.append(lineSeparator);
        return builder.toString();
    }

    private Map<String, ModuleRegionInformation> getRegionOffsets(IXtextDocument document,
        List<RegionPreprocessor> regionPreprocessors, String targetRegionName, ScriptVariant scriptVariant)
    {
        ModuleStructureSection[] declaredRegionNames = ModuleStructureSection.values();
        Map<String, ModuleRegionInformation> regionOffsets = new HashMap<>(declaredRegionNames.length / 2);
        for (RegionPreprocessor regionPreprocessor : regionPreprocessors)
        {
            INode nodeAfter = NodeModelUtils.findActualNodeFor(regionPreprocessor.getItemAfter());
            if (nodeAfter != null)
            {
                String preprocessorRegionName = regionPreprocessor.getName();
                for (int regionNameIndex = 0; regionNameIndex < declaredRegionNames.length; regionNameIndex++)
                {
                    ModuleStructureSection moduleStructureSection = declaredRegionNames[regionNameIndex];
                    String declaredRegionName = moduleStructureSection.getName(scriptVariant);
                    if ((preprocessorRegionName != null)
                        && isMatchingRegion(preprocessorRegionName, declaredRegionName))
                    {
                        INode node = NodeModelUtils.findActualNodeFor(regionPreprocessor.getItem());
                        if (node != null)
                        {
                            ModuleRegionInformation moduleRegionInformation = null;
                            if (!preprocessorRegionName.equals(declaredRegionName))
                            {
                                if (!moduleStructureSection.isSuffixed())
                                {
                                    continue;
                                }
                                String suffix = getSuffixOfMatchingRegion(preprocessorRegionName, declaredRegionName);
                                moduleRegionInformation = regionOffsets.get(declaredRegionName);
                                if (moduleRegionInformation == null)
                                {
                                    moduleRegionInformation = ModuleRegionInformation.create(document, node, nodeAfter);
                                    if (moduleRegionInformation == null)
                                    {
                                        return regionOffsets;
                                    }
                                }
                                moduleRegionInformation.addSuffix(suffix, document, node, nodeAfter);
                            }
                            if (moduleRegionInformation == null && !moduleStructureSection.isSuffixed())
                            {
                                moduleRegionInformation = ModuleRegionInformation.create(document, node, nodeAfter);
                            }
                            if (moduleRegionInformation != null)
                            {
                                regionOffsets.put(declaredRegionName, moduleRegionInformation);
                                if ((targetRegionName != null) && targetRegionName.equals(preprocessorRegionName))
                                {
                                    return regionOffsets;
                                }
                            }
                        }
                    }
                }
            }
        }
        return regionOffsets;
    }

    private int getRegionOffset(Map<String, ModuleRegionInformation> regionOffsets, String declaredRegionName,
        String suffix, int defaultOffset, ScriptVariant scriptVariant)
    {
        int offset = defaultOffset;
        boolean createNewRegion = !isRegionExists(regionOffsets, declaredRegionName, suffix);
        ModuleRegionInformation regionOffset = regionOffsets.get(declaredRegionName);
        if (regionOffset != null)
        {
            if (!suffix.isEmpty())
            {
                ModuleRegionInformation suffixedRegionInformation = regionOffset.getInformationBySuffix(suffix);
                if (suffixedRegionInformation != null)
                {
                    return suffixedRegionInformation.getBeforeEndOffset();
                }
                else if (createNewRegion)
                {
                    return getNewRegionOffset(regionOffsets, declaredRegionName, suffix, defaultOffset, scriptVariant);
                }
            }
            return regionOffset.getBeforeEndOffset();
        }
        if (createNewRegion)
        {
            return getNewRegionOffset(regionOffsets, declaredRegionName, suffix, defaultOffset, scriptVariant);
        }
        return offset;
    }

    private boolean isRegionExists(Map<String, ModuleRegionInformation> regionOffsets, String declaredRegionName,
        String suffix)
    {
        ModuleRegionInformation moduleRegionInformation = regionOffsets.get(declaredRegionName);
        return (moduleRegionInformation != null)
            && (suffix.isEmpty() || moduleRegionInformation.getInformationBySuffix(suffix) != null);
    }

    private int getNewRegionOffset(Map<String, ModuleRegionInformation> regionOffsets, String regionName,
        String suffix, int defaultOffset, ScriptVariant scriptVariant)
    {
        boolean placeBefore = false;
        int offset = regionOffsets.isEmpty() ? 0 : defaultOffset;
        ModuleStructureSection[] declaredRegionNames = ModuleStructureSection.values();
        for (int regionNameIndex = 0; regionNameIndex < declaredRegionNames.length; regionNameIndex++)
        {
            ModuleStructureSection moduleStructuredSection = declaredRegionNames[regionNameIndex];
            String declaredRegionName = moduleStructuredSection.getName(scriptVariant);
            if (declaredRegionName.equals(regionName))
            {
                placeBefore = true;
            }
            ModuleRegionInformation regionInformation = regionOffsets.get(declaredRegionName);
            if (regionInformation != null)
            {
                if (placeBefore && (suffix.isEmpty() || !declaredRegionName.equals(regionName)))
                {
                    return regionInformation.getStartOffset();
                }
                offset = placeBefore ? regionInformation.getStartOffset() : regionInformation.getEndOffset();
            }
        }
        return offset;
    }

    private String getDeclaredRegionName(String eventOwnerRootName, boolean isMain, boolean isCommand, boolean isTable,
        ScriptVariant scriptVariant)
    {
        if (eventOwnerRootName.equals(FORM_NAME))
        {
            return getDeclaredRegionNameForForm(isMain, isCommand, isTable, scriptVariant);
        }
        if (eventOwnerRootName.equals(GRAPHICAL_SCHEME_NAME))
        {
            return getDeclaredRegionNameForGraphicalScheme(scriptVariant);
        }
        return getDefaultRegionName(scriptVariant);
    }

    private String getDeclaredRegionNameForForm(boolean isMain, boolean isCommand, boolean isTable,
        ScriptVariant scriptVariant)
    {
        if (isMain)
        {
            return ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant);
        }
        else if (isCommand)
        {
            return ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant);
        }
        else if (isTable)
        {
            return ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant);
        }
        return ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant);
    }

    private String getDeclaredRegionNameForGraphicalScheme(ScriptVariant scriptVariant)
    {
        return ModuleStructureSection.EVENT_HANDLERS.getName(scriptVariant);
    }

    private String getDefaultRegionName(ScriptVariant scriptVariant)
    {
        return ModuleStructureSection.PRIVATE.getName(scriptVariant);
    }

    private String getSuffixOfMatchingRegion(String regionName, String declaredRegionName)
    {
        return regionName.substring(declaredRegionName.length(), regionName.length());
    }

    private boolean isMatchingRegion(String regionName, String declaredRegionName)
    {
        int declaredRegionNameLength = declaredRegionName.length();
        return regionName.length() >= declaredRegionNameLength
            && regionName.substring(0, declaredRegionNameLength).equals(declaredRegionName);
    }
}
