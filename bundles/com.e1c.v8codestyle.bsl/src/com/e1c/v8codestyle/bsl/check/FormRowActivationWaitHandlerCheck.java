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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PROCEDURE;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.bsl.model.Procedure;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks that the client event handler OnActivateRow attaches a one-time idle handler. Attaching a one-time idle handler
 * avoids repeated and synchronous server calls while the row is being activated. The call of the connected commands
 * client method that attaches the idle handler itself is also accepted as a compliant action. Handlers that make no
 * calls are ignored.
 *
 * @author Ivan Sergeev
 */
public class FormRowActivationWaitHandlerCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "form-row-activation-wait-handler"; //$NON-NLS-1$

    private static final Set<String> ROW_ACTIVATION_HANDLERS;

    static
    {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("ПриАктивизацииСтроки"); //$NON-NLS-1$
        names.add("OnActivateRow"); //$NON-NLS-1$
        ROW_ACTIVATION_HANDLERS = names;
    }

    private static final Set<String> ATTACH_IDLE_HANDLERS;

    static
    {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("ПодключитьОбработчикОжидания"); //$NON-NLS-1$
        names.add("AttachIdleHandler"); //$NON-NLS-1$
        ATTACH_IDLE_HANDLERS = names;
    }

    private static final Set<String> CONTEXT_SERVER_PRAGMAS;

    static
    {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("НаСервере"); //$NON-NLS-1$
        names.add("AtServer"); //$NON-NLS-1$
        CONTEXT_SERVER_PRAGMAS = names;
    }

    private static final Set<String> COMMANDS_UPDATE_HANDLERS;

    static
    {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("НачатьОбновлениеКоманд"); //$NON-NLS-1$
        names.add("StartCommandUpdate"); //$NON-NLS-1$
        COMMANDS_UPDATE_HANDLERS = names;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormRowActivationWaitHandlerCheck_Title)
            .description(Messages.FormRowActivationWaitHandlerCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(636, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(PROCEDURE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Procedure procedure = (Procedure)object;
        if (monitor.isCanceled())
        {
            return;
        }
        for (String string : ROW_ACTIVATION_HANDLERS)
        {
            if (monitor.isCanceled())
            {
                return;
            }
            if (procedure.getName().toLowerCase().contains(string.toLowerCase()))
            {
                List<Invocation> invocations = EcoreUtil2.getAllContentsOfType(procedure, Invocation.class);
                if (invocations.isEmpty())
                {
                    return;
                }

                boolean hasOneTimeIdleHandler = false;
                for (Invocation invocation : invocations)
                {
                    if (monitor.isCanceled())
                    {
                        return;
                    }
                    if (isOneTimeIdleHandler(invocation) || isCommandsUpdateHandler(invocation))
                    {
                        hasOneTimeIdleHandler = true;
                        break;
                    }
                }
                if (!hasOneTimeIdleHandler)
                {
                    resultAceptor.addIssue(Messages.FormRowActivationWaitHandlerCheck_Issue, procedure,
                        NAMED_ELEMENT__NAME);
                    return;
                }

                for (Invocation invocation : invocations)
                {
                    if (monitor.isCanceled())
                    {
                        return;
                    }
                    Method calleed = getLocalMethod(invocation.getMethodAccess());
                    if (calleed != null && isContextServerMethod(calleed))
                    {
                        resultAceptor.addIssue(Messages.FormRowActivationWaitHandlerCheck_ServerCallIssue, invocation);
                    }
                }
            }
        }
    }

    private boolean isOneTimeIdleHandler(Invocation invocation)
    {
        FeatureAccess featureAccess = invocation.getMethodAccess();
        if (!(featureAccess instanceof StaticFeatureAccess) || !ATTACH_IDLE_HANDLERS.contains(featureAccess.getName()))
        {
            return false;
        }

        List<Expression> params = invocation.getParams();
        if (params.size() < 3)
        {
            return false;
        }

        Expression oneTime = params.get(2);
        if (oneTime instanceof BooleanLiteral booleanLiteral)
        {
            return booleanLiteral.isIsTrue();
        }
        return true;
    }

    private boolean isCommandsUpdateHandler(Invocation invocation)
    {
        if (invocation.getMethodAccess() instanceof DynamicFeatureAccess featureAccess)
        {
            String name = featureAccess.getName();
            return COMMANDS_UPDATE_HANDLERS.contains(name);
        }
        return false;
    }

    private Method getLocalMethod(FeatureAccess access)
    {
        if (!(access instanceof StaticFeatureAccess))
        {
            return null;
        }

        for (FeatureEntry entry : ((StaticFeatureAccess)access).getFeatureEntries())
        {
            EObject feature = entry.getFeature();
            if (feature instanceof Method)
            {
                return (Method)feature;
            }
        }
        return null;
    }

    private boolean isContextServerMethod(Method method)
    {
        for (Pragma pragma : method.getPragmas())
        {
            if (CONTEXT_SERVER_PRAGMAS.contains(pragma.getSymbol()))
            {
                return true;
            }
        }
        return false;
    }
}
