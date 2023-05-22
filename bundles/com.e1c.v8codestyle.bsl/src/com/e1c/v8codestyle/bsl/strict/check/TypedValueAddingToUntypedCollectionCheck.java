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
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.FEATURE_ACCESS__NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.SourceObjectLinkProvider;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.mcore.Method;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.dt.core.api.naming.INamingService;
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
    private static final String CHECK_ID = "typed-value-adding-to-untyped-collection"; //$NON-NLS-1$

    private static final String MAP_VALUE = "Value"; //$NON-NLS-1$

    //@formatter:off
    private static final Map<String, Map<String, Integer>> COLLECTION_ADD_METHODS = Map.of(
        "Add", Map.of(IEObjectTypeNames.ARRAY, 0, //$NON-NLS-1$
            IEObjectTypeNames.VALUE_LIST, 0),
        "Insert", Map.of( //$NON-NLS-1$
            IEObjectTypeNames.ARRAY, 1,
            IEObjectTypeNames.VALUE_LIST, 1),
        "Set", Map.of( //$NON-NLS-1$
            IEObjectTypeNames.ARRAY, 1)
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
        IQualifiedNameConverter qualifiedNameConverter, INamingService namingService, IBmModelManager bmModelManager)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager);
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
        IBmTransaction bmTransaction, IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof EObject))
        {
            return;
        }

        FeatureAccess fa = (FeatureAccess)object;
        EObject method = getSourceMethod(fa);

        if (!(method instanceof Method) || ((Method)method).getParamSet().isEmpty())
        {
            return;
        }

        Collection<TypeItem> expectedCollectionTypes = getExpectedCollectionTypes(fa, (Method)method);

        if (expectedCollectionTypes.isEmpty())
        {
            return;
        }

        Collection<TypeItem> actualTypes = getActualCollectionTypes(fa, expectedCollectionTypes);

        if (!actualTypes.isEmpty() && isActualCollectionItemTypeEmpty(actualTypes))
        {
            resultAceptor.addIssue(Messages.TypedValueAddingToUntypedCollectionCheck_title, FEATURE_ACCESS__NAME);
        }
    }

    private Collection<TypeItem> getExpectedCollectionTypes(FeatureAccess fa, Method method)
    {
        Collection<TypeItem> expectedTypes = new ArrayList<>();

        if (method instanceof SourceObjectLinkProvider)
        {
            return expectedTypes;
        }

        Invocation inv = BslUtil.getInvocation(fa);

        if (!(inv.getMethodAccess() instanceof DynamicFeatureAccess))
        {
            return expectedTypes;
        }

        Map<String, Integer> typesAndParams = COLLECTION_ADD_METHODS.get(method.getName());

        if (typesAndParams == null)
        {
            return expectedTypes;
        }

        TypeItem collectionType = EcoreUtil2.getContainerOfType(method, TypeItem.class);
        String typeName = collectionType == null ? null : McoreUtil.getTypeName(collectionType);

        if (typeName == null || !typesAndParams.containsKey(typeName))
        {
            return expectedTypes;
        }

        int parameterNumber = typesAndParams.get(typeName);

        if (inv.getParams().size() < parameterNumber + 1)
        {
            return expectedTypes;
        }

        expectedTypes = typeComputer
            .computeTypes(((DynamicFeatureAccess)inv.getMethodAccess()).getSource(), getActualEnvironments(inv));

        return expectedTypes;
    }

    private Collection<TypeItem> getActualCollectionTypes(FeatureAccess fa, Collection<TypeItem> expectedTypes)
    {
        Collection<TypeItem> actualTypes = new ArrayList<>();
        Invocation inv = BslUtil.getInvocation(fa);

        for (TypeItem type : expectedTypes)
        {
            type = (TypeItem)EcoreUtil.resolve(type, inv);

            if (type.getName().equals(IEObjectTypeNames.VALUE_LIST))
            {
                List<TypeItem> valueListItemTypes = ((Type)type).getCollectionElementTypes().allTypes();

                Set<TypeItem> collectionTypes =
                    dynamicFeatureAccessComputer.getAllProperties(valueListItemTypes, null)
                        .stream()
                        .flatMap(e -> e.getFirst().stream())
                        .filter(p -> p.getName().equals(MAP_VALUE))
                        .flatMap(p -> p.getTypes().stream())
                        .collect(Collectors.toSet());
                actualTypes.addAll(collectionTypes);
            }
            else
            {
                actualTypes.addAll(((Type)type).getCollectionElementTypes().allTypes());
            }
        }

        return actualTypes;
    }

    private boolean isActualCollectionItemTypeEmpty(Collection<TypeItem> actualTypes)
    {
        actualTypes.removeIf(p -> IEObjectTypeNames.ARBITRARY.equals(McoreUtil.getTypeName(p)));

        return actualTypes.isEmpty();
    }
}
