/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.core.naming.ITopObjectFqnGenerator;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * Check self reference by name in manager modules.
 *
 * @author Maxim Galios
 * @author Vadim Goncharov
 *
 */
public class ManagerModuleNamedSelfReferenceCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "manager-module-named-self-reference"; //$NON-NLS-1$

    private final IScopeProvider scopeProvider;

    private final IQualifiedNameConverter qualifiedNameConverter;

    private final ITopObjectFqnGenerator topObjectFqnGenerator;

    private static final Map<EReference, EClass> MANAGERS_FOR_MDOBJECT = new ImmutableMap.Builder<EReference, EClass>()
        .put(MdClassPackage.Literals.CONFIGURATION__CATALOGS, MdClassPackage.Literals.CATALOG)
        .put(MdClassPackage.Literals.CONFIGURATION__DOCUMENTS, MdClassPackage.Literals.DOCUMENT)
        .put(MdClassPackage.Literals.CONFIGURATION__DOCUMENT_JOURNALS, MdClassPackage.Literals.DOCUMENT_JOURNAL)
        .put(MdClassPackage.Literals.CONFIGURATION__ENUMS, MdClassPackage.Literals.ENUM)
        .put(MdClassPackage.Literals.CONFIGURATION__REPORTS, MdClassPackage.Literals.REPORT)
        .put(MdClassPackage.Literals.CONFIGURATION__DATA_PROCESSORS, MdClassPackage.Literals.DATA_PROCESSOR)
        .put(MdClassPackage.Literals.CONFIGURATION__CHARTS_OF_CHARACTERISTIC_TYPES,
            MdClassPackage.Literals.CHART_OF_CHARACTERISTIC_TYPES)
        .put(MdClassPackage.Literals.CONFIGURATION__CHARTS_OF_ACCOUNTS, MdClassPackage.Literals.CHART_OF_ACCOUNTS)
        .put(MdClassPackage.Literals.CONFIGURATION__CHARTS_OF_CALCULATION_TYPES,
            MdClassPackage.Literals.CHART_OF_CALCULATION_TYPES)
        .put(MdClassPackage.Literals.CONFIGURATION__INFORMATION_REGISTERS, MdClassPackage.Literals.INFORMATION_REGISTER)
        .put(MdClassPackage.Literals.CONFIGURATION__ACCUMULATION_REGISTERS,
            MdClassPackage.Literals.ACCUMULATION_REGISTER)
        .put(MdClassPackage.Literals.CONFIGURATION__ACCOUNTING_REGISTERS, MdClassPackage.Literals.ACCOUNTING_REGISTER)
        .put(MdClassPackage.Literals.CONFIGURATION__CALCULATION_REGISTERS, MdClassPackage.Literals.CALCULATION_REGISTER)
        .put(MdClassPackage.Literals.CONFIGURATION__BUSINESS_PROCESSES, MdClassPackage.Literals.BUSINESS_PROCESS)
        .put(MdClassPackage.Literals.CONFIGURATION__TASKS, MdClassPackage.Literals.TASK)
        .put(MdClassPackage.Literals.CONFIGURATION__EXCHANGE_PLANS, MdClassPackage.Literals.EXCHANGE_PLAN)
        .put(MdClassPackage.Literals.CONFIGURATION__EXTERNAL_DATA_SOURCES, MdClassPackage.Literals.EXTERNAL_DATA_SOURCE)
        .build();

    @Inject
    public ManagerModuleNamedSelfReferenceCheck(IScopeProvider scopeProvider,
        IQualifiedNameConverter qualifiedNameConverter, ITopObjectFqnGenerator topObjectFqnGenerator)
    {
        this.scopeProvider = scopeProvider;
        this.qualifiedNameConverter = qualifiedNameConverter;
        this.topObjectFqnGenerator = topObjectFqnGenerator;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ManagerModuleNamedSelfReferenceCheck_title)
            .description(Messages.ManagerModuleNamedSelfReferenceCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(467, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.MANAGER_MODULE))
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Expression featureAccessSource = ((DynamicFeatureAccess)object).getSource();
        Module module = EcoreUtil2.getContainerOfType(featureAccessSource, Module.class);

        if (monitor.isCanceled() || !(featureAccessSource instanceof DynamicFeatureAccess))
        {
            return;
        }

        DynamicFeatureAccess source = (DynamicFeatureAccess)featureAccessSource;

        Expression managerTypeExpression = source.getSource();
        if (monitor.isCanceled() || !(managerTypeExpression instanceof StaticFeatureAccess))
        {
            return;
        }

        StaticFeatureAccess managerType = (StaticFeatureAccess)managerTypeExpression;

        EReference eRef = getConfigurationMdObjectRef(managerType);
        if (monitor.isCanceled() || eRef == null)
        {
            return;
        }

        IEObjectDescription objectDesc = getObjectFromScope(source, eRef);
        if (monitor.isCanceled() || objectDesc == null)
        {
            return;
        }

        EObject eObject = objectDesc.getEObjectOrProxy();
        if (!monitor.isCanceled() && eObject.equals(module.getOwner()))
        {
            resultAceptor.addIssue(Messages.ManagerModuleNamedSelfReferenceCheck_issue, source);
        }
    }

    private EReference getConfigurationMdObjectRef(StaticFeatureAccess managerType)
    {
        EReference result = null;
        CaseInsensitiveString managerTypeName = new CaseInsensitiveString(managerType.getName());
        result = LinkPart.MD_OBJECT_MANAGERS.get(managerTypeName);
        if (result == null)
        {
            result = LinkPart.MD_OBJECT_MANAGERS_RU.get(managerTypeName);
        }
        return result;
    }

    private IEObjectDescription getObjectFromScope(DynamicFeatureAccess source, EReference reference)
    {
        IScope scope = scopeProvider.getScope(source, reference);
        String fqn =
            topObjectFqnGenerator.generateStandaloneObjectFqn(MANAGERS_FOR_MDOBJECT.get(reference), source.getName());
        return scope.getSingleElement(qualifiedNameConverter.toQualifiedName(fqn));
    }

}
