/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.comment.check;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.e1c.v8codestyle.internal.bsl.ExpressionAccessHelper;
import com.google.common.base.Strings;
import com.google.inject.Inject;

/**
 * The Check finds Structure/ValueTree/ValueTable type in parameter section
 * And if there is access to the variable's properties or methods => the comment should refer to the constructor-function
 * 
 * @author Artem Samohvalov
 */
public class DocCommentConstructorReferenceCheck
    extends DocumentationCommentBasicDelegateCheck
{
    //@formatter:off
    private static final Set<String> CHECKED_TYPE_NAMES = Set.of(
        IEObjectTypeNames.STRUCTURE.toLowerCase(), 
        IEObjectTypeNames.STRUCTURE_RU.toLowerCase(),
        IEObjectTypeNames.VALUE_TABLE.toLowerCase(), 
        IEObjectTypeNames.VALUE_TABLE_RU.toLowerCase(),
        IEObjectTypeNames.VALUE_TREE.toLowerCase(), 
        IEObjectTypeNames.VALUE_TREE_RU.toLowerCase());
    //@formatter:on

    @Inject
    public DocCommentConstructorReferenceCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return "doc-comment-constructor-reference"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DocCommentConstructorReferenceCheck_Title)
            .description(Messages.DocCommentConstructorReferenceCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(453, getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(ParametersSection.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAcceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        ParametersSection parametersSection = (ParametersSection)object;

        Map<String, TypeDefinition> checkedVariableMap = new HashMap<>();
        for (FieldDefinition fieldDefinition : parametersSection.getParameterDefinitions())
        {
            if (Strings.isNullOrEmpty(fieldDefinition.getName()))
            {
                continue;
            }

            // if in parameters checked type
            Optional<TypeDefinition> typeOptional = getTypeNamesFromField(fieldDefinition).stream()
                .filter(td -> isCheckedType(td) && td.getParent() instanceof TypeSection ts
                    && !(hasDescriptionExtension(ts) || hasReferenceToConstructor(ts)))
                .findFirst();
            if (typeOptional.isPresent())
            {
                checkedVariableMap.put(fieldDefinition.getName().toLowerCase(), typeOptional.get());
            }
        }

        if (checkedVariableMap.isEmpty())
        {
            return;
        }

        if (parametersSection.getParent() instanceof BslDocumentationComment bslDocumentationComment)
        {
            Set<String> keys = findPropertyCallKeys(checkedVariableMap, bslDocumentationComment.getMethod());

            for (String key : keys)
            {
                TypeDefinition typeDefinition = checkedVariableMap.get(key);
                resultAcceptor.addIssue(Messages.DocCommentConstructorReferenceCheck_Issue,
                    typeDefinition.getLineNumber(), typeDefinition.getOffset(), typeDefinition.getTypeName().length());
            }
        }
    }

    private List<TypeDefinition> getTypeNamesFromField(FieldDefinition fieldDefinition)
    {
        return fieldDefinition.getTypeSections()
            .stream()
            .flatMap(listContainer -> listContainer.getTypeDefinitions().stream())
            .toList();
    }

    private boolean isCheckedType(TypeDefinition typeDefinition)
    {
        String typeName = typeDefinition.getTypeName();
        return typeName != null && CHECKED_TYPE_NAMES.contains(typeName.toLowerCase());
    }

    private boolean hasDescriptionExtension(TypeSection typeSection)
    {
        Description description = typeSection.getSourceExtensionDescription();
        return description != null && !description.getParts().isEmpty();
    }

    private boolean hasReferenceToConstructor(TypeSection typeSection)
    {
        Description description = typeSection.getDescription();
        if (description == null)
        {
            return false;
        }

        List<IDescriptionPart> parts = description.getParts();
        return !parts.isEmpty() && parts.get(0) instanceof LinkPart;
    }

    private Set<String> findPropertyCallKeys(Map<String, TypeDefinition> checkedVariableMap, Method method)
    {
        Stream<Expression> accesses =
            Stream.concat(EcoreUtil2.getAllContentsOfType(method, DynamicFeatureAccess.class).stream(),
                EcoreUtil2.getAllContentsOfType(method, IndexAccess.class).stream());

        return accesses.map(ExpressionAccessHelper::getBaseFeatureAccess)
            .flatMap(Optional::stream)
            .filter(sfa -> isCheckedVariable(sfa, checkedVariableMap))
            .map(sfa -> sfa.getName().toLowerCase())
            .collect(Collectors.toSet());
    }

    private boolean isCheckedVariable(StaticFeatureAccess staticAccess, Map<String, TypeDefinition> checkedVariableMap)
    {
        for (FeatureEntry entry : staticAccess.getFeatureEntries())
        {
            EObject feature = entry.getFeature();

            if (feature instanceof Variable)
            {
                String name = staticAccess.getName();
                if (name != null && checkedVariableMap.containsKey(name.toLowerCase()))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
