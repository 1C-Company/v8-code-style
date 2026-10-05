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
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.form.model.EventHandler;
import com._1c.g5.v8.dt.form.model.Form;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * The Check finds the AfterWrite event handler and adds a issue if the handler doesn't contain a call to Notify()
 * 
 * @author Artem Samohvalov
 */
public class NotifyCallInAfterWriteEventCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECKED_EVENT = "AfterWrite"; //$NON-NLS-1$

    private static final String NOTIFY_METHOD_NAME = "Notify"; //$NON-NLS-1$
    private static final String NOTIFY_METHOD_NAME_RU = "Оповестить"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return "notify-call-in-after-write-event"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.NotifyCallInAfterWriteEventCheck_Title)
            .description(Messages.NotifyCallInAfterWriteEventCheck_Description)
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

        if (module.getOwner() instanceof Form form)
        {
            List<Method> checkedHandlers = findCheckedHandlersByForm(form, module);
            for (Method method : checkedHandlers) // loop for all handlers of the AfterWrite event
            {
                if (!hasNotifyCall(method))
                {
                    resultAcceptor.addIssue(Messages.NotifyCallInAfterWriteEventCheck_Issue, method,
                        NAMED_ELEMENT__NAME);
                }
            }
        }
    }

    private List<Method> findCheckedHandlersByForm(Form form, Module module)
    {
        if (form.getExtInfo() == null)
        {
            return List.of();
        }

        List<EventHandler> handlers = form.getExtInfo().getHandlers();
        if (handlers.isEmpty())
        {
            return List.of();
        }

        Set<String> checkedHandlerNames = handlers.stream()
            .filter(handler -> handler.getEvent().getName() != null)
            .filter(handler -> handler.getEvent().getName().equalsIgnoreCase(CHECKED_EVENT))
            .map(EventHandler::getName)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        if (checkedHandlerNames.isEmpty())
        {
            return List.of();
        }

        return module.allMethods()
            .stream()
            .filter(method -> checkedHandlerNames.contains(method.getName().toLowerCase()))
            .toList();
    }

    private boolean hasNotifyCall(Method method)
    {
        TreeIterator<EObject> it = EcoreUtil.getAllContents(method, true);

        while (it.hasNext())
        {
            EObject element = it.next();
            if (element instanceof SimpleStatement simpleStatement
                && simpleStatement.getLeft() instanceof Invocation invocation
                && invocation.getMethodAccess() instanceof StaticFeatureAccess staticAccess
                && (NOTIFY_METHOD_NAME_RU.equalsIgnoreCase(staticAccess.getName())
                    || NOTIFY_METHOD_NAME.equalsIgnoreCase(staticAccess.getName())))
            {
                return true;
            }
        }
        return false;
    }
}
