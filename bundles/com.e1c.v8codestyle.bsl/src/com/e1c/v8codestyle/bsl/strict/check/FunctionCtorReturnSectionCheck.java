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

import static com.e1c.v8codestyle.bsl.strict.check.StrictTypeAnnotationCheckExtension.PARAM_CHECK_ANNOTATION_IN_MODULE_DESCRIPTION;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.util.Pair;

import com._1c.g5.v8.dt.bsl.comment.DocumentationCommentProperties;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.model.BslPackage.Literals;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.Property;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

/**
 *  Documenting comment validator.
 *  Validate user's data types of function constructor in return types specified in documenting comments.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class FunctionCtorReturnSectionCheck
    extends DocumentationCommentBasicDelegateCheck
{

    private static final String CHECK_ID = "constructor-function-return-section"; //$NON-NLS-1$

    private static final String PARAM_CHECK_TYPES = "checkTypes"; //$NON-NLS-1$

    private static final Set<String> DEFAULT_CHECK_TYPES = Set.of(IEObjectTypeNames.STRUCTURE,
        IEObjectTypeNames.FIXED_STRUCTURE, IEObjectTypeNames.VALUE_TABLE, IEObjectTypeNames.VALUE_TREE);

    private final TypesComputer typesComputer;

    private final DynamicFeatureAccessComputer dynamicComputer;

    private final IScopeProvider scopeProvider;

    private final IQualifiedNameConverter qualifiedNameConverter;

    private final BslMultiLineCommentDocumentationProvider commentProvider;

    private final IResourceLookup resourceLookup;

    private final IV8ProjectManager v8ProjectManager;

    private final IBslPreferences bslPreferences;

    /**
     * Instantiates a new function constructor documentation comment return section check.
     *
     * @param resourceLookup the resource lookup service, cannot be {@code null}.
     * @param v8ProjectManager the v8 project manager service, cannot be {@code null}.
     * @param qualifiedNameConverter the qualified name converter service, cannot be {@code null}.
     * @param bslPreferences the BSL preferences service, cannot be {@code null}.
     * @param typesComputer the types computer service, cannot be {@code null}.
     * @param dynamicComputer the dynamic computer service, cannot be {@code null}.
     * @param scopeProvider the scope provider service, cannot be {@code null}.
     * @param commentProvider the comment provider service, cannot be {@code null}.
     */
    @Inject
    public FunctionCtorReturnSectionCheck(IResourceLookup resourceLookup, IV8ProjectManager v8ProjectManager,
        IQualifiedNameConverter qualifiedNameConverter, IBslPreferences bslPreferences, TypesComputer typesComputer,
        DynamicFeatureAccessComputer dynamicComputer, IScopeProvider scopeProvider,
        BslMultiLineCommentDocumentationProvider commentProvider)
    {
        super();
        this.typesComputer = typesComputer;
        this.dynamicComputer = dynamicComputer;
        this.scopeProvider = scopeProvider;
        this.commentProvider = commentProvider;
        this.qualifiedNameConverter = qualifiedNameConverter;
        this.resourceLookup = resourceLookup;
        this.v8ProjectManager = v8ProjectManager;
        this.bslPreferences = bslPreferences;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FunctionCtorReturnSectionCheck_title)
            .description(Messages.FunctionCtorReturnSectionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .delegate(ReturnSection.class);
        builder.parameter(PARAM_CHECK_TYPES, String.class, String.join(",", DEFAULT_CHECK_TYPES), //$NON-NLS-1$
            Messages.FunctionCtorReturnSectionCheck_User_extandable_Data_type_list_comma_separated);
        builder.parameter(PARAM_CHECK_ANNOTATION_IN_MODULE_DESCRIPTION, Boolean.class, Boolean.FALSE.toString(),
            Messages.StrictTypeAnnotationCheckExtension_Check__strict_types_annotation_in_module_desctioption);

    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (monitor.isCanceled()
            || !(root.getMethod() instanceof Function)
            || parameters.getBoolean(PARAM_CHECK_ANNOTATION_IN_MODULE_DESCRIPTION)
                && !StrictTypeUtil.hasStrictTypeAnnotation(root.getModule()))
        {
            return;
        }

        ReturnSection returnSection = (ReturnSection)object;

        Function method = (Function)root.getMethod();

        IScope typeScope = scopeProvider.getScope(method, McorePackage.Literals.TYPE_DESCRIPTION__TYPES);

        IProject project = resourceLookup.getProject(root.getModule());

        boolean isRussianScript = isRussianScriptVariantProject(project);

        DocumentationCommentProperties props = bslPreferences.getDocumentCommentProperties(project);

        boolean oldFormat = props.oldCommentFormat();

        Collection<TypeItem> computedReturnTypes = root.computeReturnTypes(typeScope, scopeProvider,
            qualifiedNameConverter, commentProvider, oldFormat, method);

        Set<String> checkTypes = getCheckTypes(parameters);

        List<String> computedReturnTypeNames =
            computedReturnTypes.stream().map(McoreUtil::getTypeName).collect(Collectors.toList());
        if (isUserDataTypes(computedReturnTypeNames, checkTypes))
        {

            //@formatter:off
            List<ReturnStatement> returns = method.allStatements()
                .stream()
                .filter(ReturnStatement.class::isInstance)
                .map(ReturnStatement.class::cast)
                .collect(Collectors.toList());
            //@formatter:on

            Resource res = method.eResource();

            Collection<Pair<Collection<Property>, TypeItem>> coputedProperties =
                dynamicComputer.getAllProperties(computedReturnTypes, res);

            for (ReturnStatement statment : returns)
            {
                List<TypeItem> returnTypes =
                    typesComputer.computeTypes(statment.getExpression(), method.environments());

                Collection<Pair<Collection<Property>, TypeItem>> properties =
                    dynamicComputer.getAllProperties(returnTypes, res);

                for (TypeItem returnType : returnTypes)
                {
                    String returnTypeName = McoreUtil.getTypeName(returnType);
                    if (computedReturnTypeNames.contains(returnTypeName))
                    {
                        if (isUserDataTypes(List.of(returnTypeName), checkTypes))
                        {
                            Optional<Pair<Collection<Property>, TypeItem>> declaredProperties =
                                coputedProperties.stream()
                                    .filter(t -> McoreUtil.getTypeName(t.getSecond()).equals(returnTypeName))
                                    .findAny();
                            Optional<Pair<Collection<Property>, TypeItem>> typeProperties = properties.stream()
                                .filter(t -> McoreUtil.getTypeName(t.getSecond()).equals(returnTypeName))
                                .findAny();

                            checkTypeProperties(method, statment, isRussianScript, returnType,
                                declaredProperties.orElse(null), typeProperties.orElse(null), resultAceptor);
                        }
                    }
                    else if (isWarningReturnNonDeclaredType(method, returnSection, returnType, computedReturnTypes))
                    {
                        addWarningReturnNonDeclaredType(statment, isRussianScript, returnType, returnTypeName,
                            resultAceptor);
                    }
                }
            }
        }
    }

    private Set<String> getCheckTypes(ICheckParameters parameters)
    {
        String[] types = parameters.getString(PARAM_CHECK_TYPES).replace(" ", " ").split(","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Set<String> checkTypes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        checkTypes.addAll(Set.of(types));
        return checkTypes;
    }

    private void checkTypeProperties(Function function, ReturnStatement statment, boolean useRussianScript,
        TypeItem returnType, Pair<Collection<Property>, TypeItem> declaredProperties,
        Pair<Collection<Property>, TypeItem> typeProperties, DocumentationCommentResultAcceptor resultAceptor)
    {
        if (declaredProperties == null || typeProperties == null)
        {
            return;
        }

        List<String> declaredProertyNames = new ArrayList<>();

        // check declared properties and types
        for (Property declaredProperty : declaredProperties.getFirst())
        {
            String propertyName = useRussianScript ? declaredProperty.getNameRu() : declaredProperty.getName();
            declaredProertyNames.add(propertyName);
            List<String> declaredType =
                declaredProperty.getTypes().stream().map(McoreUtil::getTypeName).collect(Collectors.toList());
            if (declaredType.isEmpty())
            {
                continue;
            }

            List<TypeItem> types = typeProperties.getFirst()
                .stream()
                .filter(p -> useRussianScript ? p.getNameRu().equals(declaredProperty.getNameRu())
                    : p.getName().equals(declaredProperty.getName()))
                .flatMap(p -> p.getTypes().stream())
                .collect(Collectors.toList());

            List<TypeItem> types2 = types.stream()
                .filter(t -> !declaredType.contains(McoreUtil.getTypeName(t)))
                .collect(Collectors.toList());
            if (types.isEmpty())
            {
                addWarningDeclaredNonReturningProperty(statment, useRussianScript, declaredProperty, resultAceptor);
            }
            else if (!types2.isEmpty())
            {
                addWarningDeclaredNonReturningPropertyType(statment, useRussianScript, declaredProperty, types2,
                    resultAceptor);
            }
        }

        // check for non declared properties
        if (isWarningReturnNonDeclaredProperty(function, statment, declaredProperties.getFirst()))
        {
            List<String> nonDeclaredProperties = typeProperties.getFirst()
                .stream()
                .map(useRussianScript ? Property::getNameRu : Property::getName)
                .filter(Predicates.not(declaredProertyNames::contains))
                .collect(Collectors.toList());
            if (!nonDeclaredProperties.isEmpty())
            {
                addWarningNonDeclaredReturningProperty(statment, useRussianScript, nonDeclaredProperties,
                    resultAceptor);
            }
        }
    }

    private void addWarningReturnNonDeclaredType(ReturnStatement statment, boolean isRussianScript, TypeItem returnType,
        String returnTypeName, DocumentationCommentResultAcceptor resultAceptor)
    {
        final String message = MessageFormat.format(Messages.FunctionCtorReturnSectionCheck_Return_non_declared_type__T,
            isRussianScript ? McoreUtil.getTypeNameRu(returnType) : returnTypeName);

        resultAceptor.addIssue(message, statment, Literals.RETURN_STATEMENT__EXPRESSION);
    }

    private boolean isWarningReturnNonDeclaredType(Function function, ReturnSection returnSection, TypeItem returnType,
        Collection<TypeItem> computedReturnTypes)
    {
        return function.isExport();
    }

    private boolean isUserDataTypes(List<String> computedReturnTypeNames, Set<String> checkTypes)
    {
        if (computedReturnTypeNames.isEmpty())
        {
            return false;
        }

        for (String typeName : computedReturnTypeNames)
        {
            if (checkTypes.contains(typeName))
            {
                return true;
            }
        }
        return false;
    }

    private void addWarningDeclaredNonReturningProperty(ReturnStatement statment, boolean useRussianScript,
        Property property, DocumentationCommentResultAcceptor resultAceptor)
    {
        final String message = MessageFormat.format(
            Messages.FunctionCtorReturnSectionCheck_Declared_property__N__with_type__T__not_returning,
            useRussianScript ? property.getNameRu() : property.getName(), String.join(", ", //$NON-NLS-1$
                property.getTypes()
                    .stream()
                    .map(useRussianScript ? McoreUtil::getTypeNameRu : McoreUtil::getTypeName)
                    .collect(Collectors.toList())));

        resultAceptor.addIssue(message, statment, Literals.RETURN_STATEMENT__EXPRESSION);
    }

    private void addWarningDeclaredNonReturningPropertyType(ReturnStatement statment, boolean useRussianScript,
        Property property, List<TypeItem> missingTypes, DocumentationCommentResultAcceptor resultAceptor)
    {
        final String message = MessageFormat.format(
            Messages.FunctionCtorReturnSectionCheck_Declared_property__N__with_type__T__missing_returning_types__M,
            useRussianScript ? property.getNameRu() : property.getName(), String.join(", ", //$NON-NLS-1$
                property.getTypes()
                    .stream()
                    .map(useRussianScript ? McoreUtil::getTypeNameRu : McoreUtil::getTypeName)
                    .collect(Collectors.toList())),
            String.join(", ", //$NON-NLS-1$
                missingTypes.stream()
                    .map(useRussianScript ? McoreUtil::getTypeNameRu : McoreUtil::getTypeName)
                    .collect(Collectors.toList())));

        resultAceptor.addIssue(message, statment, Literals.RETURN_STATEMENT__EXPRESSION);

    }

    private boolean isWarningReturnNonDeclaredProperty(Function function, ReturnStatement statment,
        Collection<Property> computedReturnProperties)
    {
        return function.isExport();
    }

    private void addWarningNonDeclaredReturningProperty(ReturnStatement statment, boolean useRussianScript,
        Collection<String> properties, DocumentationCommentResultAcceptor resultAceptor)
    {

        final String message = MessageFormat.format(
            Messages.FunctionCtorReturnSectionCheck_Return_non_declared_propertes__N, String.join(", ", properties)); //$NON-NLS-1$

        resultAceptor.addIssue(message, statment, Literals.RETURN_STATEMENT__EXPRESSION);

    }

    private boolean isRussianScriptVariantProject(IProject project)
    {
        IV8Project v8Project = v8ProjectManager.getProject(project);

        return (v8Project != null) && (v8Project.getScriptVariant() == ScriptVariant.RUSSIAN);
    }

}
