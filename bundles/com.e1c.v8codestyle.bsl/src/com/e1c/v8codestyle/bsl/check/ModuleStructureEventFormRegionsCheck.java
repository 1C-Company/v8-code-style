/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.resource.BslEventsService;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.form.model.DecorationExtInfo;
import com._1c.g5.v8.dt.form.model.EventHandlerContainer;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormCommandHandlerContainer;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.GroupExtInfo;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
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

    private static final String CHECK_ID = "module-structure-form-event-regions"; //$NON-NLS-1$

    private static final String PARAMETER_EXCLUDE_METHOD_NAME_PATTERN = "excludeModuleMethodNamePattern"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_NESTING_OF_REGIONS = Boolean.toString(Boolean.TRUE);

    private static final String MULTILEVEL_NESTING_OF_REGIONS = "multilevelNestingOfRegions"; //$NON-NLS-1$

    private static final String PATTERN_EXCLUDE = "^(?U)(Подключаемый|Attachable)_.*$"; //$NON-NLS-1$

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
            .extension(new StandardCheckExtension(455, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(MODULE)
            .parameter(PARAMETER_EXCLUDE_METHOD_NAME_PATTERN, String.class, PATTERN_EXCLUDE,
                Messages.ModuleStructureEventFormRegionsCheck_Excluded_method_names)
            .parameter(MULTILEVEL_NESTING_OF_REGIONS, Boolean.class, DEFAULT_CHECK_NESTING_OF_REGIONS,
                Messages.ModuleStructureEventFormRegionsCheck_Multilevel_nesting_of_regions);
    }

    @Override
    protected void check(Object object, ResultAcceptor result, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }

        Module module = (Module)object;
        IV8Project project = v8ProjectManager.getProject(module);
        ScriptVariant scriptVariant = project.getScriptVariant();
        if (module == null)
        {
            return;
        }
        EList<Method> methods = module.allMethods();
        Map<CaseInsensitiveString, List<EObject>> eventHandlers = bslEventsService.getEventHandlersContainer(module);
        for (Method method : methods)
        {
            String methodName = method.getName();
            if (methodName == null)
            {
                continue;
            }

            String excludeNamePattern = parameters.getString(PARAMETER_EXCLUDE_METHOD_NAME_PATTERN);
            if (!StringUtils.isEmpty(excludeNamePattern) && isExcludeName(methodName, excludeNamePattern))
            {
                continue;
            }

            boolean multilevel = parameters.getBoolean(MULTILEVEL_NESTING_OF_REGIONS);

            Optional<RegionPreprocessor> region =
                multilevel ? getTopParentRegion(method) : getFirstParentRegion(method);
            if (region.isEmpty())
            {
                continue;
            }

            String regionName = region.get().getName();
            List<EObject> containers = eventHandlers.get(new CaseInsensitiveString(methodName));
            if (containers == null)
            {
                if (isEventHandlerRegion(scriptVariant, regionName))
                {
                    addIssueShouldNotBeInRegion(result, methodName, regionName, method);
                }
                continue;
            }
            check(result, containers, regionName, methodName, scriptVariant, monitor, method);
        }
    }

    private void check(ResultAcceptor result, List<EObject> containers, String regionName, String methodName,
        ScriptVariant scriptVariant, IProgressMonitor monitor, EObject method)
    {
        for (EObject obj : containers)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (obj instanceof FormCommandHandlerContainer)
            {
                addIssueCommand(result, regionName, methodName, scriptVariant, method);
            }
            else if (obj instanceof EventHandlerContainer)
            {
                EventHandlerContainer container = (EventHandlerContainer)obj;
                check(result, container, regionName, methodName, scriptVariant, method);
            }
        }
    }

    private void check(ResultAcceptor result, EventHandlerContainer container, String regionName, String methodName,
        ScriptVariant scriptVariant, EObject method)
    {

        EventHandlerContainer object = getContainer(container);

        if (object instanceof Table)
        {
            addIssueTable(result, ((Table)object).getName(), regionName, methodName, scriptVariant, method);
        }
        else if (object instanceof FormField || object instanceof DecorationExtInfo || object instanceof GroupExtInfo)
        {
            addIssueItem(result, regionName, methodName, scriptVariant, method);
        }
        else if (object instanceof Form)
        {
            addIssueForm(result, regionName, methodName, scriptVariant, method);
        }
    }

    private EventHandlerContainer getContainer(EventHandlerContainer container)
    {
        EventHandlerContainer object = null;
        for (EObject e = container; e != null; e = e.eContainer())
        {
            if (e instanceof FormField)
            {
                object = (FormField)e;
            }
            else if (e instanceof DecorationExtInfo)
            {
                object = (DecorationExtInfo)e;
            }
            else if (e instanceof GroupExtInfo)
            {
                object = (GroupExtInfo)e;
            }
            else if (e instanceof Table)
            {
                return (Table)e;
            }
            else if (e instanceof Form && object == null)
            {
                return (Form)e;
            }

        }
        return object;
    }

    private void addIssueCommand(ResultAcceptor result, String regionName, String methodName,
        ScriptVariant scriptVariant, EObject method)
    {
        if (!ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant).equalsIgnoreCase(regionName))
        {
            String defRegionName = ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant);
            addIssueShouldBeInRegion(result, methodName, defRegionName, method);
        }
    }

    private void addIssueTable(ResultAcceptor result, String tableName, String regionName, String methodName,
        ScriptVariant scriptVariant, EObject method)
    {
        if (!(ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant) + tableName)
            .equalsIgnoreCase(regionName))
        {
            String defRegionName =
                ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant) + tableName;
            addIssueShouldBeInRegion(result, methodName, defRegionName, method);
        }
    }

    private void addIssueForm(ResultAcceptor result, String regionName, String methodName, ScriptVariant scriptVariant,
        EObject method)
    {
        if (!ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant).equalsIgnoreCase(regionName))
        {
            addIssueShouldBeInRegion(result, methodName,
                ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant), method);
        }
    }

    private void addIssueItem(ResultAcceptor result, String regionName, String methodName, ScriptVariant scriptVariant,
        EObject method)
    {
        if (!ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant)
            .equalsIgnoreCase(regionName))
        {
            addIssueShouldBeInRegion(result, methodName,
                ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant), method);
        }
    }

    private void addIssueShouldBeInRegion(ResultAcceptor result, String methodName, String defaultRegionName,
        EObject method)
    {
        result.addIssue(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__should_be_placed_in_the_region__1,
            methodName, defaultRegionName), method, NAMED_ELEMENT__NAME);
    }

    private void addIssueShouldNotBeInRegion(ResultAcceptor result, String methodName, String regionName,
        EObject method)
    {
        result.addIssue(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__can_not_be_placed_in_the_region__1,
            methodName, regionName), method, NAMED_ELEMENT__NAME);
    }

    private boolean isEventHandlerRegion(ScriptVariant scriptVariant, String regionName)
    {
        return ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant)
            .equalsIgnoreCase(regionName)
            || ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant).equalsIgnoreCase(regionName)
            || ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant).equalsIgnoreCase(regionName)
            || regionName != null && regionName
                .indexOf(ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant)) != -1;
    }

    private boolean isExcludeName(String name, String excludeNamePattern)
    {
        return StringUtils.isNotEmpty(excludeNamePattern) && name.matches(excludeNamePattern);
    }
}
