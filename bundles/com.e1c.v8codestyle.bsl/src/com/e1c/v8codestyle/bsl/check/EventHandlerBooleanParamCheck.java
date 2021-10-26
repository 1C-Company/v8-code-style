/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.dt.bsl.contextdef.IBslModuleContextDefService;
import com._1c.g5.v8.dt.bsl.model.BinaryExpression;
import com._1c.g5.v8.dt.bsl.model.BinaryOperation;
import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.resource.BslEventsService;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com._1c.g5.v8.dt.mcore.Event;
import com._1c.g5.v8.dt.mcore.ParamSet;
import com._1c.g5.v8.dt.mcore.Parameter;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Checks that boolean parameter of event handler (or for all method if set) is used correctly to set boolean value.
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 */
public class EventHandlerBooleanParamCheck
    extends BasicCheck
{
    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String CHECK_ID = "event-heandler-boolean-param"; //$NON-NLS-1$

    private static final String PARAM_CHECK_EVENT_ONLY = "checkEventOnly"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_EVENT_ONLY = Boolean.TRUE.toString();

    private static final String PARAM_PARAMS_TO_TRUE = "paramsToTrue"; //$NON-NLS-1$

    private static final Set<String> DEFAULT_PARAMS_TO_TRUE = Set.of("Cancel"); //$NON-NLS-1$

    private static final String PARAM_PARAMS_TO_FALSE = "paramsToFalse"; //$NON-NLS-1$

    private static final Set<String> DEFAULT_PARAMS_TO_FALSE = Set.of("StandardProcessing", "Perform"); //$NON-NLS-1$ //$NON-NLS-2$

    private BslEventsService bslEventsService;

    private IBslModuleContextDefService contextDefService;

    /**
     * Instantiates a new event handler boolean parameter check.
     *
     * @param contextDefService the context definition service, cannot be {@code null}.
     */
    @Inject
    public EventHandlerBooleanParamCheck(IBslModuleContextDefService contextDefService)
    {
        super();

        this.contextDefService = contextDefService;
        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        this.bslEventsService = rsp.get(BslEventsService.class);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.EventHandlerBooleanParamCheck_title)
            .description(Messages.EventHandlerBooleanParamCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .module()
            .checkedObjectType(SIMPLE_STATEMENT)
            .parameter(PARAM_CHECK_EVENT_ONLY, Boolean.class, DEFAULT_CHECK_EVENT_ONLY,
                Messages.EventHandlerBooleanParamCheck_Check_only_in_event_handlers)
            .parameter(PARAM_PARAMS_TO_TRUE, String.class, String.join(DELIMITER, DEFAULT_PARAMS_TO_TRUE),
                Messages.EventHandlerBooleanParamCheck_Prams_to_set_to_True)
            .parameter(PARAM_PARAMS_TO_FALSE, String.class, String.join(DELIMITER, DEFAULT_PARAMS_TO_FALSE),
                Messages.EventHandlerBooleanParamCheck_Prams_to_set_to_False);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        SimpleStatement statement = (SimpleStatement)object;
        Expression left = statement.getLeft();
        Expression right = statement.getRight();
        if (!(left instanceof StaticFeatureAccess))
        {
            return;
        }
        boolean checkEventOnly = parameters.getBoolean(PARAM_CHECK_EVENT_ONLY);
        Set<String> paramsToTrue = getParamsToTrue(parameters);
        Set<String> paramsToFalse = getParamsToFalse(parameters);

        StaticFeatureAccess stat = (StaticFeatureAccess)left;
        String paramName = stat.getName();

        if (stat.getImplicitVariable() != null || stat.getFeatureEntries().isEmpty()
            || !(stat.getFeatureEntries().get(0).getFeature() instanceof FormalParam))
        {
            return;
        }

        FormalParam param = (FormalParam)stat.getFeatureEntries().get(0).getFeature();

        // Fast check by name and boolean literal
        if (right instanceof BooleanLiteral
            && (paramsToTrue.contains(param.getName()) && ((BooleanLiteral)right).isIsTrue()
                || paramsToFalse.contains(param.getName()) && !((BooleanLiteral)right).isIsTrue()))
        {
            return;
        }

        boolean valueToCheckAssignment = paramsToTrue.contains(paramName);
        Parameter eventParameter = getEventBooleanParameter(param, monitor);

        if (eventParameter != null)
        {
            if (paramsToTrue.contains(eventParameter.getNameRu()) || paramsToTrue.contains(eventParameter.getName()))
            {
                valueToCheckAssignment = true;

            }
            else if (paramsToFalse.contains(eventParameter.getNameRu())
                || paramsToFalse.contains(eventParameter.getName()))
            {
                valueToCheckAssignment = false;
            }
            else
            {
                return;
            }
        }
        else if (checkEventOnly || !paramsToTrue.contains(paramName) && !paramsToFalse.contains(paramName))
        {
            return;
        }

        if (monitor.isCanceled())
        {
            return;
        }

        // check right expression
        if (!isCorrectBooleanExpression(right, paramName, valueToCheckAssignment, monitor))
        {
            String message;
            if (valueToCheckAssignment)
            {
                message = MessageFormat.format(Messages.EventHandlerBooleanParamCheck_Parameter_0_should_set_to_True,
                    paramName);
            }
            else
            {
                message = MessageFormat.format(Messages.EventHandlerBooleanParamCheck_Parameter_0_should_set_to_False,
                    paramName);
            }
            resultAceptor.addIssue(message, right);
        }
    }

    private Parameter getEventBooleanParameter(FormalParam param, IProgressMonitor monitor)
    {
        Method method = EcoreUtil2.getContainerOfType(param, Method.class);
        if (monitor.isCanceled() || method == null || method.getFormalParams().isEmpty())
        {
            return null;
        }

        Module module = EcoreUtil2.getContainerOfType(method, Module.class);
        if (monitor.isCanceled() || !isCorrectModule(module))
        {
            return null;
        }

        List<FormalParam> params = method.getFormalParams();
        int index = params.indexOf(param);

        Map<CaseInsensitiveString, Event> eventHandlers = getAllModuleEvents(module);
        CaseInsensitiveString methodName = new CaseInsensitiveString(method.getName());
        Event event = eventHandlers.get(methodName);
        if (event == null && isCorrectModuleForCustomHandlers(module))
        {
            // Get event for Form Item event handlers or common module event subscription
            List<EObject> enventHandlers = bslEventsService.getEventHandlers(module).get(methodName);
            if (enventHandlers != null && !enventHandlers.isEmpty())
            {
                if (index > 0)
                {
                    // shift because Form Item (or event subscription) Event doesn't contains first parameter "Item"
                    --index;
                }

                for (EObject handler : enventHandlers)
                {
                    if (handler instanceof Event)
                    {
                        event = (Event)handler;
                        break;
                    }
                }
            }
        }
        if (monitor.isCanceled() || event == null)
        {
            return null;
        }

        // get actual parameter name in case it renamed, but still boolean
        ParamSet paramSet = event.actualParamSet(params.size());
        if (paramSet != null)
        {
            List<Parameter> eventParameters = paramSet.getParams();
            if (index < eventParameters.size())
            {
                Parameter eventParameter = eventParameters.get(index);
                if (isBooleanParameter(eventParameter))
                {
                    return eventParameter;
                }
            }
        }
        return null;
    }

    private boolean isCorrectBooleanExpression(Expression expr, String paramName, boolean checkState,
        IProgressMonitor monitor)
    {
        if (expr instanceof BinaryExpression)
        {
            BinaryExpression binaryExpression = (BinaryExpression)expr;
            return isCorrectBooleanExpression(binaryExpression, paramName, checkState, monitor);
        }
        else if (expr instanceof BooleanLiteral)
        {
            BooleanLiteral booleanLiteral = (BooleanLiteral)expr;
            return booleanLiteral.isIsTrue() == checkState;
        }
        else if (expr instanceof StaticFeatureAccess)
        {
            return paramName.equalsIgnoreCase(((StaticFeatureAccess)expr).getName());
        }

        return false;
    }

    private boolean isCorrectBooleanExpression(BinaryExpression expr, String paramName, boolean checkState,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return false;
        }

        Expression left = expr.getLeft();
        Expression right = expr.getRight();

        BinaryOperation operation;
        if (checkState)
        {
            operation = BinaryOperation.OR;
        }

        else
        {
            operation = BinaryOperation.AND;
        }

        if (expr.getOperation() == operation)
        {
            return isCorrectBooleanExpression(left, paramName, checkState, monitor)
                || isCorrectBooleanExpression(right, paramName, checkState, monitor);
        }

        return false;
    }

    private boolean isBooleanParameter(Parameter eventParameter)
    {
        for (TypeItem type : eventParameter.getType())
        {
            if (IEObjectTypeNames.BOOLEAN.equals(McoreUtil.getTypeName(type)))
            {
                return true;
            }
        }
        return false;
    }

    private Set<String> getParamsToFalse(ICheckParameters parameters)
    {
        Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String paramString = parameters.getString(PARAM_PARAMS_TO_FALSE);
        Set<String> params = Set.of(paramString.replace(" ", "").split(DELIMITER)); //$NON-NLS-1$ //$NON-NLS-2$
        result.addAll(params);
        return result;
    }

    private Set<String> getParamsToTrue(ICheckParameters parameters)
    {
        Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String paramString = parameters.getString(PARAM_PARAMS_TO_TRUE);
        Set<String> params = Set.of(paramString.replace(" ", "").split(DELIMITER)); //$NON-NLS-1$ //$NON-NLS-2$
        result.addAll(params);
        return result;
    }

    private Map<CaseInsensitiveString, Event> getAllModuleEvents(Module module)
    {

        Map<CaseInsensitiveString, Event> result = new HashMap<>();
        if (module.getModuleType() == ModuleType.FORM_MODULE)
        {
            for (Entry<Event, CaseInsensitiveString> entry : bslEventsService.getAllModuleEvents(module).entrySet())
            {
                result.put(entry.getValue(), entry.getKey());
            }
        }
        else
        {
            List<Event> moduleEvents = contextDefService.getModuleEvents(module);

            for (Event event : moduleEvents)
            {
                result.put(new CaseInsensitiveString(event.getName()), event);
                result.put(new CaseInsensitiveString(event.getNameRu()), event);
            }
        }
        return result;
    }

    private boolean isCorrectModule(Module module)
    {
        ModuleType type = module.getModuleType();
        return type == ModuleType.FORM_MODULE || type == ModuleType.RECORDSET_MODULE
            || type == ModuleType.MANAGER_MODULE || type == ModuleType.OBJECT_MODULE
            || type == ModuleType.COMMAND_MODULE || type == ModuleType.COMMON_MODULE;
    }

    private boolean isCorrectModuleForCustomHandlers(Module module)
    {
        ModuleType type = module.getModuleType();
        return type == ModuleType.FORM_MODULE || type == ModuleType.COMMON_MODULE;
    }

}
