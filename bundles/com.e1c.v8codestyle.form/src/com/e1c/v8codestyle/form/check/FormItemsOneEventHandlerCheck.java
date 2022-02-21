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
 *     Manaev Konstantin - issue #855
 *******************************************************************************/

package com.e1c.v8codestyle.form.check;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.form.model.EventHandler;
import com._1c.g5.v8.dt.form.model.ExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.FormItemContainer;
import com._1c.g5.v8.dt.form.model.FormPackage;
import com._1c.g5.v8.dt.form.service.FormItemInformationService;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

/**
 * The check for the {@link Form} that each handler is assigned to a single event
 *
 * @author Manaev Konstantin
 */
public class FormItemsOneEventHandlerCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-items-one-event-handler"; //$NON-NLS-1$
    private static final FormItemInformationService formItemInformationService = new FormItemInformationService();
    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public FormItemsOneEventHandlerCheck(IV8ProjectManager v8ProjectManager)
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
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof Form && object instanceof EObject)
        {
            IV8Project project = v8ProjectManager.getProject((EObject)object);
            ScriptVariant variant = project == null ? ScriptVariant.ENGLISH : project.getScriptVariant();
            Map<String, String> handlers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            checkEObjectEventHandlers((EObject)object, handlers, variant, resultAceptor, parameters, monitor);
        }
    }

    private void checkEObjectEventHandlers(EObject object, Map<String, String> handlers, ScriptVariant variant,
        ResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {

        if (monitor.isCanceled() || object == null)
        {
            return;
        }

        checkHandlersList(object, formItemInformationService.getEventHandlers(object), handlers, variant, resultAceptor,
            parameters, monitor);

        ExtInfo extInfo = formItemInformationService.getExtensionInfo(object);
        if (extInfo != null)
        {
            checkHandlersList(object, formItemInformationService.getEventHandlers(extInfo), handlers, variant,
                resultAceptor, parameters, monitor);
        }

        if (object instanceof FormItemContainer)
        {
            for (Iterator<FormItem> itemIterator = ((FormItemContainer)object).getItems().iterator(); itemIterator
                .hasNext();)
            {
                checkEObjectEventHandlers(itemIterator.next(), handlers, variant, resultAceptor, parameters, monitor);
            }
        }

    }

    private void checkHandlersList(EObject item, List<EventHandler> eventHandlers, Map<String, String> handlers,
        ScriptVariant variant, ResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        String itemAsString = itemName(item);
        for (Iterator<EventHandler> eventIterator = eventHandlers.iterator(); eventIterator.hasNext();)
        {
            if (monitor.isCanceled())
            {
                return;
            }
            EventHandler event = eventIterator.next();
            String handlerName =
                variant == ScriptVariant.ENGLISH ? event.getEvent().getName() : event.getEvent().getNameRu();
            if (handlers.containsKey(event.getName()))
            {
                resultAceptor.addIssue(MessageFormat.format(
                    Messages.FormItemsOneEventHandlerCheck_the_handler_is_already_assigned_to_event, handlerName,
                    handlers.get(event.getName())), event, FormPackage.Literals.EVENT_HANDLER__NAME);
            }
            else
            {
                handlers.put(event.getName(), MessageFormat
                    .format(Messages.FormItemsOneEventHandlerCheck_itemName_dot_eventName, itemAsString, handlerName));
            }
        }
    }

    private String itemName(EObject object)
    {
        if (object instanceof Form)
        {
            return ((Form)object).getMdForm().getName();
        }
        else if (object instanceof FormItem)
        {
            return ((FormItem)object).getName();
        }
        else
        {
            return "undefined"; //$NON-NLS-1$
        }
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormItemsOneEventHandlerCheck_title)
            .description(Messages.FormItemsOneEventHandlerCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.WARNING)
            .extension(new EventHandlerChangeExtension())
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FormPackage.Literals.FORM)
            .containment(FormPackage.Literals.EVENT_HANDLER_CONTAINER);
        builder.topObject(FormPackage.Literals.FORM).containment(FormPackage.Literals.EVENT_HANDLER);
    }

}
