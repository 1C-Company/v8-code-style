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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.common.Symbols;
import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.resource.BslEventsService;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com._1c.g5.v8.dt.mcore.Event;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks that the client event handler OnActivateRow attaches a one-time idle handler and performs no form rebuilding
 * actions directly. Context server calls and changes of form item properties rebuild the form, so they have to be placed
 * into the one-time idle handler, otherwise they are executed synchronously on every row activation. The call of the
 * connected commands client method that attaches the idle handler itself is also accepted as a compliant action.
 * Handlers that make no calls are ignored.
 *
 * @author Ivan Sergeev
 */
public class FormRowActivationWaitHandlerCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "form-row-activation-wait-handler"; //$NON-NLS-1$

    private static final Set<String> ROW_ACTIVATION_EVENTS = newNames("ПриАктивизацииСтроки", //$NON-NLS-1$
        "OnActivateRow"); //$NON-NLS-1$

    private static final Set<String> ATTACH_IDLE_HANDLERS = newNames("ПодключитьОбработчикОжидания", //$NON-NLS-1$
        "AttachIdleHandler"); //$NON-NLS-1$

    private static final Set<String> COMMANDS_UPDATE_HANDLERS = newNames("НачатьОбновлениеКоманд", //$NON-NLS-1$
        "StartCommandUpdate"); //$NON-NLS-1$

    private static final Set<String> ATTACHABLE_COMMANDS_CLIENT = newNames("ПодключаемыеКомандыКлиент", //$NON-NLS-1$
        "AttachableCommandsClient"); //$NON-NLS-1$

    private static final Set<String> FORM_ITEMS = newNames("Элементы", //$NON-NLS-1$
        "Items"); //$NON-NLS-1$

    private static final Set<String> SELF_REFERENCES = newNames("ЭтотОбъект", //$NON-NLS-1$
        "ThisObject", //$NON-NLS-1$
        "ЭтаФорма", //$NON-NLS-1$
        "ThisForm"); //$NON-NLS-1$

    private static final Set<String> NON_REBUILDING_PROPERTIES = newNames("ТекущаяСтрока", //$NON-NLS-1$
        "CurrentRow", //$NON-NLS-1$
        "ТекущийЭлемент", //$NON-NLS-1$
        "CurrentItem", //$NON-NLS-1$
        "ТекущиеДанные", //$NON-NLS-1$
        "CurrentData"); //$NON-NLS-1$

    private static final String INDEX_SEGMENT = "[]"; //$NON-NLS-1$

    private static Set<String> newNames(String... names)
    {
        Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Collections.addAll(result, names);
        return result;
    }

    private final BslEventsService bslEventsService;

    /**
     * Instantiates a new form row activation wait handler check.
     *
     * @param bslEventsService the BSL events service, cannot be {@code null}.
     */
    @Inject
    public FormRowActivationWaitHandlerCheck(BslEventsService bslEventsService)
    {
        super();
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
        builder.title(Messages.FormRowActivationWaitHandlerCheck_Title)
            .description(Messages.FormRowActivationWaitHandlerCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(636, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module module = (Module)object;
        if (module == null)
        {
            return;
        }
        Map<CaseInsensitiveString, List<EObject>> eventHandlers = bslEventsService.getEventHandlers(module);
        if (eventHandlers == null)
        {
            return;
        }
        for (Method method : module.allMethods())
        {
            if (monitor.isCanceled())
            {
                return;
            }
            if (isRowActivationHandler(eventHandlers, method))
            {
                checkHandler(method, resultAceptor, monitor);
            }
        }
    }

    private void checkHandler(Method handler, ResultAcceptor resultAceptor, IProgressMonitor monitor)
    {
        Invocation notOneTimeIdleHandler = null;
        Invocation serverCall = null;
        for (Invocation invocation : EcoreUtil2.getAllContentsOfType(handler, Invocation.class))
        {
            if (monitor.isCanceled())
            {
                return;
            }
            if (isIdleHandler(invocation))
            {
                if (notOneTimeIdleHandler == null && !isOneTimeIdleHandler(invocation))
                {
                    notOneTimeIdleHandler = invocation;
                }
                continue;
            }
            if (serverCall != null || isCommandsUpdateHandler(invocation))
            {
                continue;
            }
            Method callee = getLocalMethod(invocation.getMethodAccess());
            if (callee != null && isContextServerMethod(callee))
            {
                serverCall = invocation;
            }
        }

        if (monitor.isCanceled())
        {
            return;
        }
        if (notOneTimeIdleHandler != null)
        {
            resultAceptor.addIssue(Messages.FormRowActivationWaitHandlerCheck_Issue, notOneTimeIdleHandler);
        }
        if (serverCall != null)
        {
            resultAceptor.addIssue(Messages.FormRowActivationWaitHandlerCheck_ServerCallIssue, serverCall);
        }
        SimpleStatement formItemChange = findFormItemChange(handler, monitor);
        if (formItemChange != null)
        {
            resultAceptor.addIssue(Messages.FormRowActivationWaitHandlerCheck_ItemChangeIssue, formItemChange);
        }
    }

    private boolean isRowActivationHandler(Map<CaseInsensitiveString, List<EObject>> eventHandlers, Method method)
    {
        String methodName = method.getName();
        if (methodName == null)
        {
            return false;
        }

        List<EObject> events = eventHandlers.get(new CaseInsensitiveString(methodName));
        if (events == null)
        {
            return false;
        }

        for (EObject event : events)
        {
            if (event instanceof Event rowActivationEvent && isRowActivationEvent(rowActivationEvent))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isRowActivationEvent(Event event)
    {
        String name = event.getName();
        if (name != null && ROW_ACTIVATION_EVENTS.contains(name))
        {
            return true;
        }
        String nameRu = event.getNameRu();
        return nameRu != null && ROW_ACTIVATION_EVENTS.contains(nameRu);
    }

    private SimpleStatement findFormItemChange(Method handler, IProgressMonitor monitor)
    {
        for (SimpleStatement statement : EcoreUtil2.getAllContentsOfType(handler, SimpleStatement.class))
        {
            if (monitor.isCanceled())
            {
                return null;
            }
            if (isFormItemPropertyAccess(statement.getLeft()))
            {
                return statement;
            }
        }
        return null;
    }

    private boolean isFormItemPropertyAccess(Expression expression)
    {
        List<String> chain = getAccessChain(expression);
        int itemsIndex = getItemsIndex(chain);
        // Items, <Item>, <Property>
        if (itemsIndex < 0 || chain.size() < itemsIndex + 3)
        {
            return false;
        }
        return isRebuildingProperty(chain.get(itemsIndex + 2));
    }

    private int getItemsIndex(List<String> chain)
    {
        if (!chain.isEmpty() && FORM_ITEMS.contains(chain.get(0)))
        {
            return 0;
        }
        if (chain.size() > 1 && SELF_REFERENCES.contains(chain.get(0)) && FORM_ITEMS.contains(chain.get(1)))
        {
            return 1;
        }
        return -1;
    }

    private boolean isRebuildingProperty(String propertyName)
    {
        return !NON_REBUILDING_PROPERTIES.contains(propertyName);
    }

    private List<String> getAccessChain(Expression expression)
    {
        List<String> chain = new ArrayList<>();
        Expression current = expression;
        while (true)
        {
            if (current instanceof DynamicFeatureAccess dynamicAccess)
            {
                String name = dynamicAccess.getName();
                if (name == null)
                {
                    return List.of();
                }
                chain.add(0, name);
                current = dynamicAccess.getSource();
            }
            else if (current instanceof IndexAccess indexAccess)
            {
                chain.add(0, INDEX_SEGMENT);
                current = indexAccess.getSource();
            }
            else
            {
                break;
            }
        }
        if (current instanceof StaticFeatureAccess staticAccess && staticAccess.getName() != null)
        {
            chain.add(0, staticAccess.getName());
            return chain;
        }
        return List.of();
    }

    private boolean isIdleHandler(Invocation invocation)
    {
        FeatureAccess access = invocation.getMethodAccess();
        return access instanceof StaticFeatureAccess && isFeatureNamed(access, ATTACH_IDLE_HANDLERS);
    }

    private boolean isCommandsUpdateHandler(Invocation invocation)
    {
        FeatureAccess access = invocation.getMethodAccess();
        if (!(access instanceof DynamicFeatureAccess dynamicAccess)
            || !isFeatureNamed(dynamicAccess, COMMANDS_UPDATE_HANDLERS))
        {
            return false;
        }
        return dynamicAccess.getSource() instanceof StaticFeatureAccess source
            && isFeatureNamed(source, ATTACHABLE_COMMANDS_CLIENT);
    }

    private boolean isFeatureNamed(FeatureAccess access, Set<String> names)
    {
        if (access == null)
        {
            return false;
        }
        String name = access.getName();
        return name != null && names.contains(name);
    }

    private boolean isOneTimeIdleHandler(Invocation invocation)
    {
        List<Expression> params = invocation.getParams();
        if (params.size() < 3)
        {
            return false;
        }
        Expression oneTime = params.get(2);
        return !(oneTime instanceof BooleanLiteral booleanLiteral) || booleanLiteral.isIsTrue();
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
        Environments envi = method.environments();
        if (!envi.contains(Environment.SERVER) || envi.contains(Environment.CLIENT))
        {
            return false;
        }
        for (Pragma pragma : method.getPragmas())
        {
            String symbol = pragma.getSymbol();
            if (Symbols.AT_SERVER_NO_CONTEXT_RUS.equalsIgnoreCase(symbol)
                || Symbols.AT_SERVER_NO_CONTEXT_INTNL.equalsIgnoreCase(symbol))
            {
                return false;
            }
        }
        return true;
    }
}