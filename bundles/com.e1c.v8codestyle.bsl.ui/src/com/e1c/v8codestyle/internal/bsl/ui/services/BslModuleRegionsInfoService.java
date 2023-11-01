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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

import com._1c.g5.v8.dt.bsl.common.EventItemType;
import com._1c.g5.v8.dt.bsl.common.IBslModuleEventData;
import com._1c.g5.v8.dt.bsl.common.IBslModuleTextInsertInfo;
import com._1c.g5.v8.dt.bsl.common.IBslModuleTextInsertInfoService;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.resource.owner.BslOwnerComputerService;
import com._1c.g5.v8.dt.bsl.ui.BslGeneratorMultiLangProposals;
import com._1c.g5.v8.dt.bsl.ui.event.BslModuleEventData;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.form.model.FormPackage;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;

/**
 * Module regions related implementation of {@link IBslModuleTextInsertInfoService}
 *
 * @author Kuznetsov Nikita
 */
public class BslModuleRegionsInfoService
    implements IBslModuleTextInsertInfoService
{
    @Override
    public IBslModuleTextInsertInfo getEventHandlerTextInsertInfo(IXtextDocument document, Module module,
        int defaultPosition, IBslModuleEventData data)
    {
        if (!(data instanceof BslModuleEventData))
        {
            return () -> defaultPosition;
        }
        URI moduleResourceURI = module.eResource().getURI();
        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(moduleResourceURI);
        IV8ProjectManager projectManager = rsp.get(IV8ProjectManager.class);
        BslOwnerComputerService bslOwnerComputerService = rsp.get(BslOwnerComputerService.class);
        IV8Project project = projectManager.getProject(moduleResourceURI);
        EClass moduleOwner = bslOwnerComputerService.computeOwnerEClass(module);
        EObject eventOwner = data.getEventOwner();
        BslModuleEventData regionData = (BslModuleEventData)data;
        EventItemType itemType = regionData.getEventItemType();
        String suffix = getSuffix(eventOwner, itemType);
        List<RegionPreprocessor> regionPreprocessors = BslUtil.getAllRegionPreprocessors(module);
        ScriptVariant scriptVariant = project.getScriptVariant();
        String declaredRegionName = getDeclaredRegionName(moduleOwner, itemType, scriptVariant);
        Map<String, BslModuleOffsets> regionOffsets =
            getRegionOffsets(document, regionPreprocessors, declaredRegionName, scriptVariant);
        int offset = getRegionOffset(regionOffsets, declaredRegionName, suffix, defaultPosition, scriptVariant);
        String regionName = null;
        if (!isRegionExists(regionOffsets, declaredRegionName, suffix))
        {
            regionName = suffix.isEmpty() ? declaredRegionName : (declaredRegionName + suffix);
        }
        return new BslModuleRegionsInfo(offset, module, regionName);
    }

    @Override
    public String wrap(IBslModuleTextInsertInfo moduleTextInsertInfo, String content)
    {
        if (moduleTextInsertInfo instanceof BslModuleRegionsInfo)
        {
            BslModuleRegionsInfo moduleRegionInformation = (BslModuleRegionsInfo)moduleTextInsertInfo;
            Module module = moduleTextInsertInfo.getModule();
            String regionName = moduleRegionInformation.getRegionName();
            if (module != null && regionName != null)
            {
                URI moduleResourceURI = module.eResource().getURI();
                IResourceServiceProvider rsp =
                    IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(moduleResourceURI);
                IV8ProjectManager projectManager = rsp.get(IV8ProjectManager.class);
                BslGeneratorMultiLangProposals proposals = rsp.get(BslGeneratorMultiLangProposals.class);
                IV8Project project = projectManager.getProject(moduleResourceURI);
                String lineSeparator = PreferenceUtils.getLineSeparator(project.getProject());
                proposals.setRussianLang(ScriptVariant.RUSSIAN.equals(project.getScriptVariant()));
                String beginRegion = proposals.getBeginRegionPropStr();
                String endRegion = proposals.getEndRegionPropStr();
                String space = proposals.getSpacePropStr();
                StringBuilder builder = new StringBuilder();
                builder.append(lineSeparator).append(beginRegion).append(space).append(regionName);
                builder.append(lineSeparator).append(content).append(lineSeparator);
                builder.append(endRegion);
                builder.append(lineSeparator);
                return builder.toString();
            }
        }
        return content;
    }

    private Map<String, BslModuleOffsets> getRegionOffsets(IXtextDocument document,
        List<RegionPreprocessor> regionPreprocessors, String targetRegionName, ScriptVariant scriptVariant)
    {
        ModuleStructureSection[] declaredRegionNames = ModuleStructureSection.values();
        Map<String, BslModuleOffsets> regionOffsets = new HashMap<>(declaredRegionNames.length / 2);
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
                            BslModuleOffsets moduleRegionInformation = null;
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
                                    moduleRegionInformation = BslModuleOffsets.create(document, node, nodeAfter);
                                    if (moduleRegionInformation == null)
                                    {
                                        return regionOffsets;
                                    }
                                }
                                moduleRegionInformation.addSuffix(suffix, document, node, nodeAfter);
                            }
                            if (moduleRegionInformation == null && !moduleStructureSection.isSuffixed())
                            {
                                moduleRegionInformation = BslModuleOffsets.create(document, node, nodeAfter);
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

    private int getRegionOffset(Map<String, BslModuleOffsets> regionOffsets, String declaredRegionName, String suffix,
        int defaultOffset, ScriptVariant scriptVariant)
    {
        int offset = defaultOffset;
        boolean createNewRegion = !isRegionExists(regionOffsets, declaredRegionName, suffix);
        BslModuleOffsets regionOffset = regionOffsets.get(declaredRegionName);
        if (regionOffset != null)
        {
            if (!suffix.isEmpty())
            {
                BslModuleOffsets suffixedRegionInformation = regionOffset.getInformationBySuffix(suffix);
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

    private boolean isRegionExists(Map<String, BslModuleOffsets> regionOffsets, String declaredRegionName,
        String suffix)
    {
        BslModuleOffsets moduleRegionInformation = regionOffsets.get(declaredRegionName);
        return (moduleRegionInformation != null)
            && (suffix.isEmpty() || moduleRegionInformation.getInformationBySuffix(suffix) != null);
    }

    private int getNewRegionOffset(Map<String, BslModuleOffsets> regionOffsets, String regionName, String suffix,
        int defaultOffset, ScriptVariant scriptVariant)
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
            BslModuleOffsets regionInformation = regionOffsets.get(declaredRegionName);
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

    private String getDeclaredRegionName(EClass moduleOwnerClass, EventItemType itemType, ScriptVariant scriptVariant)
    {
        String moduleOwnerName = moduleOwnerClass.getName();
        switch (moduleOwnerName)
        {
        case "AbstractForm": //$NON-NLS-1$
            return getDeclaredRegionNameForForm(itemType, scriptVariant);
        case "WebService": //$NON-NLS-1$
        case "HTTPService": //$NON-NLS-1$
        case "IntegrationService": //$NON-NLS-1$
            return getPrivateRegionName(scriptVariant);
        default:
            return getDefaultRegionName(scriptVariant);
        }
    }

    private String getDeclaredRegionNameForForm(EventItemType itemType, ScriptVariant scriptVariant)
    {
        switch (itemType)
        {
        case MAIN:
            return ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant);
        case COMMAND:
            return ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant);
        case TABLE:
            return ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant);
        default:
            return ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant);
        }
    }

    private String getPrivateRegionName(ScriptVariant scriptVariant)
    {
        return ModuleStructureSection.PRIVATE.getName(scriptVariant);
    }

    private String getDefaultRegionName(ScriptVariant scriptVariant)
    {
        return ModuleStructureSection.EVENT_HANDLERS.getName(scriptVariant);
    }

    private String getSuffix(EObject eventOwner, EventItemType itemType)
    {
        if (itemType.equals(EventItemType.TABLE))
        {
            while ((eventOwner = eventOwner.eContainer()) != null)
            {
                if (eventOwner.eClass() == FormPackage.Literals.TABLE)
                {
                    return ((NamedElement)eventOwner).getName();
                }
            }
        }
        return StringUtils.EMPTY;
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
