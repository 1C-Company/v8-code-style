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

import java.text.MessageFormat;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.MethodCallProcessor;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.e1c.v8codestyle.internal.bsl.MethodAccessHelper;

/**
 * Checks that a client-side method calls NotifyChanged() after a server-side method changes an object. 
 * The NotifyChanged() call must be the last statement of the client-side method.
 * 
 * @author Artem Samohvalov
 */
public class NotifyCallAfterObjectChangeCheck
    extends BasicCheck<Object>
{
    private static final String NOTIFY_METHOD_NAME = "notifychanged"; //$NON-NLS-1$
    private static final String NOTIFY_METHOD_NAME_RU = "оповеститьобизменении"; //$NON-NLS-1$

    private static final Set<String> MAIN_OBJECT_NAMES = Set.of("объект", "object"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final Set<String> FORM_SELF_NAMES = Set.of("этаформа", "этотобъект", "thisobject"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final Set<String> OBJECT_CHANGE_METHOD_NAMES =
        Set.of("записать", "write", "удалить", "delete", "установитьпометкуудаления", "setdeletionmark"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    @Override
    public String getCheckId()
    {
        return "notify-call-after-object-change"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.NotifyCallAfterObjectChange_Title)
            .description(Messages.NotifyCallAfterObjectChange_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(558, getCheckId(), BslPlugin.PLUGIN_ID))
            .disable()
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
            for (Method method : module.allMethods())
            {
                if (isMethodExecutedOnClient(method))
                {
                    if (hasNotifyAtTheEnd(method))
                    {
                        continue;
                    }

                    Set<Method> methods = getAllServerMethodCalledBy(method);
                    Optional<Method> optionalMethod = methods.stream().filter(this::isMethodChangedObject).findFirst();
                    if (optionalMethod.isPresent())
                    {
                        String message = MessageFormat.format(Messages.NotifyCallAfterObjectChange_Issue,
                            optionalMethod.get().getName());

                        resultAcceptor.addIssue(message, method, NAMED_ELEMENT__NAME);
                    }
                }
            }
        }
    }

    private boolean hasNotifyAtTheEnd(Method method)
    {
        EList<Statement> statements = method.allStatements();
        if (statements.isEmpty())
        {
            return false;
        }

        // get last statement
        Statement statement = statements.get(statements.size() - 1);

        return statement instanceof SimpleStatement simpleStatement // if simple
            && simpleStatement.getLeft() instanceof Invocation invocation // if methodCall
            && MethodAccessHelper.isNameMatching(invocation, NOTIFY_METHOD_NAME_RU, NOTIFY_METHOD_NAME);
    }

    private boolean isMethodExecutedOnClient(Method method)
    {
        Environments environments = method.environments();
        return environments.containsAny(Environments.ALL_CLIENTS);
    }

    private boolean isMethodExecutedOnServer(Method method)
    {
        Environments environments = method.environments();
        return environments.containsAny(Environments.ALL_SERVERS);
    }

    private Set<Method> getAllServerMethodCalledBy(Method method)
    {
        //@formatter:off
        return method.getCallees()
            .stream()
            .filter(this::isMethodExecutedOnServer)
            .collect(Collectors.toSet());
        //@formatter:on
    }

    private boolean isMethodChangedObject(Method method)
    {
        AtomicBoolean res = new AtomicBoolean(false);
        MethodCallProcessor callProcessor = new MethodCallProcessor(method, (Invocation invocation) -> {
            FeatureAccess methodAccess = invocation.getMethodAccess();
            if (methodAccess == null || methodAccess.getName() == null
                || !OBJECT_CHANGE_METHOD_NAMES.contains(methodAccess.getName().toLowerCase()))
            {
                return;
            }

            // common method call: Write()
            if (methodAccess instanceof StaticFeatureAccess)
            {
                res.set(true);
                return;
            }

            if (!(methodAccess instanceof DynamicFeatureAccess dynamicAccess))
            {
                return;
            }

            Expression source = dynamicAccess.getSource();

            // Object.Write(), Объект.Записать()
            if (source instanceof StaticFeatureAccess sourceAccess && sourceAccess.getName() != null
                && MAIN_OBJECT_NAMES.contains(sourceAccess.getName().toLowerCase()))
            {
                res.set(true);
                return;
            }

            // ThisObject.Object.Write(), ЭтотОбъект.Объект.Записать(), ЭтаФорма.Объект.Записать()
            if (source instanceof DynamicFeatureAccess sourceAccess && sourceAccess.getName() != null
                && MAIN_OBJECT_NAMES.contains(sourceAccess.getName().toLowerCase())
                && sourceAccess.getSource() instanceof StaticFeatureAccess selfAccess && selfAccess.getName() != null
                && FORM_SELF_NAMES.contains(selfAccess.getName().toLowerCase()))
            {
                res.set(true);
            }
        });

        callProcessor.process();

        return res.get();
    }

}
