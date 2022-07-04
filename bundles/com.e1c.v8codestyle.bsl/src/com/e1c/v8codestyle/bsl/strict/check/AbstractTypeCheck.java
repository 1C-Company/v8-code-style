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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.ForEachStatement;
import com._1c.g5.v8.dt.bsl.model.ForToStatement;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.bsl.model.typesytem.TypeSystemMode;
import com._1c.g5.v8.dt.bsl.model.typesytem.VariableTreeTypeState;
import com._1c.g5.v8.dt.bsl.model.typesytem.VariableTreeTypeStateWithSubStates;
import com._1c.g5.v8.dt.bsl.model.typesytem.VariableTypeState;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.bsl.typesystem.util.TypeSystemUtil;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.TypeSet;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectDynamicTypeNames;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.components.BasicCheck;

/**
 * Abstract check of types in module objects. Allows to compute types respecting system enums,
 * compare intersections of type collections and etc.
 *
 * @author Dmitriy Marmyshev
 */
public abstract class AbstractTypeCheck
    extends BasicCheck
{

    private static final Collection<String> ALL_REF_TYPE_SET_PARENT_TYPE_NAMES =
        Set.of(IEObjectDynamicTypeNames.CATALOG_REF_TYPE_NAME, IEObjectDynamicTypeNames.DOCUMENT_REF_TYPE_NAME,
            IEObjectDynamicTypeNames.ENUM_REF_TYPE_NAME, IEObjectDynamicTypeNames.COC_REF_TYPE_NAME,
            IEObjectDynamicTypeNames.COA_REF_TYPE_NAME, IEObjectDynamicTypeNames.CALCULATION_TYPE_REF_TYPE_NAME,
            IEObjectDynamicTypeNames.BP_REF_TYPE_NAME, IEObjectDynamicTypeNames.BP_ROUTEPOINT_TYPE_NAME,
            IEObjectDynamicTypeNames.TASK_REF_TYPE_NAME, IEObjectDynamicTypeNames.EXCHANGE_PLAN_REF_TYPE_NAME);

    private static final String COMMON_MODULE = "CommonModule"; //$NON-NLS-1$

    private static final QualifiedName QN_COMMON_MODULE = QualifiedName.create(COMMON_MODULE);

    /** The resource lookup service. */
    protected final IResourceLookup resourceLookup;

    /** The BSL preferences service. */
    protected final IBslPreferences bslPreferences;

    /** The type computer service. */
    protected final TypesComputer typeComputer;

    /** The dynamic feature access computer service. */
    protected final DynamicFeatureAccessComputer dynamicFeatureAccessComputer;

    /** The scope provider service. */
    protected final IScopeProvider scopeProvider;

    /** The qualified name converter service. */
    protected final IQualifiedNameConverter qualifiedNameConverter;

    /** The comment provider service. */
    protected final BslMultiLineCommentDocumentationProvider commentProvider;

    /**
     * Instantiates a new abstract type check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     */
    protected AbstractTypeCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter)
    {
        super();
        this.resourceLookup = resourceLookup;
        this.bslPreferences = bslPreferences;
        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$
        this.typeComputer = rsp.get(TypesComputer.class);
        this.dynamicFeatureAccessComputer = rsp.get(DynamicFeatureAccessComputer.class);
        this.scopeProvider = rsp.get(IScopeProvider.class);
        this.commentProvider = rsp.get(BslMultiLineCommentDocumentationProvider.class);
        this.qualifiedNameConverter = qualifiedNameConverter;
    }

    /**
     * Checks if the object has empty types.
     *
     * @param object the object, cannot be {@code null}.
     * @return true, if the object has empty types
     */
    protected boolean isEmptyTypes(EObject object)
    {
        Environmental envs = EcoreUtil2.getContainerOfType(object, Environmental.class);
        if (envs == null)
        {
            return true;
        }

        Environments actualEnvs = bslPreferences.getLoadEnvs(object).intersect(envs.environments());
        if (actualEnvs.isEmpty())
        {
            return true;
        }
        if (object instanceof Invocation && !actualEnvs.containsAny(Environments.SERVER)
            && ((Invocation)object).isIsServerCall())
        {
            actualEnvs = actualEnvs.add(Environments.SERVER);
        }

        List<TypeItem> types = computeTypes(object, actualEnvs);

        if (types.isEmpty() && object instanceof ExplicitVariable)
        {
            Collection<TypeItem> commentTypes = computeCommentTypes(object);
            return commentTypes.isEmpty();
        }

        return types.isEmpty();
    }

    /**
     * Compute types with respect to system enumeration and variable type state.
     *
     * @param object the object, cannot be {@code null}.
     * @param envs the environments, cannot be {@code null}.
     * @return the list of types, cannot return {@code null}.
     */
    protected List<TypeItem> computeTypes(EObject object, Environments envs)
    {
        if (object instanceof Variable && object.eContainer() instanceof FeatureAccess)
        {
            return getActualTypesForFeatureVariable((Variable)object, object.eContainer(), envs);
        }
        else if (object instanceof StaticFeatureAccess && ((StaticFeatureAccess)object).getImplicitVariable() != null)
        {
            return getActualTypesForFeatureVariable(((StaticFeatureAccess)object).getImplicitVariable(), object, envs);
        }

        List<TypeItem> types = typeComputer.computeTypes(object, envs);

        if (types.isEmpty() && object instanceof DynamicFeatureAccess
            && ((DynamicFeatureAccess)object).getSource() instanceof FeatureAccess)
        {
            // Bypass system enum types with property without type
            DynamicFeatureAccess dfa = (DynamicFeatureAccess)object;
            FeatureAccess fa = (FeatureAccess)dfa.getSource();
            List<TypeItem> sourceTypes = typeComputer.computeTypes(fa, envs);
            if (sourceTypes.size() == 1)
            {
                String typeName = fa.getName();
                String propertyName = dfa.getName();
                TypeItem type = sourceTypes.get(0);
                if (typeName != null
                    && (typeName.equalsIgnoreCase(McoreUtil.getTypeNameRu(type))
                        || typeName.equalsIgnoreCase(McoreUtil.getTypeName(type)))
                    && dynamicFeatureAccessComputer.getAllProperties(sourceTypes, object.eResource())
                        .stream()
                        .flatMap(e -> e.getFirst().stream())
                        .anyMatch(p -> propertyName.equalsIgnoreCase(p.getNameRu())
                            || propertyName.equalsIgnoreCase(p.getName())))
                {
                    return sourceTypes;
                }
            }
        }

        return types;
    }

    /**
     * Compute comment types. This method may be used for {@link SimpleStatement} with variable assignment.
     * In-line documentation comment with type also may be add to the type collection of the right part of
     * the statement.
     *
     * @param object the object, cannot be {@code null}.
     * @return the collection of comment types, cannot return {@code null}.
     */
    protected Collection<TypeItem> computeCommentTypes(EObject object)
    {
        IScope typeScope = scopeProvider.getScope(object, McorePackage.Literals.TYPE_DESCRIPTION__TYPES);
        IProject project = resourceLookup.getProject(object);
        boolean oldFormatComment = bslPreferences.getDocumentCommentProperties(project).oldCommentFormat();
        return TypeSystemUtil.computeCommentTypes(object, typeScope, scopeProvider, qualifiedNameConverter,
            commentProvider, oldFormatComment);
    }

    /**
     * Checks that collections of {@link TypeItem} have common item
     * @param expectedTypes expected collection of {@link TypeItem}, cannot be <code>null</code>
     * @param realTypes real collection of {@link TypeItem}, cannot be <code>null</code>
     * @param context {@link EObject} for resolving proxy checking types, cannot be <code>null</code>
     * @return <code>true</code> if intersection was detected
     */
    protected boolean intersectTypeItem(Collection<TypeItem> expectedTypes, Collection<TypeItem> realTypes,
        EObject context)
    {
        if (expectedTypes.isEmpty())
        {
            return true;
        }
        Collection<String> expectedTypesNames = getTypeNames(expectedTypes, context);
        expectedTypesNames.addAll(getCastingType(expectedTypesNames));
        if (expectedTypesNames.contains(IEObjectTypeNames.ARBITRARY)
            || expectedTypesNames.contains(IEObjectTypeNames.UNDEFINED)
            || expectedTypesNames.contains(IEObjectTypeNames.REFERENCE_TO_OBJECT_OF_INFORMATION_BASE)
            || expectedTypesNames.contains(IEObjectTypeNames.XDTO_DATA_VALUE)
            || expectedTypesNames.contains(IEObjectTypeNames.XDTO_DATA_OBJECT))
        {
            return true;
        }
        if (realTypes.isEmpty())
        {
            return false;
        }
        Collection<TypeItem> withParentTypes = getParentsOfRealTypes(realTypes, context);
        Collection<String> realTypesNames = getTypeNames(withParentTypes, context);

        if (!expectedTypesNames.isEmpty() && !realTypesNames.isEmpty())
        {
            expectedTypesNames.retainAll(realTypesNames);
            return !expectedTypesNames.isEmpty();
        }
        else
        {
            return true;
        }
    }

    private static Collection<? extends String> getCastingType(Collection<String> expectedTypesNames)
    {
        List<String> castTypeNames = new ArrayList<>();
        for (String typeName : expectedTypesNames)
        {
            if (IEObjectTypeNames.STRUCTURE.equals(typeName))
            {
                castTypeNames.add(IEObjectTypeNames.FIXED_STRUCTURE);
            }
        }
        return castTypeNames;
    }

    private Collection<TypeItem> getParentsOfRealTypes(Collection<TypeItem> realTypes, EObject context)
    {
        Deque<TypeItem> types = new ArrayDeque<>(realTypes);
        List<TypeItem> parentTypes = new LinkedList<>();
        while (!types.isEmpty())
        {
            TypeItem type = types.pollFirst();
            type = (TypeItem)EcoreUtil.resolve(type, context);
            parentTypes.add(type);
            if (type instanceof TypeSet)
            {
                ((TypeSet)type).getTypes().forEach(types::add);
            }
            if (type instanceof Type && ((Type)type).getParentType() != null)
            {
                types.add(((Type)type).getParentType());
            }
            else if (type instanceof Type && COMMON_MODULE.equals(McoreUtil.getTypeCategory(type)))
            {
                // Here is bypass of wrong type hierarchy of types for common modules
                IScope typeScope = scopeProvider.getScope(context, McorePackage.Literals.TYPE_DESCRIPTION__TYPES);
                IEObjectDescription element = typeScope.getSingleElement(QN_COMMON_MODULE);
                if (element != null)
                {
                    EObject parentCommonModuleType = element.getEObjectOrProxy();
                    if (parentCommonModuleType instanceof TypeItem)
                    {
                        parentTypes.add((TypeItem)parentCommonModuleType);
                    }
                }
            }
        }
        return parentTypes;
    }

    private static Collection<String> getTypeNames(Collection<TypeItem> parentTypes, EObject context)
    {
        Set<String> typeNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (TypeItem type : parentTypes)
        {
            type = (TypeItem)EcoreUtil.resolve(type, context);
            String typeName = McoreUtil.getTypeName(type);
            if (typeName != null)
            {
                typeNames.add(typeName);
            }
            if (type instanceof TypeSet && typeName != null)
            {
                typeNames.addAll(((TypeSet)type).getTypes()
                    .stream()
                    .map(McoreUtil::getTypeName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

                if (IEObjectTypeNames.ANY_REF.equals(typeName))
                {
                    typeNames.addAll(ALL_REF_TYPE_SET_PARENT_TYPE_NAMES);
                }
                else
                {
                    String parentTypeName = getTypeSetItemParentTypeName(typeName);
                    if (parentTypeName != null)
                    {
                        typeNames.add(parentTypeName);
                    }
                }
            }
        }
        return typeNames;
    }

    private static String getTypeSetItemParentTypeName(String typeName)
    {
        switch (typeName)
        {
        case IEObjectTypeNames.CATALOG_REF:
            return IEObjectDynamicTypeNames.CATALOG_REF_TYPE_NAME;
        case IEObjectTypeNames.DOCUMENT_REF:
            return IEObjectDynamicTypeNames.DOCUMENT_REF_TYPE_NAME;
        case IEObjectTypeNames.ENUM_REF:
            return IEObjectDynamicTypeNames.ENUM_REF_TYPE_NAME;
        case IEObjectTypeNames.CHART_OF_CHARACTERISTIC_TYPES_REF:
            return IEObjectDynamicTypeNames.COC_REF_TYPE_NAME;
        case IEObjectTypeNames.CHART_OF_ACCOUNTS_REF:
            return IEObjectDynamicTypeNames.COA_REF_TYPE_NAME;
        case IEObjectTypeNames.CHART_OF_CALCULATION_TYPES_REF:
            return IEObjectDynamicTypeNames.CALCULATION_TYPE_REF_TYPE_NAME;
        case IEObjectTypeNames.BUSINESS_PROCESS_REF:
            return IEObjectDynamicTypeNames.BP_REF_TYPE_NAME;
        case IEObjectTypeNames.BUSINESS_PROCESS_ROUTE_POINT_REF:
            return IEObjectDynamicTypeNames.BP_ROUTEPOINT_TYPE_NAME;
        case IEObjectTypeNames.TASK_REF:
            return IEObjectDynamicTypeNames.TASK_REF_TYPE_NAME;
        case IEObjectTypeNames.EXCHANGE_PLAN_REF:
            return IEObjectDynamicTypeNames.EXCHANGE_PLAN_REF_TYPE_NAME;
        default:
            return null;
        }
    }

    // TODO replace this with utility com._1c.g5.v8.dt.bsl.typesystem.util.TypeSystemUtil after 2022.1
    private List<TypeItem> getActualTypesForFeatureVariable(Variable variable, EObject featureObject, Environments envs)
    {
        SimpleStatement statement = EcoreUtil2.getContainerOfType(featureObject, SimpleStatement.class);
        Invocation inv = EcoreUtil2.getContainerOfType(featureObject, Invocation.class);
        List<TypeItem> allTypes = null;
        int actualOffset = -1;
        if (statement != null && statement.getRight() != null && statement.getLeft() == featureObject)
        {
            actualOffset = NodeModelUtils.findActualNodeFor(statement).getTotalEndOffset();
        }
        else if (inv != null && inv.getParams().contains(featureObject))
        {
            actualOffset = NodeModelUtils.findActualNodeFor(featureObject).getTotalEndOffset();
        }
        else if (inv == null && statement == null && variable instanceof FormalParam)
        {
            actualOffset = NodeModelUtils.findActualNodeFor(featureObject).getTotalEndOffset();
        }
        else if (featureObject.eContainingFeature() == BslPackage.Literals.FOR_STATEMENT__VARIABLE_ACCESS)
        {
            if (featureObject.eContainer() instanceof ForEachStatement
                && ((ForEachStatement)featureObject.eContainer()).getCollection() != null)
            {
                actualOffset =
                    NodeModelUtils.findActualNodeFor(((ForEachStatement)featureObject.eContainer()).getCollection())
                        .getTotalEndOffset() + 1;
            }
            else if (featureObject.eContainer() instanceof ForToStatement
                && ((ForToStatement)featureObject.eContainer()).getInitializer() != null)
            {
                actualOffset =
                    NodeModelUtils.findActualNodeFor(((ForToStatement)featureObject.eContainer()).getInitializer())
                        .getTotalEndOffset() + 1;
            }
        }
        if (actualOffset != -1 && variable.getTypeStateProvider() != null)
        {
            allTypes = new ArrayList<>();
            List<VariableTypeState> nearestStates =
                variable.getTypeStateProvider().get(TypeSystemMode.NORMAL).getNearestByOffset(envs, actualOffset);
            for (VariableTypeState nearestState : nearestStates)
            {
                if (nearestState != null)
                {
                    if (nearestState instanceof VariableTreeTypeStateWithSubStates)
                    {
                        for (VariableTreeTypeState subState : ((VariableTreeTypeStateWithSubStates)nearestState)
                            .getSubStates(envs))
                        {
                            subState.getTypes().forEach(allTypes::add);
                        }
                    }
                    else
                    {
                        nearestState.getTypes().forEach(allTypes::add);
                    }
                }
            }
        }
        else
        {
            allTypes = typeComputer.computeTypes(featureObject, envs);
        }
        return allTypes;
    }

}
