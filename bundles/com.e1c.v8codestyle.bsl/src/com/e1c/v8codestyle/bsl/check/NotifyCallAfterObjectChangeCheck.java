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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
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
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks that a client-side method calls Notify() after a server-side method changes an object. 
 * The Notify() call must be the last statement of the client-side method.
 * 
 * @author Artem Samohvalov
 */
public class NotifyCallAfterObjectChangeCheck
    extends AbstractModuleStructureCheck
{
    private static final Set<String> NOTIFI_METHOD_NAMES =
        Set.of("notify", "оповестить", "notifychanged", "оповеститьобизменении"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

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
                    var optionalMethod = methods.stream().filter(this::isMethodChangedObject).findFirst();
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
            && invocation.getMethodAccess() instanceof StaticFeatureAccess staticAccess // if static method call
            && NOTIFI_METHOD_NAMES.contains(staticAccess.getName().toLowerCase()); // if name - notify
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
        TreeIterator<EObject> it = EcoreUtil.getAllContents(method, true);

        Set<Method> methods = new HashSet<>();
        while (it.hasNext())
        {
            EObject object = it.next();

            if (object instanceof Invocation invocation)
            {
                var optionalMethod = getMethodByInvocation(invocation);
                if (optionalMethod.isPresent() && isMethodExecutedOnServer(optionalMethod.get()))
                {
                    methods.add(optionalMethod.get());
                }
            }
        }

        return methods;
    }

    private Optional<Method> getMethodByInvocation(Invocation invocation)
    {
        if (invocation.getMethodAccess() instanceof StaticFeatureAccess staticAccess
            && !staticAccess.getFeatureEntries().isEmpty())
        {
            FeatureEntry entry = staticAccess.getFeatureEntries().get(0);
            EObject object = entry.getFeature();
            if (object instanceof Method method)
            {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    private boolean isMethodChangedObject(Method method)
    {
        TreeIterator<EObject> it = EcoreUtil.getAllContents(method, true);

        while (it.hasNext())
        {
            EObject object = it.next();

            if (object instanceof Invocation invocation
                && invocation.getMethodAccess() instanceof StaticFeatureAccess staticAccsess
                && OBJECT_CHANGE_METHOD_NAMES.contains(staticAccsess.getName().toLowerCase()))
            {
                return true;
            }
        }

        return false;
    }
}
