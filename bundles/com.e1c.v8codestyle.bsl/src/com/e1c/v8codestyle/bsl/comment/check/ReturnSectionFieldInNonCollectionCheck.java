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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks that field definitions in the return section of the documentation comment
 * are used only for collection types (Structure/Map/ValueTable/ValueTree).
 *
 * @author 1C-Soft LLC
 */
public class ReturnSectionFieldInNonCollectionCheck
    extends DocumentationCommentBasicDelegateCheck
{
    private static final String CHECK_ID = "doc-comment-return-section-field-in-non-collection"; //$NON-NLS-1$

    private static final String PARAMETER_COLLECTION_TYPES = "collectionTypes"; //$NON-NLS-1$

    private static final String TYPE_DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_COLLECTION_TYPES = String.join(TYPE_DELIMITER, Set.of(
        "структура", //$NON-NLS-1$
        "structure", //$NON-NLS-1$
        "соответствие", //$NON-NLS-1$
        "map", //$NON-NLS-1$
        "таблицазначений", //$NON-NLS-1$
        "valuetable", //$NON-NLS-1$
        "деревозначений", //$NON-NLS-1$
        "valuetree" //$NON-NLS-1$
    ));

    /**
     * Constructs an instance
     *
     * @param resourceLookup service for look up workspace resources, see {@link IResourceLookup}, cannot be <code>null</code>
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     */
    @Inject
    public ReturnSectionFieldInNonCollectionCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ReturnSectionFieldInNonCollectionCheck_title)
            .description(Messages.ReturnSectionFieldInNonCollectionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(ReturnSection.class);
        builder.parameter(PARAMETER_COLLECTION_TYPES, String.class, DEFAULT_COLLECTION_TYPES,
            Messages.ReturnSectionFieldInNonCollectionCheck_Collection_types);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        String parameterCollectionTypes = parameters.getString(PARAMETER_COLLECTION_TYPES);
        if (StringUtils.isBlank(parameterCollectionTypes))
        {
            return;
        }

        Set<String> allowedCollectionTypes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String[] paramTypes = parameterCollectionTypes.split(TYPE_DELIMITER);
        for (String type : paramTypes)
        {
            String trimmed = type.trim();
            if (!trimmed.isEmpty())
            {
                allowedCollectionTypes.add(trimmed.toLowerCase(Locale.ROOT));
            }
        }

        if (allowedCollectionTypes.isEmpty())
        {
            return;
        }

        ReturnSection returnSection = (ReturnSection)object;

        for (TypeSection typeSection : returnSection.getReturnTypes())
        {
            if (monitor.isCanceled())
            {
                return;
            }

            for (TypeDefinition typeDef : typeSection.getTypeDefinitions())
            {
                if (monitor.isCanceled())
                {
                    return;
                }

                List<FieldDefinition> fields = new ArrayList<>();
                collectFieldDefinitions(typeDef, fields);

                if (fields.isEmpty())
                {
                    continue;
                }

                String typeName = typeDef.getTypeName();
                if (typeName != null && allowedCollectionTypes.contains(typeName.toLowerCase(Locale.ROOT)))
                {
                    continue;
                }

                String message = MessageFormat.format(
                    Messages.ReturnSectionFieldInNonCollectionCheck_Field_only_allowed_for_collection_type_M,
                    typeName);

                resultAceptor.addIssue(message, typeDef.getLineNumber(), typeDef.getNameOffset(),
                    typeName == null ? 0 : typeName.length());
            }
        }
    }

    private static void collectFieldDefinitions(TypeDefinition typeDef, List<FieldDefinition> out)
    {
        if (typeDef == null)
        {
            return;
        }

        out.addAll(typeDef.getFieldDefinitionExtension());

        for (TypeDefinition containType : typeDef.getContainTypes())
        {
            collectFieldDefinitions(containType, out);
        }
    }
}
