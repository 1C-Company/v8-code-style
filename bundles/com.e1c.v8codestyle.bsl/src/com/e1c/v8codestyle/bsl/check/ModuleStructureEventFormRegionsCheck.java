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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

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

    private static final String CHECK_ID = "module-structure-form-event-regions"; //$NON-NLS-1$

    private static final String PARAMETER_EXCLUDE_METHOD_NAME_PATTERN = "excludeModuleMethodNamePattern"; //$NON-NLS-1$

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
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD)
            .parameter(PARAMETER_EXCLUDE_METHOD_NAME_PATTERN, String.class, PATTERN_EXCLUDE,
                Messages.ModuleStructureEventFormRegionsCheck_Excluded_method_names);
    }

    @Override
    protected void check(Object object, ResultAcceptor result, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }

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

        String excludeNamePattern = parameters.getString(PARAMETER_EXCLUDE_METHOD_NAME_PATTERN);
        if (!StringUtils.isEmpty(excludeNamePattern) && isExcludeName(method.getName(), excludeNamePattern))
        {
            return;
        }

        Optional<RegionPreprocessor> region = getTopParentRegion(method);
        if (region.isEmpty())
        {
            return;
        }

        String regionName = region.get().getName();
        String methodName = method.getName();
        Map<CaseInsensitiveString, List<EObject>> eventHandlers = bslEventsService.getEventHandlersContainer(module);
        List<EObject> containers = eventHandlers.get(new CaseInsensitiveString(methodName));
        if (containers == null)
        {
            if (isEventHandlerRegion(scriptVariant, regionName))
            {
                addIssueShouldNotBeInRegion(result, methodName, regionName);
            }
            return;
        }

        check(result, containers, regionName, methodName, scriptVariant, monitor);
    }

    private void check(ResultAcceptor result, List<EObject> containers, String regionName, String methodName,
        ScriptVariant scriptVariant, IProgressMonitor monitor)
    {
        for (EObject obj : containers)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (obj instanceof FormCommandHandlerContainer)
            {
                addIssueCommand(result, regionName, methodName, scriptVariant);
            }
            else if (!(obj instanceof EventHandlerContainer))
            {
                return;
            }
            else
            {
                EventHandlerContainer container = (EventHandlerContainer)obj;
                check(result, container, regionName, methodName, scriptVariant);
            }
        }
    }

    private void check(ResultAcceptor result, EventHandlerContainer container, String regionName, String methodName,
        ScriptVariant scriptVariant)
    {
        Table table = null;
        FormField field = null;
        DecorationExtInfo decoration = null;
        GroupExtInfo group = null;
        Form form = null;
        for (EObject e = container; e != null; e = e.eContainer())
        {
            if (e instanceof Table)
            {
                table = (Table)e;
            }
            else if (e instanceof FormField)
            {
                field = (FormField)e;
            }
            else if (e instanceof DecorationExtInfo)
            {
                decoration = (DecorationExtInfo)e;
            }
            else if (e instanceof GroupExtInfo)
            {
                group = (GroupExtInfo)e;
            }
            else if (e instanceof Form)
            {
                form = (Form)e;
                break;
            }
        }

        if (table != null)
        {
            addIssueTable(result, table.getName(), regionName, methodName, scriptVariant);
        }
        else if (field != null || decoration != null || group != null)
        {
            addIssueItem(result, regionName, methodName, scriptVariant);
        }
        else if (form != null)
        {
            addIssueForm(result, regionName, methodName, scriptVariant);
        }
    }

    private void addIssueCommand(ResultAcceptor result, String regionName, String methodName,
        ScriptVariant scriptVariant)
    {
        if (!isCommandHanlderRegion(regionName, scriptVariant))
        {
            String defRegionName = ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant);
            addIssueShouldBeInRegion(result, methodName, defRegionName);
        }
    }

    private void addIssueTable(ResultAcceptor result, String tableName, String regionName, String methodName,
        ScriptVariant scriptVariant)
    {
        if (!isTableHanlderRegion(scriptVariant, regionName, tableName))
        {
            String defRegionName =
                ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant) + tableName;
            addIssueShouldBeInRegion(result, methodName, defRegionName);
        }
    }

    private void addIssueForm(ResultAcceptor result, String regionName, String methodName, ScriptVariant scriptVariant)
    {
        if (!isFormHanlderRegion(regionName, scriptVariant))
        {
            addIssueShouldBeInRegion(result, methodName,
                ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant));
        }
    }

    private void addIssueItem(ResultAcceptor result, String regionName, String methodName, ScriptVariant scriptVariant)
    {
        if (!isFormHeaderHanlderRegion(regionName, scriptVariant))
        {
            addIssueShouldBeInRegion(result, methodName,
                ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant));
        }
    }

    private void addIssueShouldBeInRegion(ResultAcceptor result, String methodName, String defaultRegionName)
    {
        result.addIssue(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__should_be_placed_in_the_region__1,
            methodName, defaultRegionName), McorePackage.Literals.NAMED_ELEMENT__NAME);
    }

    private void addIssueShouldNotBeInRegion(ResultAcceptor result, String methodName, String regionName)
    {
        result.addIssue(MessageFormat.format(
            Messages.ModuleStructureEventFormRegionsCheck_Event_method__0__can_not_be_placed_in_the_region__1,
            methodName, regionName), McorePackage.Literals.NAMED_ELEMENT__NAME);
    }

    private boolean isCommandHanlderRegion(String regionName, ScriptVariant scriptVariant)
    {
        return ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant).equals(regionName);
    }

    private boolean isTableHanlderRegion(ScriptVariant scriptVariant, String regionName, String tableName)
    {
        return (ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant) + tableName)
            .equals(regionName);
    }

    private boolean isFormHeaderHanlderRegion(String regionName, ScriptVariant scriptVariant)
    {
        return ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant).equals(regionName);
    }

    private boolean isFormHanlderRegion(String regionName, ScriptVariant scriptVariant)
    {
        return ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant).equals(regionName);
    }

    private boolean isEventHandlerRegion(ScriptVariant scriptVariant, String regionName)
    {
        return ModuleStructureSection.FORM_HEADER_ITEMS_EVENT_HANDLERS.getName(scriptVariant).equals(regionName)
            || ModuleStructureSection.FORM_COMMAND_EVENT_HANDLERS.getName(scriptVariant).equals(regionName)
            || ModuleStructureSection.FORM_EVENT_HANDLERS.getName(scriptVariant).equals(regionName)
            || ModuleStructureSection.FORM_TABLE_ITEMS_EVENT_HANDLERS.getName(scriptVariant).indexOf(regionName) != -1;
    }

    private boolean isExcludeName(String name, String excludeNamePattern)
    {
        return StringUtils.isNotEmpty(excludeNamePattern) && name.matches(excludeNamePattern);
    }

}
