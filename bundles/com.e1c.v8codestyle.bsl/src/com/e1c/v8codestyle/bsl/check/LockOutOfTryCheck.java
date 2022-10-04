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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;

import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.contextdef.IBslModuleContextDefService;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.resource.BslEventsService;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.Event;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks for initialization of the data lock. If the creation of a lock is found, the call of the Lock() method is
 * checked, and the call must be in a try.
 *
 * @author Artem Iliukhin
 */
public final class LockOutOfTryCheck
    extends BasicCheck
{

    private static final List<String> EXLUDED_EVENTS =
        List.of("BeforeDelete", "BeforeWrite", "OnWrite", "BeforeDelete", "OnWriteAtServer", "Posting"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    private static final String CHECK_ID = "lock-out-of-try"; //$NON-NLS-1$
    private static final String NAME_DATA_LOCK = "DataLock"; //$NON-NLS-1$
    private static final String NAME_LOCK = "Lock"; //$NON-NLS-1$
    private static final String NAME_LOCK_RU = "Заблокировать"; //$NON-NLS-1$
    private final TypesComputer typesComputer;
    private BslEventsService bslEventsService;
    private IBslModuleContextDefService contextDefService;

    /**
     * Instantiates a new lock out of try check.
     *
     * @param contextDefService the context def service, cannot be {@code null}
     * @param typesComputer the types computer service, cannot be {@code null}
     * @param bslEventsService the BSL events service, cannot be {@code null}
     */
    @Inject
    public LockOutOfTryCheck(IBslModuleContextDefService contextDefService, TypesComputer typesComputer,
        BslEventsService bslEventsService)
    {
        super();

        this.contextDefService = contextDefService;
        this.bslEventsService = bslEventsService;
        this.typesComputer = typesComputer;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.LockOutOfTry_Lock_out_of_try)
            .description(Messages.LockOutOfTry_Checks_for_init_of_the_data_lock)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(499, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DynamicFeatureAccess dynamicFeatureAccess = (DynamicFeatureAccess)object;

        String name = dynamicFeatureAccess.getName();
        if (BslUtil.getInvocation(dynamicFeatureAccess) == null
            || !NAME_LOCK.equalsIgnoreCase(name) && !NAME_LOCK_RU.equalsIgnoreCase(name))
        {
            return;
        }

        Expression source = dynamicFeatureAccess.getSource();
        Environmental env = EcoreUtil2.getContainerOfType(source, Environmental.class);
        if (Objects.isNull(env))
        {
            return;
        }

        Method method = EcoreUtil2.getContainerOfType(source, Method.class);
        if (monitor.isCanceled() || method == null)
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(method, Module.class);
        if (monitor.isCanceled())
        {
            return;
        }

        if (isExcluded(module, method))
        {
            return;
        }

        List<TypeItem> types = typesComputer.computeTypes(source, env.environments());
        for (TypeItem type : types)
        {
            if (NAME_DATA_LOCK.equals(McoreUtil.getTypeName(type))
                && Objects.isNull(EcoreUtil2.getContainerOfType(source, TryExceptStatement.class)))
            {
                resultAceptor.addIssue(Messages.LockOutOfTry_Method_lock_out_of_try, object);
            }
        }
    }

    private boolean isExcluded(Module module, Method method)
    {
        CaseInsensitiveString methodName = new CaseInsensitiveString(method.getName());
        if (module.getModuleType() == ModuleType.FORM_MODULE)
        {
            List<EObject> eventHandlers = bslEventsService.getEventHandlers(module).get(methodName);
            if (!Objects.isNull(eventHandlers) && !eventHandlers.isEmpty())
            {
                for (EObject handler : eventHandlers)
                {
                    if (handler instanceof Event && (EXLUDED_EVENTS.contains(((Event)handler).getName())))
                    {
                        return true;
                    }
                }
            }
        }
        else if (module.getModuleType() == ModuleType.OBJECT_MODULE)
        {
            List<Event> moduleEvents = contextDefService.getModuleEvents(module);
            for (Event event : moduleEvents)
            {
                if (EXLUDED_EVENTS.contains(event.getName()))
                {
                    CaseInsensitiveString name = new CaseInsensitiveString(event.getName());
                    CaseInsensitiveString nameRu = new CaseInsensitiveString(event.getNameRu());
                    if (name.equals(methodName) || nameRu.equals(methodName))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
