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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * The check finds invocation {@code object.SetAction("Event", "Attachable_HandlerName");} and validate that handler
 * name matches pattern for attachable procedures.
 * Form modules should be excluded because local methods must be found by direct reference.
 *
 * @author Dmitriy Marmyshev
 */
public class AttachableEventHandlerNameCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "module-attachable-event-handler-name"; //$NON-NLS-1$

    private static final String METHOD_NAME = "SetAction"; //$NON-NLS-1$

    private static final String METHOD_NAME_RU = "УстановитьДействие"; //$NON-NLS-1$

    private static final String DEFAULT_PARAM_ACTION_PATTERN = "^(?U)(Подключаемый|Attachable)_.*$"; //$NON-NLS-1$

    private static final String PARAM_ACTION_PATTERN = "actionNamePattern"; //$NON-NLS-1$

    //@formatter:off
    private static final Set<String> SET_ACTION_TYPES = Set.of(
        IEObjectTypeNames.CLIENT_APPLICATION_FORM,
        IEObjectTypeNames.MANAGED_FORM,
        IEObjectTypeNames.FORM_FIELD,
        IEObjectTypeNames.FORM_GROUP,
        IEObjectTypeNames.FORM_TABLE,
        IEObjectTypeNames.FORM_DECORATION);
    //@formatter:on

    private final TypesComputer typesComputer;

    /**
     * Instantiates a new attachable event handler name check.
     *
     * @param typesComputer the types computer, cannot be {@code null}.
     */
    @Inject
    public AttachableEventHandlerNameCheck(TypesComputer typesComputer)
    {
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
        builder.title(Messages.AttachableEventHandlerNameCheck_Title)
            .description(Messages.AttachableEventHandlerNameCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(492, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.excludeTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(INVOCATION)
            .parameter(PARAM_ACTION_PATTERN, String.class, DEFAULT_PARAM_ACTION_PATTERN,
                Messages.AttachableEventHandlerNameCheck_Event_handler_name_pattern);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation inv = (Invocation)object;
        FeatureAccess method = inv.getMethodAccess();
        String methodName = method.getName();
        if (methodName == null)
        {
            return;
        }

        String actionPattern = parameters.getString(PARAM_ACTION_PATTERN);
        if (!(method instanceof DynamicFeatureAccess) || StringUtils.isEmpty(actionPattern)
            || inv.getParams().size() != 2 || !(inv.getParams().get(1) instanceof StringLiteral)
            || !(METHOD_NAME_RU.equalsIgnoreCase(methodName) || METHOD_NAME.equalsIgnoreCase(methodName)))
        {
            return;
        }

        Expression parameter = inv.getParams().get(1);
        String content = getStringContent(parameter);
        if (content != null && !content.matches(actionPattern)
            && isExpectedTypes(((DynamicFeatureAccess)method).getSource()))
        {
            String message = MessageFormat.format(Messages.AttachableEventHandlerNameCheck_Message, methodName);
            resultAceptor.addIssue(message, parameter);
        }
    }

    private boolean isExpectedTypes(Expression object)
    {
        Environmental env = EcoreUtil2.getContainerOfType(object, Environmental.class);
        List<TypeItem> types = typesComputer.computeTypes(object, env.environments());
        return types.stream().map(McoreUtil::getTypeName).filter(Objects::nonNull).anyMatch(SET_ACTION_TYPES::contains);
    }

    private String getStringContent(Expression parameter)
    {
        // TODO #1187 get string from content computer in future, for now hard-coded StringLiteral
        StringLiteral literal = (StringLiteral)parameter;
        return String.join(StringUtils.EMPTY, literal.lines(true));
    }

}
