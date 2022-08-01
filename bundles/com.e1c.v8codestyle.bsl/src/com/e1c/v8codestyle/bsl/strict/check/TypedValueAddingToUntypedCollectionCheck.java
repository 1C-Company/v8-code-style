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
package com.e1c.v8codestyle.bsl.strict.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.SourceObjectLinkProvider;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.mcore.Method;
import com._1c.g5.v8.dt.mcore.Type;
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
 * Checks that typed value is added to an untyped collection
 *
 * @author Timur Mukhamedishin
 */
public class TypedValueAddingToUntypedCollectionCheck
    extends AbstractTypeCheck
{
    private static final String MAP_VALUE = "Value"; //$NON-NLS-1$

    private static final String CHECK_ID = "typed-value-adding-to-untyped-collection"; //$NON-NLS-1$

    //@formatter:off
    private static final Map<String, Map<String, Collection<Integer>>> COLLECTION_ADD_METHODS = Map.of(
        "Add", Map.of(IEObjectTypeNames.ARRAY, Set.of(0), //$NON-NLS-1$
            IEObjectTypeNames.VALUE_LIST, Set.of(0))
        );

    /**
     * Instantiates a new typed value adding to untyped collection check
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     */
    @Inject
    public TypedValueAddingToUntypedCollectionCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.TypedValueAddingToUntypedCollectionCheck_title)
            .description(Messages.TypedValueAddingToUntypedCollectionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StrictTypeAnnotationCheckExtension())
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
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

        Method method = (Method)getSourceMethod(fa);

        if (method == null || method.getParamSet().isEmpty())
        {
            return;
        }

        Collection<TypeItem> collectionItemTypes =
            getCollectionItemTypes(inv, method);

        if (collectionItemTypes.isEmpty())
        {
            resultAceptor.addIssue(Messages.TypedValueAddingToUntypedCollectionCheck_title,
                BslPackage.Literals.EXPRESSION__TYPES);
        }
    }

    private Collection<TypeItem> getCollectionItemTypes(Invocation inv,
        Method method)
    {
        Collection<TypeItem> collectionItemTypes = new ArrayList<>();
        Environments actualEnvs = getActualEnvironments(inv);

        if(method instanceof SourceObjectLinkProvider || !(inv.getMethodAccess() instanceof DynamicFeatureAccess))
        {
            return collectionItemTypes;
        }

        Map<String, Collection<Integer>> typesAndParams = COLLECTION_ADD_METHODS.get(method.getName());

        if (typesAndParams == null)
        {
            return collectionItemTypes;
        }

        TypeItem collectionType = EcoreUtil2.getContainerOfType(method, TypeItem.class);
        String typeName = collectionType == null ? null : McoreUtil.getTypeName(collectionType);

        if (typeName == null || !typesAndParams.containsKey(typeName))
        {
            return collectionItemTypes;
        }

        List<TypeItem> types = typeComputer
            .computeTypes(((DynamicFeatureAccess)inv.getMethodAccess()).getSource(), actualEnvs);

        for (TypeItem type : types)
        {
            type = (TypeItem)EcoreUtil.resolve(type, inv);

            if (!(type instanceof Type) || !typeName.equals(McoreUtil.getTypeName(type)))
            {
                continue;
            }

            if (typeName.equals(IEObjectTypeNames.VALUE_LIST))
            {
                List<TypeItem> valueListItemTypes = ((Type)type).getCollectionElementTypes().allTypes();

                Set<TypeItem> collectionTypes =
                    dynamicFeatureAccessComputer.getAllProperties(valueListItemTypes, inv.eResource())
                        .stream()
                        .flatMap(e -> e.getFirst().stream())
                        .filter(p -> p.getName().equals(MAP_VALUE))
                        .flatMap(p -> p.getTypes().stream())
                        .collect(Collectors.toSet());
                collectionItemTypes.addAll(collectionTypes);
            }
            else
            {
                collectionItemTypes.addAll(((Type)type).getCollectionElementTypes().allTypes());
            }
        }
        // Remove Arbitrary type which do not need to check
        for (Iterator<TypeItem> iterator = collectionItemTypes.iterator(); iterator.hasNext();)
        {
            TypeItem typeItem = iterator.next();
            if (McoreUtil.getTypeName(typeItem).equals(IEObjectTypeNames.ARBITRARY))
            {
                iterator.remove();
            }
        }

        return collectionItemTypes;
    }
}
