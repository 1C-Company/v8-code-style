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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.resource.BslEventsService;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com._1c.g5.v8.dt.mcore.Event;
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
 * Checks the region of form event handlers for methods related only to handlers.
 *
 * @author Artem Iliukhin
 *
 */
public class ModuleStructureEventFormRegionsCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHOICE_PROCESSING = "ChoiceProcessing"; //$NON-NLS-1$

    private static final String CHECK_ID = "module-structure-form-event-regions"; //$NON-NLS-1$

    private static final Set<String> COMMON_FORM_EVENTS =
        Set.of("OnCreateAtServer", "OnOpen", "OnReopen", "BeforeClose", "OnClose", CHOICE_PROCESSING, //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
            "ActivationProcessing", "NewWriteProcessing", "FillCheckProcessingAtServer", "OnSaveDataInSettingsAtServer", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
            "OnLoadDataFromSettingsAtServer", "ExternalEvent", "NotificationProcessing", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            "BeforeLoadDataFromSettingsAtServer", "URLProcessing", "NavigationProcessing", "OnChangeDisplaySettings", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
            "CollaborationSystemUsersAutoComplete", "CollaborationSystemUsersChoiceFormGetProcessing", //$NON-NLS-1$//$NON-NLS-2$
            "BeforeReopenFromOtherServer", //$NON-NLS-1$
            "OnReopenFromOtherServer", "OnMainServerAvailabilityChange", "URLListGetProcessing", "URLGetProcessing", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
            "AddInCrashEvent", "OnReadAtServer", "AfterWriteAtServer", "AfterWrite", "BeforeWriteAtServer", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
            "OnWriteAtServer", "ValueChoice", "BeforeWrite"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

    private static final Set<String> HEADER_FORM_EVENTS =
        Set.of("OnChange", "AutoComplete", CHOICE_PROCESSING, "Clearing", "Creating", "EditTextChange", "Opening", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "StartChoice", "StartListChoice", "TextEditEnd", "Tuning"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private static final Set<String> TABLE_ITEMS_FORM_EVENTS =
        Set.of("AfterDeleteRow", "BeforeAddRow", "BeforeCollapse", "BeforeDeleteRow", "BeforeEditEnd", "BeforeExpand", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "BeforeRowChange", CHOICE_PROCESSING, "Drag", "DragCheck", "DragEnd", "DragStart", "NewWriteProcessing", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "OnActivateCell", "OnActivateField", "OnActivateRow", "OnChange", "OnCurrentParentChange", "OnEditEnd", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "OnStartEdit", "RefreshRequestProcessing", "Selection", "ValueChoice"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private static final Set<String> COMMAND_FORM_EVENTS = Set.of("Action"); //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    private final BslEventsService bslEventsService;

    @Inject
    public ModuleStructureEventFormRegionsCheck(IV8ProjectManager v8ProjectManager, BslEventsService bslEventsService)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
        this.bslEventsService = bslEventsService;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ModuleStructureEventFormRegionsCheck_Title)
            .description(Messages.ModuleStructureEventFormRegionsCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor result, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;

        IV8Project project = v8ProjectManager.getProject(method);

        ScriptVariant scriptVariant = project.getScriptVariant();

        Module module = EcoreUtil2.getContainerOfType(method, Module.class);
        if (module == null)
        {
            return;
        }

        ModuleType moduleType = module.getModuleType();
        if (!ModuleType.FORM_MODULE.equals(moduleType))
        {
            return;
        }

        RegionPreprocessor region = EcoreUtil2.getContainerOfType(method, RegionPreprocessor.class);
        if (region == null)
        {
            return;
        }

        Optional<RegionPreprocessor> parent = getParentRegion(region);
        if (parent.isPresent())
        {
            region = parent.get();
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
                    return;
                }
            }
        }

        Map<CaseInsensitiveString, Event> eventHandlers = new HashMap<>();
        for (Entry<CaseInsensitiveString, List<EObject>> entry : bslEventsService.getEventHandlers(module).entrySet())
        {
            for (EObject event : entry.getValue())
            {
                if (event instanceof Event)
                {
                    eventHandlers.put(entry.getKey(), (Event)event);
                }
            }
        }

        String name = method.getName();
        CaseInsensitiveString methodName = new CaseInsensitiveString(name);
        Event event = eventHandlers.get(methodName);
        String regionName = region.getName();

        if (!check(result, name, event, regionName, ModuleStructureSection.FORM_EVENT_HANDLERS, COMMON_FORM_EVENTS,
            scriptVariant))
        {
            return;
        }

        if (!check(result, name, event, regionName, ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS,
            HEADER_FORM_EVENTS, scriptVariant))
        {
            return;
        }

        if (!check(result, name, event, regionName, ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS,
            TABLE_ITEMS_FORM_EVENTS, scriptVariant))
        {
            return;
        }

        check(result, name, event, regionName, ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS, COMMAND_FORM_EVENTS,
            scriptVariant);
    }

    private boolean check(ResultAcceptor resultAceptor, String methodName, Event event, String regionName,
        ModuleStructureSection section, Set<String> defaultEvents, ScriptVariant scriptVariant)
    {

        String defaultRegionName = section.getName(scriptVariant);
        if (defaultRegionName.equals(regionName) && (event == null || !defaultEvents.contains(event.getName())))
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__can_not_be_placed_in_the_region__1, methodName,
                    regionName), McorePackage.Literals.NAMED_ELEMENT__NAME);
            return false;
        }

        if (!defaultRegionName.equals(regionName) && event != null && defaultEvents.contains(event.getName())
            && isSameParam(event, defaultRegionName, scriptVariant))
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__should_be_placed_in_the_region__1,
                    methodName, defaultRegionName),
                McorePackage.Literals.NAMED_ELEMENT__NAME);
            return false;
        }

        return true;
    }

    private boolean isSameParam(Event event, String regionName, ScriptVariant scriptVariant)
    {
        String eventHandlersName = ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant);
        String eventHeaderHandlersName = ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant);
        if (CHOICE_PROCESSING.equals(event.getName()))
        {
            if (eventHandlersName.equals(regionName))
            {
                return event.getParamSet().size() == 2;
            }
            else if (eventHeaderHandlersName.equals(regionName))
            {
                return event.getParamSet().size() == 3;
            }
        }
        return true;
    }

}
