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
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
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
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks the region of event handlers for methods related only to handlers.
 *
 * @author Artem Iliukhin
 *
 */
public class ModuleStructureEventRegionsCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "module-structure-event-regions"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public ModuleStructureEventRegionsCheck(IV8ProjectManager v8ProjectManager)
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
        builder.title(Messages.ModuleStructureEventRegionsCheck_Title)
            .description(Messages.ModuleStructureEventRegionsCheck_Description)
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

        Module module = EcoreUtil2.getContainerOfType(method, Module.class);
        if (module == null)
        {
            return;
        }

        ModuleType moduleType = module.getModuleType();
        if (ModuleType.FORM_MODULE.equals(moduleType))
        {
            return;
        }

        Optional<RegionPreprocessor> region = getUpperRegion(method);
        if (region.isEmpty())
        {
            return;
        }

        String name = region.get().getName();
        String eventHandlersName = ModuleStructureSection.EVENT_HANDLERS.getName(scriptVariant);
        if (eventHandlersName.equals(name) && !method.isEvent())
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleStructureEventRegionsCheck_Only_event_methods__0, name),
                McorePackage.Literals.NAMED_ELEMENT__NAME);
            return;
        }

        if (!eventHandlersName.equals(name) && method.isEvent())
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleStructureEventRegionsCheck_Event_handler__0__not_region__1,
                method.getName(), eventHandlersName), McorePackage.Literals.NAMED_ELEMENT__NAME);
        }
    }

}
