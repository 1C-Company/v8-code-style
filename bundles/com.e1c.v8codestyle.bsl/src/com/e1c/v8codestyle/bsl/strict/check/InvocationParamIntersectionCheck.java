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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.scoping.IScope;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.EmptyExpression;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.SourceObjectLinkProvider;
import com._1c.g5.v8.dt.bsl.model.UndefinedLiteral;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.typesystem.ExportMethodTypeProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.DuallyNamedElement;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com._1c.g5.v8.dt.mcore.ParamSet;
import com._1c.g5.v8.dt.mcore.Parameter;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
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

    private static final String PARAM_ALLOW_DYNAMIC_TYPES_CHECK = "allowDynamicTypesCheck"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    private final ExportMethodTypeProvider exportMethodTypeProvider;

    /**
     * Instantiates a new invocation param intersection check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     * @param v8ProjectManager the v 8 project manager service, cannot be {@code null}.
     */
    @Inject
    public InvocationParamIntersectionCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter);

        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        this.exportMethodTypeProvider = rsp.get(ExportMethodTypeProvider.class);
        this.v8ProjectManager = v8ProjectManager;
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
            .checkedObjectType(STATIC_FEATURE_ACCESS, DYNAMIC_FEATURE_ACCESS)
            .parameter(PARAM_ALLOW_DYNAMIC_TYPES_CHECK, Boolean.class, Boolean.FALSE.toString(),
                Messages.InvocationParamIntersectionCheck_Allow_dynamic_types_check_for_local_method_call);

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
            boolean allowDynamicTypesCheck = parameters.getBoolean(PARAM_ALLOW_DYNAMIC_TYPES_CHECK);
            checkParamTypesIntersect(inv, (Method)source, allowDynamicTypesCheck, resultAceptor, monitor);
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

        boolean isBslContexMethod = method instanceof SourceObjectLinkProvider;

        Optional<BslDocumentationComment> docComment = null;
        boolean oldFormatComment = false;
        IScope typeScope = null;

        Environments actualEnvs = getActualEnvironments(inv);

        for (int i = 0; i < inv.getParams().size(); i++)
        {
            if (monitor.isCanceled())
            {
                return;
            }

            Expression param = inv.getParams().get(i);
            List<TypeItem> sorceTypes = computeTypes(param, actualEnvs);
            boolean isUndefined = param == null || param instanceof UndefinedLiteral || param instanceof EmptyExpression
                || isUndefinedType(sorceTypes);

            Collection<TypeItem> targetTypes = Collections.emptyList();
            boolean isIntersect = false;
            Parameter parameter = null;
            for (Iterator<ParamSet> iterator = paramSets.iterator(); iterator.hasNext();)
            {
                ParamSet paramSet = iterator.next();

                List<Parameter> targetParams = paramSet.getParams();
                if (targetParams.size() <= i)
                {
                    iterator.remove();
                    continue;
                }
                parameter = targetParams.get(i);
                boolean isDefaultValue = parameter.isDefaultValue();

                if (isDefaultValue && isUndefined)
                {
                    isIntersect = true;
                    break;
                }

                if (monitor.isCanceled())
                {
                    return;
                }

                targetTypes = exportMethodTypeProvider.getMethodParamType(method, paramSet, i);
                if (targetTypes.isEmpty() && isBslContexMethod && docComment == null)
                {
                    // get types from doc-comment for export method
                    IProject project = resourceLookup.getProject(method);
                    oldFormatComment = bslPreferences.getDocumentCommentProperties(project).oldCommentFormat();
                    typeScope = scopeProvider.getScope(method, McorePackage.Literals.TYPE_DESCRIPTION__TYPES);

                    docComment = getDocComment((SourceObjectLinkProvider)method, oldFormatComment);
                }

                if (monitor.isCanceled())
                {
                    return;
                }

                if (docComment != null && docComment.isPresent())
                {
                    targetTypes = docComment.get()
                        .computeParameterTypes(parameter.getName(), typeScope, scopeProvider, qualifiedNameConverter,
                            commentProvider, oldFormatComment, method);
                }

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
                markInvalidSourceTypeNoIntercection(param, i, parameter, resultAceptor, targetTypes);
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

    private void checkParamTypesIntersect(Invocation inv, Method method, boolean allowDynamicTypesCheck,
        ResultAcceptor resultAceptor, IProgressMonitor monitor)
    {
        Environments actualEnvs = getActualEnvironments(inv);

        Optional<BslDocumentationComment> docComment;
        boolean oldFormatComment = false;
        IScope typeScope = null;
        if (allowDynamicTypesCheck)
        {
            docComment = Optional.empty();
        }
        else
        {
            // get types from doc-comment for export method
            IProject project = resourceLookup.getProject(method);
            oldFormatComment = bslPreferences.getDocumentCommentProperties(project).oldCommentFormat();
            typeScope = scopeProvider.getScope(method, McorePackage.Literals.TYPE_DESCRIPTION__TYPES);

            BslDocumentationComment docModel =
                BslCommentUtils.parseTemplateComment(method, oldFormatComment, commentProvider);
            if (docModel.getParametersSection() == null
                || docModel.getParametersSection().getParameterDefinitions().isEmpty())
            {
                docModel = null;
            }
            docComment = Optional.ofNullable(docModel);
        }

        List<FormalParam> targetParams = method.getFormalParams();
        for (int i = 0; i < inv.getParams().size(); i++)
        {
            if (monitor.isCanceled() || targetParams.size() <= i)
            {
                return;
            }

            FormalParam formalParam = targetParams.get(i);
            String paramName = formalParam.getName();
            Collection<TypeItem> targetTypes = Collections.emptyList();
            if (docComment.isPresent() && docComment.get().getParametersSection().getParameterByName(paramName) != null)
            {
                // if parameter declared in doc-comment then check only declared types
                targetTypes = docComment.get()
                    .computeParameterTypes(paramName, typeScope, scopeProvider, qualifiedNameConverter, commentProvider,
                        oldFormatComment, method);
            }
            else
            {
                targetTypes = computeTypes(formalParam, actualEnvs);
            }

            if (targetTypes.isEmpty())
            {
                return;
            }

            Expression param = inv.getParams().get(i);

            Collection<TypeItem> sorceTypes = computeTypes(param, actualEnvs);

            if (!intersectTypeItem(targetTypes, sorceTypes, inv))
            {
                markInvalidSourceTypeNoIntercection(param, i, formalParam, resultAceptor, targetTypes);
            }
        }
    }

    private void markInvalidSourceTypeNoIntercection(Expression param, int index, NamedElement parameter,
        ResultAcceptor resultAceptor, Collection<TypeItem> targetTypes)
    {
        IV8Project project = v8ProjectManager.getProject(param);
        ScriptVariant variant = project.getScriptVariant();
        Function<TypeItem, String> nameFunc =
            variant == ScriptVariant.RUSSIAN ? McoreUtil::getTypeNameRu : McoreUtil::getTypeName;
        List<String> typeNames = targetTypes.stream().map(nameFunc).collect(Collectors.toList());

        String name = parameter == null ? String.valueOf(index + 1) : parameter.getName();
        if (parameter instanceof DuallyNamedElement && variant == ScriptVariant.RUSSIAN)
        {
            name = ((DuallyNamedElement)parameter).getNameRu();
        }
        String message = MessageFormat.format(
            Messages.StrictModuleInvocationCheck_Type_of_N_parameter_not_intersect_with_invocation_type, name,
            String.join(", ", typeNames)); //$NON-NLS-1$
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
        {
            return Environments.EMPTY;
        }

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

    // TODO remove this method when EDT will provide service for this,
    // or ExportMethodTypeProvider will work properly with doc-comment types for export methods
    private Optional<BslDocumentationComment> getDocComment(SourceObjectLinkProvider mcoreMethod,
        boolean oldFormatComment)
    {
        EObject source = EcoreFactory.eINSTANCE.createEObject();
        ((InternalEObject)source).eSetProxyURI(mcoreMethod.getSourceUri());
        Method sourceMethod = (Method)EcoreUtil.resolve(source, mcoreMethod);

        BslDocumentationComment docComment =
            BslCommentUtils.parseTemplateComment(sourceMethod, oldFormatComment, commentProvider);
        return Optional.ofNullable(docComment);
    }

}
