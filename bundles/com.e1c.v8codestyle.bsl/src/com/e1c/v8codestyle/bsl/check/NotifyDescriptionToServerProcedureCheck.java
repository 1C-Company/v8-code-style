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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.OPERATOR_STYLE_CREATOR;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STRING_LITERAL__LINES;

import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.model.BslFactory;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.ImplicitVariable;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.MethodsScopeSpec;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.ContextDef;
import com._1c.g5.v8.dt.mcore.DerivedProperty;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.Property;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check that notify description procedure is exist and available at the client.
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 */
public class NotifyDescriptionToServerProcedureCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "notify-description-to-server-procedure"; //$NON-NLS-1$

    private static final String THIS_OBJECT = "ThisObject"; //$NON-NLS-1$

    private static final String THIS_OBJECT_RU = "ЭтотОбъект"; //$NON-NLS-1$

    private static final String THIS_FORM = "ThisForm"; //$NON-NLS-1$

    private static final String THIS_FORM_RU = "ЭтаФорма"; //$NON-NLS-1$

    private static final String NOTIFICATION_OLD = "NotifyDescription"; //$NON-NLS-1$
    private static final String NOTIFICATION = "CallbackDescription"; //$NON-NLS-1$

    private final DynamicFeatureAccessComputer dynamicFeatureAccessComputer;

    private final IScopeProvider scopeProvider;

    private final TypesComputer typesComputer;

    /**
     * Instantiates a new notify description to server procedure check.
     *
     * @param dynamicFeatureAccessComputer the dynamic feature access computer service, cannot be {@code null}
     * @param scopeProvider provides actual local methods, cannot be {@code null}
     * @param typesComputer for computing types by Built-In language model elements, cannot be {@code null}
     */
    @Inject
    public NotifyDescriptionToServerProcedureCheck(DynamicFeatureAccessComputer dynamicFeatureAccessComputer,
        IScopeProvider scopeProvider, TypesComputer typesComputer)
    {
        super();
        this.dynamicFeatureAccessComputer = dynamicFeatureAccessComputer;
        this.scopeProvider = scopeProvider;
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
        builder.title(Messages.NotifyDescriptionToServerProcedureCheck_title)
            .description(Messages.NotifyDescriptionToServerProcedureCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(OPERATOR_STYLE_CREATOR);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        OperatorStyleCreator osc = (OperatorStyleCreator)object;
        String operatorName = McoreUtil.getTypeName(osc.getType());

        if ((!NOTIFICATION_OLD.equals(operatorName) && !NOTIFICATION.equals(operatorName)) || osc.getParams().isEmpty()
            || !(osc.getParams().get(0) instanceof StringLiteral))
        {
            return;
        }

        StringLiteral param = (StringLiteral)osc.getParams().get(0);
        String methodName = getCalledProcedureName(param);
        if (monitor.isCanceled() || StringUtils.isBlank(methodName))
        {
            return;
        }

        Collection<Environmental> methods = getMethods(methodName, osc, monitor);
        if (methods.isEmpty())
        {
            resultAceptor.addIssue(
                Messages.NotifyDescriptionToServerProcedureCheck_Notify_description_procedure_should_be_export, param,
                STRING_LITERAL__LINES);
        }
        else
        {
            for (Environmental method : methods)
            {
                Environments calleeEnv = method.environments();
                if (calleeEnv.containsAny(Environments.MNG_CLIENTS))
                {
                    return;
                }
            }

            resultAceptor.addIssue(
                Messages.NotifyDescriptionToServerProcedureCheck_Notify_description_to_Server_procedure, param,
                STRING_LITERAL__LINES);
        }

    }

    private Collection<Environmental> getMethods(String methodName, OperatorStyleCreator osc, IProgressMonitor monitor)
    {
        List<Expression> params = osc.getParams();
        if (params.size() > 1)
        {
            Expression moduleParam = params.get(1);
            if (moduleParam instanceof FeatureAccess featureAccess)
            {
                if (featureAccess instanceof StaticFeatureAccess
                    && (THIS_OBJECT_RU.equals(featureAccess.getName()) || THIS_OBJECT.equals(featureAccess.getName())
                        || THIS_FORM_RU.equals(featureAccess.getName()) || THIS_FORM.equals(featureAccess.getName())))
                {
                    Module module = EcoreUtil2.getContainerOfType(featureAccess, Module.class);
                    MethodsScopeSpec spec = BslFactory.eINSTANCE.createMethodsScopeSpec();
                    spec.setModule(module);
                    spec.setOnlyModuleItems(true);
                    spec.setEnvironments(Environments.ALL_CLIENTS);
                    IScope methodScope =
                        scopeProvider.getScope(spec, BslPackage.Literals.METHODS_SCOPE_SPEC__METHOD_REF);
                    return StreamSupport
                        .stream(methodScope.getElements(QualifiedName.create(methodName)).spliterator(), false)
                        .map(IEObjectDescription::getEObjectOrProxy)
                        .filter(Method.class::isInstance)
                        .map(Method.class::cast)
                        .filter(Method::isExport)
                        .map(Environmental.class::cast)
                        .toList();
                }
                else
                {
                    Environmental environmental = EcoreUtil2.getContainerOfType(featureAccess, Environmental.class);
                    List<FeatureEntry> entries = featureAccess instanceof StaticFeatureAccess
                        ? ((StaticFeatureAccess)featureAccess).getFeatureEntries() : dynamicFeatureAccessComputer
                            .getLastObject((DynamicFeatureAccess)featureAccess, environmental.environments());
                    for (FeatureEntry entry : entries)
                    {
                        if (entry.getFeature() instanceof DerivedProperty derivedProperty
                            && derivedProperty.getSource() instanceof CommonModule)
                        {
                            List<TypeItem> types = derivedProperty.getTypes();
                            if (types != null && types.size() == 1 && types.get(0) instanceof Type)
                            {
                                return ((Type)types.get(0)).getContextDef()
                                    .allMethods()
                                    .stream()
                                    .filter(Environmental.class::isInstance)
                                    .filter(item -> methodName.equalsIgnoreCase(item.getName()))
                                    .map(Environmental.class::cast)
                                    .toList();
                            }
                        }
                        else if (entry.getFeature() instanceof Property property
                            && THIS_OBJECT.equals(property.getName())
                            && property.eContainer() instanceof ContextDef contextDef)
                        {
                            return contextDef.allMethods()
                                .stream()
                                .filter(Environmental.class::isInstance)
                                .filter(item -> methodName.equalsIgnoreCase(item.getName()))
                                .map(Environmental.class::cast)
                                .toList();
                        }
                        else if (entry.getFeature() instanceof ImplicitVariable implicitVariable)
                        {
                            List<TypeItem> types =
                                typesComputer.computeTypes(featureAccess, environmental.environments());
                            if (types != null && types.size() == 1 && types.get(0) instanceof Type type
                                && "CommonModule".equals(McoreUtil.getTypeCategory(type))) //$NON-NLS-1$
                            {
                                return type.getContextDef()
                                    .allMethods()
                                    .stream()
                                    .filter(Environmental.class::isInstance)
                                    .filter(item -> methodName.equalsIgnoreCase(item.getName()))
                                    .map(Environmental.class::cast)
                                    .toList();
                            }
                        }
                    }
                }
            }
        }

        return List.of();
    }

    private String getCalledProcedureName(Expression param)
    {
        if (param instanceof StringLiteral)
        {
            StringLiteral literal = (StringLiteral)param;
            if (literal.getLines().size() == 1)
            {
                return literal.lines(true).get(0);
            }
        }
        return null;
    }
}
