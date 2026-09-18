/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.resource.BslEventsService;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.mcore.Event;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * The Check finds the AfterWrite event handler and adds a issue if the handler doesn't contain a call to Notify()
 * 
 * @author Artem Samohvalov
 */
public class ObjectChangeNotifyCallCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECKED_EVENT = "AfterWrite"; //$NON-NLS-1$

    private static final String NOTIFI_METHOD_NAME = "Notify"; //$NON-NLS-1$
    private static final String NOTIFI_METHOD_NAME_RU = "Оповестить"; //$NON-NLS-1$

    private final BslEventsService bslEventsService;

    @Inject
    public ObjectChangeNotifyCallCheck(BslEventsService bslEventsService)
    {
        this.bslEventsService = bslEventsService;
    }

    @Override
    public String getCheckId()
    {
        return "object-change-notifi-call"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ObjectChangeNotifiCallCheck_Title)
            .description(Messages.ObjectChangeNotifiCallCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(558, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;

        if (module.getOwner() instanceof Form)
        {
            var handlerList = findCheckedHandlersByModule(module);
            for (Method method : handlerList) // loop for all handlers of the AfterWrite event
            {
                if (!hasNotifiCall(method))
                {
                    resultAcceptor.addIssue(Messages.ObjectChangeNotifiCallCheck_InEventHandlerIssue, method,
                        NAMED_ELEMENT__NAME);
                }
            }
        }
    }

    private List<Method> findCheckedHandlersByModule(Module module)
    {
        var eventMap = bslEventsService.getEventHandlers(module);
        var methodList = module.allMethods();

        if (eventMap.isEmpty() || methodList.isEmpty())
        {
            return List.of();
        }

        List<String> methodNames = eventMap.entrySet()
            .stream()
            .filter(entry -> entry.getValue()
                .stream()
                .anyMatch(object -> object instanceof Event event && CHECKED_EVENT.equalsIgnoreCase(event.getName())))
            .map(entry -> entry.getKey().getString())
            .toList();

        return methodList.stream()
            .filter(method -> methodNames.stream().anyMatch(name -> name.equalsIgnoreCase(method.getName())))
            .toList();
    }

    private boolean hasNotifiCall(Method method)
    {
        TreeIterator<EObject> it = EcoreUtil.getAllContents(method, true);

        while (it.hasNext())
        {
            EObject element = it.next();
            if (element instanceof SimpleStatement simpleStatement
                && simpleStatement.getLeft() instanceof Invocation invocation && invocation.getMethodAccess() != null
                && (NOTIFI_METHOD_NAME_RU.equalsIgnoreCase(invocation.getMethodAccess().getName())
                    || NOTIFI_METHOD_NAME.equalsIgnoreCase(invocation.getMethodAccess().getName())))
            {
                return true;
            }
        }
        return false;
    }
}
