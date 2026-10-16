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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks that a context server call is not used in the client event handlers of a form module where it is not allowed.
 *
 * @author Ivan Sergeev
 */
public class FormContextServerCallInEventHandlerCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "form-context-server-call-in-event-handler"; //$NON-NLS-1$

    private static final Set<String> RESTRICTED_EVENT_HANDLERS;

    private static final String EVENT_NAMES = "Event names"; //$NON-NLS-1$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    static
    {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("ПередЗаписью"); //$NON-NLS-1$
        names.add("BeforeWrite"); //$NON-NLS-1$
        names.add("ПослеЗаписи"); //$NON-NLS-1$
        names.add("AfterWrite"); //$NON-NLS-1$
        names.add("ПередЗакрытием"); //$NON-NLS-1$
        names.add("BeforeClose"); //$NON-NLS-1$
        names.add("ПриЗакрытии"); //$NON-NLS-1$
        names.add("OnClose"); //$NON-NLS-1$
        names.add("ПриИзменении"); //$NON-NLS-1$
        names.add("OnChange"); //$NON-NLS-1$
        names.add("ПриАктивизацииСтроки"); //$NON-NLS-1$
        names.add("OnActivateRow"); //$NON-NLS-1$
        names.add("НачалоВыбора"); //$NON-NLS-1$
        names.add("OnStartChoice"); //$NON-NLS-1$
        names.add("ПриОкончанииРедактирования"); //$NON-NLS-1$
        names.add("OnEndEdit"); //$NON-NLS-1$
        names.add("ПроверкаПеретаскивания"); //$NON-NLS-1$
        names.add("OnDragCheck"); //$NON-NLS-1$
        names.add("ПриНачалеРедактирования"); //$NON-NLS-1$
        names.add("OnEditNewLine"); //$NON-NLS-1$
        names.add("ОкончаниеВводаТекста"); //$NON-NLS-1$
        names.add("OnTextEdited"); //$NON-NLS-1$
        names.add("ИзменениеТекстаРедактирования"); //$NON-NLS-1$
        names.add("OnTextChanged"); //$NON-NLS-1$
        names.add("ПриАктивизацииЯчейки"); //$NON-NLS-1$
        names.add("OnActivateCell"); //$NON-NLS-1$

        RESTRICTED_EVENT_HANDLERS = names;
    }

    private static final String DEFAULT_NAMES = String.join(DELIMITER, RESTRICTED_EVENT_HANDLERS);

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormContextServerCallInEventHandlerCheck_Title)
            .description(Messages.FormContextServerCallInEventHandlerCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .extension(new StandardCheckExtension(636, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .parameter(EVENT_NAMES, String.class, DEFAULT_NAMES,
                Messages.FormContextServerCallInEventHandlerCheck_Parameter)
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = (Invocation)object;
        Method method = EcoreUtil2.getContainerOfType(invocation, Method.class);
        if (method == null)
        {
            return;
        }
        String restrictedNames = parameters.getString(EVENT_NAMES);
        if (restrictedNames == null)
        {
            return;
        }
        String[] namesList = restrictedNames.split(DELIMITER);
        if (isRestrictedEventHandler(method, namesList))
        {
            Method callMethod = getLocalMethod(invocation.getMethodAccess());
            if (callMethod == null)
            {
                return;
            }
            if (checkPragmas(callMethod))
            {
                resultAceptor.addIssue(Messages.FormContextServerCallInEventHandlerCheck_Issue, invocation);
            }
        }
    }

    private boolean isRestrictedEventHandler(Method method, String[] namesList)
    {
        String methodName = method.getName();
        for (String name : namesList)
        {
            if (name.equalsIgnoreCase(methodName))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkPragmas(Method callMethod)
    {
        Environments envi = callMethod.getEnvironments();
        if (envi.contains(Environment.SERVER))
        {
            Set<String> symbols = new HashSet<>();
            for (Pragma pragma : callMethod.getPragmas())
            {
                symbols.add(pragma.getSymbol().toLowerCase());
            }
            if (!symbols.contains("насерверебезконтекста") //$NON-NLS-1$
                && !symbols.contains("atservernocontext")) //$NON-NLS-1$
            {
                return true;
            }
        }
        return false;
    }

    private Method getLocalMethod(FeatureAccess featureAccess)
    {
        if (featureAccess instanceof StaticFeatureAccess sfa)
        {
            List<FeatureEntry> featureEntry = sfa.getFeatureEntries();
            for (FeatureEntry entry : featureEntry)
            {
                EObject feature = entry.getFeature();
                if (feature instanceof Method method)
                {
                    return method;
                }
            }
        }
        return null;
    }
}
