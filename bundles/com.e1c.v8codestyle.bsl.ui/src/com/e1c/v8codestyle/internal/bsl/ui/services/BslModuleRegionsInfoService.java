/*******************************************************************************
 * Copyright (C) 2023-2024, 1C-Soft LLC and others.
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
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com._1c.g5.v8.dt.bsl.common.EventItemType;
import com._1c.g5.v8.dt.bsl.common.IBslModuleEventData;
import com._1c.g5.v8.dt.bsl.common.IBslModuleTextInsertInfo;
import com._1c.g5.v8.dt.bsl.common.IBslModuleTextInsertInfoService;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.resource.owner.BslOwnerComputerService;
import com._1c.g5.v8.dt.bsl.ui.BslGeneratorMultiLangProposals;
import com._1c.g5.v8.dt.bsl.ui.editor.BslXtextDocument;
import com._1c.g5.v8.dt.bsl.ui.event.BslModuleEventData;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.form.model.FormPackage;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.v8codestyle.bsl.IModuleStructureProvider;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.google.inject.Inject;

/**
 * Module regions related implementation of {@link IBslModuleTextInsertInfoService}
 *
 * @author Kuznetsov Nikita
 */
public class BslModuleRegionsInfoService
    implements IBslModuleTextInsertInfoService
{

    @Inject
    private IModuleStructureProvider moduleStructureProvider;

    @Override
    public IBslModuleTextInsertInfo getEventHandlerTextInsertInfo(IXtextDocument document, int defaultPosition,
        IBslModuleEventData data)
    {
        if (!(data instanceof BslModuleEventData))
        {
            return IBslModuleTextInsertInfo.getDefaultModuleTextInsertInfo(document, defaultPosition);
        }
        BslModuleEventData bslModuleEventData = (BslModuleEventData)data;
        URI resourceURI = document.getResourceURI();
        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(resourceURI);
        IV8ProjectManager projectManager = rsp.get(IV8ProjectManager.class);
        IV8Project project = projectManager.getProject(resourceURI);
        ModuleInfoUnitOfWork moduleInfoUnitOfWork = new ModuleInfoUnitOfWork(rsp.get(BslOwnerComputerService.class));
        ModuleInfo moduleInfo = document instanceof BslXtextDocument bslXTextDocument
            ? bslXTextDocument.readOnlyDataModelWithoutSync(moduleInfoUnitOfWork)
            : document.readOnly(moduleInfoUnitOfWork);
        EObject eventOwner = data.getEventOwner();
        BslModuleEventData regionData = (BslModuleEventData)data;
        EventItemType itemType = regionData.getEventItemType();
        String suffix = getSuffix(eventOwner, itemType, bslModuleEventData.isInternal());
        ScriptVariant scriptVariant = project.getScriptVariant();
        String declaredRegionName =
            getDeclaredRegionName(moduleInfo.owner, itemType, bslModuleEventData.isInternal(), scriptVariant);
        Map<String, BslModuleOffsets> regionOffsets =
            getRegionOffsets(document, moduleInfo.regionPreprocessors, declaredRegionName, scriptVariant);
        BslModuleOffsets bslModuleOffsets = regionOffsets.get(declaredRegionName);
        int offset = getRegionOffset(regionOffsets, declaredRegionName, suffix, defaultPosition, scriptVariant);
        String regionName = null;
        boolean createRegion = !isRegionExists(regionOffsets, declaredRegionName, suffix);
        int clearOffset = 0;
        int clearLength = 0;
        if (bslModuleOffsets != null && bslModuleOffsets.needReplace())
        {
            createRegion = true;
            String lineSeparator = PreferenceUtils.getLineSeparator(project.getProject());
            int lineSeparatorOffset =
                bslModuleOffsets.getStartOffset() >= lineSeparator.length() ? lineSeparator.length() : 0;
            clearOffset = bslModuleOffsets.getStartOffset() - lineSeparatorOffset;
            clearLength = bslModuleOffsets.getEndOffset() - bslModuleOffsets.getStartOffset() + lineSeparatorOffset;
        }
        if (createRegion && project.getProject() != null
            && moduleStructureProvider.canCreateStructure(project.getProject()))
        {
            regionName = suffix.isEmpty() ? declaredRegionName : (declaredRegionName + suffix);
        }
        int flags = calculateFlags(project, regionOffsets, offset);
        return new BslModuleRegionsInfo(resourceURI, offset, clearOffset, clearLength, flags, regionName);
    }

    @Override
    public String wrap(IBslModuleTextInsertInfo moduleTextInsertInfo, String content)
    {
        if (moduleTextInsertInfo instanceof BslModuleRegionsInfo moduleRegionInformation)
        {
            String regionName = moduleRegionInformation.getRegionName();
            if (regionName != null)
            {
                IResourceServiceProvider rsp = IResourceServiceProvider.Registry.INSTANCE
                    .getResourceServiceProvider(moduleTextInsertInfo.getResourceURI());
                IV8ProjectManager projectManager = rsp.get(IV8ProjectManager.class);
                BslGeneratorMultiLangProposals proposals = rsp.get(BslGeneratorMultiLangProposals.class);
                IV8Project project = projectManager.getProject(moduleTextInsertInfo.getResourceURI());
                String lineSeparator = PreferenceUtils.getLineSeparator(project.getProject());
                proposals.setRussianLang(ScriptVariant.RUSSIAN.equals(project.getScriptVariant()));
                String beginRegion = proposals.getBeginRegionPropStr();
                String endRegion = proposals.getEndRegionPropStr();
                String space = proposals.getSpacePropStr();
                StringBuilder builder = new StringBuilder();
                int flags = moduleRegionInformation.getRegionFlags();
                boolean hasRegionBefore = (flags
                    & BslModuleRegionsInfo.REGION_FLAG_HAS_REGION_BEFORE) == BslModuleRegionsInfo.REGION_FLAG_HAS_REGION_BEFORE;
                boolean hasRegionAfter = (flags
                    & BslModuleRegionsInfo.REGION_FLAG_HAS_REGION_AFTER) == BslModuleRegionsInfo.REGION_FLAG_HAS_REGION_AFTER;
                if (moduleTextInsertInfo.getPosition() != 0)
                {
                    builder.append(lineSeparator);
                }
                builder.append(beginRegion).append(space).append(regionName);
                builder.append(content);
                builder.append(endRegion);
                if (moduleTextInsertInfo.getPosition() == 0 || (hasRegionAfter && !hasRegionBefore))
                {
                    builder.append(lineSeparator);
                }
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
                                    if (node.getLength() == 0)
                                    {
                                        moduleRegionInformation.setNeedReplace();
                                    }
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

    private int calculateFlags(IV8Project project, Map<String, BslModuleOffsets> regionOffsets, int offset)
    {
        int flags = 0;
        for (BslModuleOffsets offsets : regionOffsets.values())
        {
            if (offsets.getEndOffset() <= offset)
            {
                flags |= BslModuleRegionsInfo.REGION_FLAG_HAS_REGION_BEFORE;
            }
            if (offsets.getStartOffset() >= offset)
            {
                flags |= BslModuleRegionsInfo.REGION_FLAG_HAS_REGION_AFTER;
            }
        }
        return flags;
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
        BslModuleOffsets lastRegionInformation = null;
        BslModuleOffsets regionInformation = null;
        for (int regionNameIndex = 0; regionNameIndex < declaredRegionNames.length; regionNameIndex++)
        {
            ModuleStructureSection moduleStructuredSection = declaredRegionNames[regionNameIndex];
            String declaredRegionName = moduleStructuredSection.getName(scriptVariant);
            if (declaredRegionName.equals(regionName))
            {
                placeBefore = true;
            }
            if (regionInformation != null)
            {
                lastRegionInformation = regionInformation;
            }
            regionInformation = regionOffsets.get(declaredRegionName);
            if (regionInformation != null)
            {
                if (placeBefore && (suffix.isEmpty() || !declaredRegionName.equals(regionName)))
                {
                    return lastRegionInformation != null ? lastRegionInformation.getEndOffset()
                        : regionInformation.getStartOffset();
                }
                offset = placeBefore ? regionInformation.getStartOffset() : regionInformation.getEndOffset();
            }
        }
        return offset;
    }

    private String getDeclaredRegionName(EClass moduleOwnerClass, EventItemType itemType, boolean isInternal,
        ScriptVariant scriptVariant)
    {
        if (isInternal)
        {
            return getPrivateRegionName(scriptVariant);
        }
        if (moduleOwnerClass.getName().equals("AbstractForm")) //$NON-NLS-1$
        {
            return getDeclaredRegionNameForForm(itemType, scriptVariant);
        }
        return getDefaultRegionName(scriptVariant);
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

    private String getSuffix(EObject eventOwner, EventItemType itemType, boolean isInternal)
    {
        if (isInternal)
        {
            return StringUtils.EMPTY;
        }
        if (itemType.equals(EventItemType.TABLE))
        {
            EObject container = eventOwner;
            while (container != null)
            {
                if (container.eClass() == FormPackage.Literals.TABLE)
                {
                    return ((NamedElement)container).getName();
                }
                else
                {
                    container = container.eContainer();
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

    private final class ModuleInfo
    {
        private final EClass owner;
        private final List<RegionPreprocessor> regionPreprocessors;

        public ModuleInfo(EClass owner, List<RegionPreprocessor> regionPreprocessors)
        {
            this.owner = owner;
            this.regionPreprocessors = regionPreprocessors;
        }
    }

    private final class ModuleInfoUnitOfWork
        implements IUnitOfWork<ModuleInfo, XtextResource>
    {
        private final BslOwnerComputerService bslOwnerComputerService;

        public ModuleInfoUnitOfWork(BslOwnerComputerService bslOwnerComputerService)
        {
            this.bslOwnerComputerService = bslOwnerComputerService;
        }

        @Override
        public ModuleInfo exec(XtextResource resource) throws Exception
        {
            Module module = (Module)resource.getParseResult().getRootASTElement();
            return new ModuleInfo(bslOwnerComputerService.computeOwnerEClass(module),
                BslUtil.getAllRegionPreprocessors(module));
        }
    }
}
