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
import java.util.function.Predicate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.bsl.model.Block;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
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
import com.e1c.v8codestyle.bsl.AbstractBlockProcessor;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * The Check finds Structure/ValueTree/ValueTable type in parameter section
 * And if has property access to Structure/ValueTree/ValueTable comment must refer to constructor-function
 * 
 * @author Artem Samohvalov
 */
public class DocCommentConstructorReferenceCheck
    extends DocumentationCommentBasicDelegateCheck
{
    //@formatter:off
    private static final Set<String> CHECKED_TYPE_NAMES = Set.of(
        IEObjectTypeNames.STRUCTURE, IEObjectTypeNames.STRUCTURE_RU,
        IEObjectTypeNames.VALUE_TABLE, IEObjectTypeNames.VALUE_TABLE_RU,
        IEObjectTypeNames.VALUE_TREE, IEObjectTypeNames.VALUE_TREE_RU);
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
            // if in parameters checked type
            Optional<TypeDefinition> typeOptional =
                getTypeNamesFromField(fieldDefinition).stream().filter(td -> isCheckedType(td)).findFirst();
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
            var optionalKey = findPropertyCallKey(checkedVariableMap, bslDocumentationComment.getMethod());

            if (optionalKey.isPresent())
            {
                TypeDefinition typeDefinition = checkedVariableMap.get(optionalKey.get());
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
        // contains without case sense
        return typeName != null && CHECKED_TYPE_NAMES.stream().anyMatch(typeName::equalsIgnoreCase);
    }

    private Optional<String> findPropertyCallKey(Map<String, TypeDefinition> checkedVariableMap, Method method)
    {
        BlockProcessor blockProcessor =
            new BlockProcessor(method, (staticAccess) -> isCheckedVariable(staticAccess, checkedVariableMap));

        blockProcessor.process();

        return blockProcessor.getResult();
    }

    private boolean isCheckedVariable(StaticFeatureAccess staticAccess, Map<String, TypeDefinition> checkedVariableMap)
    {
        if (staticAccess.getFeatureEntries() != null && !staticAccess.getFeatureEntries().isEmpty())
        {
            EObject feature = staticAccess.getFeatureEntries().get(0).getFeature();

            if (feature instanceof Variable)
            {
                String name = staticAccess.getName();
                return name != null && checkedVariableMap.containsKey(name.toLowerCase());
            }
        }
        return false;
    }

    private static class BlockProcessor
        extends AbstractBlockProcessor
    {
        private final Predicate<StaticFeatureAccess> callBack;
        private Optional<String> result = Optional.empty();

        BlockProcessor(Block block, Predicate<StaticFeatureAccess> callBack)
        {
            super(block);

            this.callBack = callBack;
        }

        Optional<String> getResult()
        {
            return result;
        }

        @Override
        protected void doProcessInternal(Invocation invocation)
        {
            if (invocation.getMethodAccess() instanceof DynamicFeatureAccess dynamicAccess // if .
                && dynamicAccess.getSource() instanceof StaticFeatureAccess staticAccess // if variable
                && callBack.test(staticAccess))
            {
                result = Optional.of(staticAccess.getName().toLowerCase());
            }

            super.doProcessInternal(invocation);
        }

        @Override
        protected void doProcessInternal(DynamicFeatureAccess dynamicAccess)
        {
            if (dynamicAccess.getSource() instanceof StaticFeatureAccess staticAccess // if variable
                && callBack.test(staticAccess))
            {
                result = Optional.of(staticAccess.getName().toLowerCase());
            }

            super.doProcessInternal(dynamicAccess);
        }

        @Override
        protected void doProcessInternal(IndexAccess indexAccess)
        {
            if (indexAccess.getSource() instanceof StaticFeatureAccess staticAccess // if variable
                && callBack.test(staticAccess))
            {
                result = Optional.of(staticAccess.getName().toLowerCase());
            }

            super.doProcessInternal(indexAccess);
        }
    }
}
