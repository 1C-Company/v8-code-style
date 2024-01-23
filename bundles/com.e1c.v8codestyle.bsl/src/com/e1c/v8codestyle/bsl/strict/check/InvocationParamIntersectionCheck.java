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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.util.Triple;
import org.eclipse.xtext.util.Tuples;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.EmptyExpression;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.SourceObjectLinkProvider;
import com._1c.g5.v8.dt.bsl.model.UndefinedLiteral;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.bsl.typesystem.ExportMethodTypeProvider;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.DuallyNamedElement;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com._1c.g5.v8.dt.mcore.ParamSet;
import com._1c.g5.v8.dt.mcore.Parameter;
import com._1c.g5.v8.dt.mcore.Property;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
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
    private static final String METHOD_INSERT = "Insert"; //$NON-NLS-1$

    private static final String MAP_GET = "Get"; //$NON-NLS-1$

    private static final String MAP_DELETE = "Delete"; //$NON-NLS-1$

    private static final String MAP_KEY = "Key"; //$NON-NLS-1$

    private static final String MAP_VALUE = "Value"; //$NON-NLS-1$

    private static final String CHECK_ID = "invocation-parameter-type-intersect"; //$NON-NLS-1$

    private static final String PARAM_ALLOW_DYNAMIC_TYPES_CHECK = "allowDynamicTypesCheck"; //$NON-NLS-1$

    //@formatter:off
    private static final Map<String, Map<String, Collection<Integer>>> COLLECTION_ADD_METHODS = Map.of(
        "Add", Map.of(IEObjectTypeNames.ARRAY, Set.of(0), //$NON-NLS-1$
            IEObjectTypeNames.VALUE_LIST, Set.of(0)),
        METHOD_INSERT, Map.of(
            IEObjectTypeNames.ARRAY, Set.of(1),
            IEObjectTypeNames.VALUE_LIST, Set.of(1),
            IEObjectTypeNames.MAP, Set.of(0, 1)),
        "Set", Map.of( //$NON-NLS-1$
            IEObjectTypeNames.ARRAY, Set.of(1)),
        MAP_GET, Map.of(IEObjectTypeNames.MAP, Set.of(0)),
        MAP_DELETE, Map.of(IEObjectTypeNames.MAP, Set.of(0)),
        "Find", Map.of(IEObjectTypeNames.ARRAY, Set.of(0)) //$NON-NLS-1$
        );

    private static final Map<String, Map<Integer, String>> MAP_KEY_VALUE_TYPES = Map.of(
        METHOD_INSERT, Map.of(0, MAP_KEY, 1, MAP_VALUE),
        MAP_GET, Map.of(0, MAP_KEY),
        MAP_DELETE, Map.of(0, MAP_KEY)
        );

    //@formatter:on

    private final ExportMethodTypeProvider exportMethodTypeProvider;

    /**
     * Instantiates a new invocation param intersection check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     * @param v8ProjectManager the v 8 project manager service, cannot be {@code null}.
     * @param exportMethodTypeProvider the export method type provider service, cannot be {@code null}.
     */
    @Inject
    public InvocationParamIntersectionCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, ExportMethodTypeProvider exportMethodTypeProvider,
        INamingService namingService, IBmModelManager bmModelManager, IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager, v8ProjectManager);
        this.exportMethodTypeProvider = exportMethodTypeProvider;
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
        IBmTransaction bmTransaction, IProgressMonitor monitor)
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
            checkParamTypesIntersect(inv, (Method)source, allowDynamicTypesCheck, resultAceptor, bmTransaction,
                monitor);
        }
        else if (source instanceof com._1c.g5.v8.dt.mcore.Method)
        {
            checkParamTypesIntersect(inv, (com._1c.g5.v8.dt.mcore.Method)source, resultAceptor, bmTransaction, monitor);
        }
    }

    private void checkParamTypesIntersect(Invocation inv, com._1c.g5.v8.dt.mcore.Method method,
        ResultAcceptor resultAceptor, IBmTransaction bmTransaction, IProgressMonitor monitor)
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

        // This allows to check collection item type
        Triple<Collection<TypeItem>, Collection<Integer>, Boolean> collectionItemContext =
            getCollectionItemContext(inv, method, actualEnvs);
        Collection<TypeItem> collectionItemTypes = collectionItemContext.getFirst();
        Collection<Integer> parameterNumbers = collectionItemContext.getSecond();
        boolean isMap = collectionItemContext.getThird();

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

            Collection<TypeItem> targetTypes =
                getDefaultTargetOrCollectionItemTypes(method, collectionItemTypes, parameterNumbers, isMap, i, inv);
            boolean isCollectionItemTypeEmpty = targetTypes.isEmpty();
            boolean isIntersect = !isCollectionItemTypeEmpty && intersectTypeItem(targetTypes, sorceTypes, inv);
            Parameter parameter = null;
            for (Iterator<ParamSet> iterator = paramSets.iterator(); !isIntersect && isCollectionItemTypeEmpty
                && iterator.hasNext();)
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
                    BmOperationContext typeComputationContext =
                        new BmOperationContext(namingService, bmModelManager, bmTransaction);
                    targetTypes = docComment.get()
                        .computeParameterTypes(parameter.getName(), typeScope, scopeProvider, qualifiedNameConverter,
                            commentProvider, v8ProjectManager, oldFormatComment, method, typeComputationContext);
                }

                if (targetTypes.isEmpty())
                {
                    continue;
                }

                isIntersect = intersectTypeItem(targetTypes, sorceTypes, inv);
                if (!isIntersect && !targetTypes.isEmpty())
                {
                    // if we don't match this ParamSet so will not use for other parameters
                    iterator.remove();
                }
            }

            if (!isIntersect && !targetTypes.isEmpty())
            {
                markInvalidSourceTypeNoIntercection(param, i, parameter, resultAceptor, targetTypes);
            }
        }
    }

    private Triple<Collection<TypeItem>, Collection<Integer>, Boolean> getCollectionItemContext(Invocation inv,
        com._1c.g5.v8.dt.mcore.Method method, Environments actualEnvs)
    {
        Collection<TypeItem> collectionItemTypes = new ArrayList<>();
        Collection<Integer> parameterNumbers = Collections.emptyList();
        boolean isMap = false;

        if (!(method instanceof SourceObjectLinkProvider) && inv.getMethodAccess() instanceof DynamicFeatureAccess)
        {
            Map<String, Collection<Integer>> typesAndParams = COLLECTION_ADD_METHODS.get(method.getName());
            if (typesAndParams != null)
            {
                TypeItem collectionType = EcoreUtil2.getContainerOfType(method, TypeItem.class);
                String typeName = collectionType == null ? null : McoreUtil.getTypeName(collectionType);
                if (typeName != null && typesAndParams.containsKey(typeName))
                {
                    List<TypeItem> types = typeComputer
                        .computeTypes(((DynamicFeatureAccess)inv.getMethodAccess()).getSource(), actualEnvs);
                    for (TypeItem type : types)
                    {
                        type = (TypeItem)EcoreUtil.resolve(type, inv);
                        if (type instanceof Type && typeName.equals(McoreUtil.getTypeName(type)))
                        {
                            if (IEObjectTypeNames.VALUE_LIST.equals(typeName))
                            {
                                List<TypeItem> valueListItemTypes = ((Type)type).getCollectionElementTypes().allTypes();
                                Set<TypeItem> collectionTypes =
                                    dynamicFeatureAccessComputer.getAllProperties(valueListItemTypes, inv.eResource())
                                        .stream()
                                        .flatMap(e -> e.getFirst().stream())
                                        .filter(p -> MAP_VALUE.equals(p.getName()))
                                        .flatMap(p -> p.getTypes().stream())
                                        .collect(Collectors.toSet());
                                collectionItemTypes.addAll(collectionTypes);
                            }
                            else
                            {
                                collectionItemTypes.addAll(((Type)type).getCollectionElementTypes().allTypes());
                            }
                            isMap = IEObjectTypeNames.MAP.equals(typeName);
                            parameterNumbers = typesAndParams.get(typeName);
                        }
                    }
                    // Remove Arbitrary type which do not need to check
                    for (Iterator<TypeItem> iterator = collectionItemTypes.iterator(); iterator.hasNext();)
                    {
                        TypeItem typeItem = iterator.next();
                        if (IEObjectTypeNames.ARBITRARY.equals(McoreUtil.getTypeName(typeItem)))
                        {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return Tuples.create(collectionItemTypes, parameterNumbers, isMap);
    }

    private Collection<TypeItem> getDefaultTargetOrCollectionItemTypes(com._1c.g5.v8.dt.mcore.Method method,
        Collection<TypeItem> collectionItemTypes, Collection<Integer> parameterNumbers, boolean isMap,
        int parameterNumber, EObject context)
    {
        Collection<TypeItem> targetTypes = Collections.emptyList();
        if (!collectionItemTypes.isEmpty() && parameterNumbers.contains(parameterNumber))
        {
            // use collection item types
            if (isMap)
            {
                String name = MAP_KEY_VALUE_TYPES.get(method.getName()).get(parameterNumber);
                Optional<Property> property =
                    dynamicFeatureAccessComputer.getAllProperties(collectionItemTypes, context.eResource())
                        .stream()
                        .flatMap(p -> p.getFirst().stream())
                        .filter(p -> name.equals(p.getName()))
                        .findFirst();
                if (property.isPresent())
                {
                    targetTypes = property.get().getTypes();
                }
            }
            else
            {
                targetTypes = collectionItemTypes;
            }
        }
        return targetTypes;
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
        ResultAcceptor resultAceptor, IBmTransaction bmTransaction, IProgressMonitor monitor)
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
            Collection<TypeItem> targetTypes = null;
            if (docComment.isPresent() && docComment.get().getParametersSection().getParameterByName(paramName) != null)
            {
                BmOperationContext typeComputationContext =
                    new BmOperationContext(namingService, bmModelManager, bmTransaction);
                // if parameter declared in doc-comment then check only declared types
                targetTypes = docComment.get()
                    .computeParameterTypes(paramName, typeScope, scopeProvider, qualifiedNameConverter, commentProvider,
                        v8ProjectManager, oldFormatComment, method, typeComputationContext);
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
        List<String> typeNames =
            targetTypes.stream().map(nameFunc).filter(Objects::nonNull).collect(Collectors.toList());

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
        source = EcoreUtil.resolve(source, mcoreMethod);
        if (source.eIsProxy() || !(source instanceof Method))
        {
            return Optional.empty();
        }
        Method sourceMethod = (Method)source;

        BslDocumentationComment docComment =
            BslCommentUtils.parseTemplateComment(sourceMethod, oldFormatComment, commentProvider);
        return Optional.ofNullable(docComment);
    }

}
