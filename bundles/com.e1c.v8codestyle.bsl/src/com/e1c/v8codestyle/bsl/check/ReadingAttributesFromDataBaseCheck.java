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

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
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
 * Checks reading single object attribute from the database
 *
 * @author Artem Iliukhin
 */
public class ReadingAttributesFromDataBaseCheck
    extends BasicCheck<Object>
{

    private static final String CHECK_ID = "reading-attribute-from-database"; //$NON-NLS-1$

    private static final String PARAMETER_NAME = "allowFieldAccessWithCompositeNonReferenceType"; //$NON-NLS-1$

    private final TypesComputer typesComputer;

    //@formatter:off
    private static final Set<String> EXCLUDED_TYPES = Set.of(
        IEObjectTypeNames.NULL,
        IEObjectTypeNames.STRING,
        IEObjectTypeNames.NUMBER,
        IEObjectTypeNames.BOOLEAN,
        IEObjectTypeNames.DATE,
        IEObjectTypeNames.VALUE_STORAGE,
        IEObjectTypeNames.UUID,
        IEObjectTypeNames.ENUM_REF
        );
    //@formatter:on

    @Inject
    public ReadingAttributesFromDataBaseCheck(TypesComputer typesComputer)
    {
        super();
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
        builder.title(Messages.ReadingAttributesFromDataBaseCheck_Title)
            .description(Messages.ReadingAttributesFromDataBaseCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.PERFORMANCE)
            .extension(new StandardCheckExtension(496, getCheckId(), BslPlugin.PLUGIN_ID))
            .parameter(PARAMETER_NAME, Boolean.class, Boolean.TRUE.toString(),
                Messages.ReadingAttributesFromDataBaseCheck_Message)
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DynamicFeatureAccess dfa = (DynamicFeatureAccess)object;
        if (BslUtil.getInvocation(dfa) != null)
        {
            return;
        }

        Environmental env = EcoreUtil2.getContainerOfType(dfa.getSource(), Environmental.class);
        if (env == null)
        {
            return;
        }

        boolean allowNonRef = parameters.getBoolean(PARAMETER_NAME);
        check(resultAceptor, dfa, monitor, env, allowNonRef);
    }

    private void check(ResultAcceptor resultAceptor, DynamicFeatureAccess dfa, IProgressMonitor monitor,
        Environmental env, boolean allowNonRef)
    {
        Expression source = dfa.getSource();
        if (monitor.isCanceled())
        {
            return;
        }
        List<TypeItem> types = typesComputer.computeTypes(source, env.environments());

        boolean hasRef = false;
        boolean hasNonRef = false;
        for (TypeItem type : types)
        {
            if (type.eIsProxy())
            {
                type = (TypeItem)EcoreUtil.resolve(type, source);
                if (type.eIsProxy())
                {
                    return;
                }
            }

            String typeName = McoreUtil.getTypeName(type);
            if (typeName == null)
            {
                return;
            }

            //TODO issue #1192 to check each type and contained property
            if (!EXCLUDED_TYPES.contains(typeName))
            {
                if (isRefType(type))
                {
                    hasRef = true;
                }
                else
                {
                    hasNonRef = true;
                }
            }

            if (hasRef && hasNonRef || hasRef && !allowNonRef)
            {
                break;
            }
        }

        if (!allowNonRef && hasNonRef && hasRef || hasRef && !hasNonRef)
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ReadingAttributesFromDataBaseCheck_Issue__0, dfa.getName()), dfa);
        }
    }

    private boolean isRefType(TypeItem type)
    {
        switch (McoreUtil.getTypeCategory(type))
        {
        case IEObjectTypeNames.EXCHANGE_PLAN_REF:
        case IEObjectTypeNames.CHART_OF_CHARACTERISTIC_TYPES_REF:
        case IEObjectTypeNames.CHART_OF_ACCOUNTS_REF:
        case IEObjectTypeNames.CHART_OF_CALCULATION_TYPES_REF:
        case IEObjectTypeNames.ACCUMULATION_REGISTER_REF:
        case IEObjectTypeNames.DOCUMENT_REF:
        case IEObjectTypeNames.DOCUMENT_JOURNAL_REF:
        case IEObjectTypeNames.INFORMATION_REGISTER_REF:
        case IEObjectTypeNames.ACCOUNTING_REGISTER_REF:
        case IEObjectTypeNames.CALCULATION_REGISTER_REF:
        case IEObjectTypeNames.CATALOG_REF:
        case IEObjectTypeNames.BUSINESS_PROCESS_REF:
        case IEObjectTypeNames.BUSINESS_PROCESS_ROUTE_POINT_REF:
        case IEObjectTypeNames.TASK_REF:
        case IEObjectTypeNames.EXTERNAL_DATA_SOURCE_TABLE_REF:
        case IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_DIMENSION_TABLE_REF:
        case IEObjectTypeNames.ANY_REF:
            return true;
        default:
            return false;
        }
    }
}
