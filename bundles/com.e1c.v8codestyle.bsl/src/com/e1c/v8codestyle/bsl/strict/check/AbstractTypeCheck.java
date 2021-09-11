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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
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
import com.google.common.collect.Sets;

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

    protected final IResourceLookup resourceLookup;

    protected final IBslPreferences bslPreferences;

    protected final TypesComputer typeComputer;

    protected final DynamicFeatureAccessComputer dynamicFeatureAccessComputer;

    private final IScopeProvider scopeProvider;

    private final IQualifiedNameConverter qualifiedNameConverter;

    private final BslMultiLineCommentDocumentationProvider commentProvider;

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
        List<TypeItem> types = computeTypes(object, actualEnvs);

        if (types.isEmpty() && object instanceof ExplicitVariable)
        {
            IScope typeScope = scopeProvider.getScope(object, McorePackage.Literals.TYPE_DESCRIPTION__TYPES);
            IProject project = resourceLookup.getProject(object);
            boolean oldFormatComment = bslPreferences.getDocumentCommentProperties(project).oldCommentFormat();
            Collection<TypeItem> commentTypes = TypeSystemUtil.computeCommentTypes(object, typeScope, scopeProvider,
                qualifiedNameConverter, commentProvider, oldFormatComment);
            return commentTypes.isEmpty();
        }

        return types.isEmpty();
    }

    /**
     * Compute types with respect to system enumeration.
     *
     * @param object the object, cannot be {@code null}.
     * @param envs the environments, cannot be {@code null}.
     * @return the list of types, cannot return {@code null}.
     */
    protected List<TypeItem> computeTypes(EObject object, Environments envs)
    {
        List<TypeItem> types = typeComputer.computeTypes(object, envs);

        if (types.isEmpty() && object instanceof DynamicFeatureAccess
            && ((DynamicFeatureAccess)object).getSource() instanceof StaticFeatureAccess)
        {
            // Bypass system enum types with property without type
            DynamicFeatureAccess dfa = (DynamicFeatureAccess)object;
            StaticFeatureAccess sfa = (StaticFeatureAccess)dfa.getSource();
            List<TypeItem> sourceTypes = typeComputer.computeTypes(sfa, envs);
            if (sourceTypes.size() == 1)
            {
                String typeName = sfa.getName();
                String propertyName = dfa.getName();
                TypeItem type = sourceTypes.get(0);
                if ((typeName.equalsIgnoreCase(McoreUtil.getTypeNameRu(type))
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
     * Checks that collections of {@link TypeItem} have common item
     * @param expectedTypes expected collection of {@link TypeItem}, cannot be <code>null</code>
     * @param realTypes real collection of {@link TypeItem}, cannot be <code>null</code>
     * @param context {@link EObject} for resolving proxy checking types, cannot be <code>null</code>
     * @return <code>true</code> if intersection was detected
     */
    protected static boolean intersectTypeItem(List<TypeItem> expectedTypes, List<TypeItem> realTypes, EObject context)
    {
        if (expectedTypes.isEmpty())
        {
            return true;
        }
        List<TypeItem> parentTypes = getParentTypes(expectedTypes);
        parentTypes.addAll(expectedTypes);
        Collection<String> expectedTypesNames = getTypeNames(parentTypes, context);
        expectedTypesNames.addAll(getCastingType(expectedTypesNames));
        expectedTypesNames = Sets.newLinkedHashSet(expectedTypesNames);
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
        parentTypes = getParentTypes(realTypes);
        parentTypes.addAll(realTypes);
        Collection<String> realTypesNames = getTypeNames(parentTypes, context);

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

    private static List<TypeItem> getParentTypes(List<TypeItem> theFirstCollectionTypes)
    {
        List<TypeItem> types = new ArrayList<>(theFirstCollectionTypes);
        List<TypeItem> parentTypes = new ArrayList<>();
        Iterator<TypeItem> iterator = types.iterator();
        while (iterator.hasNext())
        {
            TypeItem type = iterator.next();
            while (type instanceof Type && ((Type)type).getParentType() != null)
            {
                parentTypes.add(((Type)type).getParentType());
                type = ((Type)type).getParentType();
            }
        }
        return parentTypes;
    }

    private static Collection<String> getTypeNames(List<TypeItem> parentTypes, EObject context)
    {
        Set<String> typeNames = new HashSet<>();
        for (TypeItem type : parentTypes)
        {
            String typeName = McoreUtil.getTypeName(type);
            String[] parts = typeName.split("\\."); //$NON-NLS-1$
            if (parts.length == 2)
            {
                typeNames.add(parts[0]);
                if (type.eIsProxy() && (IEObjectTypeNames.DEFINED_TYPE.equals(parts[0])
                    || IEObjectTypeNames.CHARACTERISTIC.equals(parts[0])))
                {
                    type = (TypeItem)EcoreUtil.resolve(type, context);
                }
            }
            typeNames.add(typeName);
            if (type instanceof TypeSet)
            {
                if (parts.length == 2)
                {
                    typeNames.addAll(((TypeSet)type).getTypes()
                        .stream()
                        .map(typeItem -> McoreUtil.getTypeName(typeItem))
                        .collect(Collectors.toList()));
                }
                else if (IEObjectTypeNames.ANY_REF.equals(typeName))
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

}
