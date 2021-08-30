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
package com.e1c.v8codestyle.bsl.strict.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STATIC_FEATURE_ACCESS;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.UndefinedLiteral;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.typesystem.ExportMethodTypeProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.ParamSet;
import com._1c.g5.v8.dt.mcore.Parameter;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Checks invokable method parameter types intersects with types of invocation expression.
 *
 * @author Dmitriy Marmyshev
 */
public class InvocationParamIntersectionCheck
    extends AbstractTypeCheck
{

    private static final String CHECK_ID = "invocation-parameter-type-intersect"; //$NON-NLS-1$

    private final ExportMethodTypeProvider exportMethodTypeProvider;

    /**
     * Instantiates a new invocation param intersection check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     */
    @Inject
    public InvocationParamIntersectionCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter);

        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        this.exportMethodTypeProvider = rsp.get(ExportMethodTypeProvider.class);

    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.InvocationParamIntersectionCheck_title)
            .description(Messages.InvocationParamIntersectionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StrictTypeAnnotationCheckExtension())
            .module()
            .checkedObjectType(STATIC_FEATURE_ACCESS, DYNAMIC_FEATURE_ACCESS);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof EObject))
        {
            return;
        }

        FeatureAccess fa = (FeatureAccess)object;

        Invocation inv = BslUtil.getInvocation(fa);
        if (inv == null || inv.getParams().isEmpty())
        {
            return;
        }

        EObject source = getSourceMethod(fa);
        if (source instanceof Method)
        {
            checkParamTypesIntersect(inv, (Method)source, resultAceptor, monitor);
        }
        else if (source instanceof com._1c.g5.v8.dt.mcore.Method)
        {
            checkParamTypesIntersect(inv, (com._1c.g5.v8.dt.mcore.Method)source, resultAceptor, monitor);
        }
    }

    private void checkParamTypesIntersect(Invocation inv, com._1c.g5.v8.dt.mcore.Method method,
        ResultAcceptor resultAceptor, IProgressMonitor monitor)
    {
        if (method.getParamSet().isEmpty())
        {
            return;
        }

        List<ParamSet> paramSets = actualParamSet(method, inv.getParams().size());
        if (paramSets.isEmpty())
        {
            return;
        }

        Environments actualEnvs = getActualEnvironments(inv);

        for (int i = 0; i < inv.getParams().size(); i++)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            Expression param = inv.getParams().get(i);
            List<TypeItem> sorceTypes = computeTypes(param, actualEnvs);
            boolean isUndefined = (param == null || param instanceof UndefinedLiteral || isUndefinedType(sorceTypes));

            List<TypeItem> targetTypes = Collections.emptyList();
            boolean isIntersect = false;
            for (Iterator<ParamSet> iterator = paramSets.iterator(); iterator.hasNext();)
            {
                ParamSet paramSet = iterator.next();

                List<Parameter> targetParams = paramSet.getParams();
                if (targetParams.size() <= i)
                {
                    iterator.remove();
                    continue;
                }
                boolean isDefaultValue = targetParams.get(i).isDefaultValue();

                if (isDefaultValue && isUndefined)
                {
                    isIntersect = true;
                    break;
                }

                targetTypes = exportMethodTypeProvider.getMethodParamType(method, paramSet, i);
                if (targetTypes.isEmpty())
                {
                    continue;
                }

                isIntersect = intersectTypeItem(targetTypes, sorceTypes, inv);
                if (isIntersect)
                {
                    break;
                }
            }

            if (!isIntersect && !targetTypes.isEmpty())
            {
                markInvalidSourceTypeNoIntercection(param, i, resultAceptor);
            }
        }
    }

    private boolean isUndefinedType(List<TypeItem> types)
    {
        if (types.size() == 1)
        {
            return IEObjectTypeNames.UNDEFINED.equals(McoreUtil.getTypeName(types.get(0)));
        }
        return false;
    }

    private void checkParamTypesIntersect(Invocation inv, Method method, ResultAcceptor resultAceptor,
        IProgressMonitor monitor)
    {

        Environments actualEnvs = getActualEnvironments(inv);

        List<FormalParam> targetParams = method.getFormalParams();
        for (int i = 0; i < inv.getParams().size(); i++)
        {
            if (monitor.isCanceled())
                return;

            if (targetParams.size() <= i)
                return;

            List<TypeItem> targetTypes = computeTypes(targetParams.get(i), actualEnvs);

            if (targetTypes.isEmpty())
                return;

            Expression param = inv.getParams().get(i);

            List<TypeItem> sorceTypes = computeTypes(param, actualEnvs);

            if (!intersectTypeItem(targetTypes, sorceTypes, inv))
            {
                markInvalidSourceTypeNoIntercection(param, i, resultAceptor);
            }
        }

    }

    private void markInvalidSourceTypeNoIntercection(Expression param, int index, ResultAcceptor resultAceptor)
    {
        String message = MessageFormat.format(
            Messages.StrictModuleInvocationCheck_Type_of_N_parameter_not_intersect_with_invocation_type, index + 1);
        resultAceptor.addIssue(message, param, BslPackage.Literals.EXPRESSION__TYPES);
    }

    private EObject getSourceMethod(FeatureAccess object)
    {
        Environments actualEnvs = getActualEnvironments(object);
        if (actualEnvs.isEmpty())
        {
            return null;
        }
        List<FeatureEntry> objects = dynamicFeatureAccessComputer.resolveObject(object, actualEnvs);
        for (FeatureEntry entry : objects)
        {
            EObject source = entry.getFeature();
            if (source instanceof Method || (source instanceof com._1c.g5.v8.dt.mcore.Method))
            {
                return source;
            }
        }

        return null;
    }

    private Environments getActualEnvironments(EObject object)
    {
        Environmental envs = EcoreUtil2.getContainerOfType(object, Environmental.class);
        if (envs == null)
            return Environments.EMPTY;

        return bslPreferences.getLoadEnvs(object).intersect(envs.environments());
    }

    private List<ParamSet> actualParamSet(com._1c.g5.v8.dt.mcore.Method method, int numParam)
    {
        List<ParamSet> result = new ArrayList<>();
        List<ParamSet> paramSets = method.getParamSet();
        for (ParamSet paramSet : paramSets)
        {
            if (numParam >= paramSet.getMinParams()
                || numParam <= paramSet.getMaxParams() && paramSet.getMaxParams() != -1)
            {
                result.add(paramSet);
            }
        }
        if (result.isEmpty() && numParam > 0 && !paramSets.isEmpty())
        {
            result.addAll(paramSets);
        }

        return result;
    }

}
