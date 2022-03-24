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
package com.e1c.v8codestyle.bsl.comment.check;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check field that it has definition of complex type and also reference to constructor function instead of just only
 * the reference to function.
 *
 * @author Dmitriy Marmyshev
 */
public class FieldDefinitionTypeWithLinkRefCheck
    extends AbstractDocCommentTypeCheck
{

    private static final String CHECK_ID = "doc-comment-complex-type-with-link"; //$NON-NLS-1$

    private static final String PARAMETER_COLLECTION_TYPES = "collectionTypes"; //$NON-NLS-1$

    private static final String TYPE_DELIMITER = ","; //$NON-NLS-1$

    //@formatter:off
    private static final String DEFAULT_COLLECTION_TYPES = String.join(TYPE_DELIMITER, Set.of(
        IEObjectTypeNames.STRUCTURE, IEObjectTypeNames.STRUCTURE_RU,
        IEObjectTypeNames.FIXED_STRUCTURE, "ФиксированнаяСтруктура", //$NON-NLS-1$
        IEObjectTypeNames.ARRAY, "Массив", //$NON-NLS-1$
        IEObjectTypeNames.FIXED_ARRAY, "ФиксированныйМассив", //$NON-NLS-1$
        IEObjectTypeNames.VALUE_TREE, IEObjectTypeNames.VALUE_TREE_RU,
        IEObjectTypeNames.VALUE_TREE_ROW_COLLECTION, "КоллекцияСтрокДереваЗначений", //$NON-NLS-1$
        IEObjectTypeNames.VALUE_TREE_ROW, "строкадеревазначений", //$NON-NLS-1$
        IEObjectTypeNames.VALUE_TREE_COLUMN_COLLECTION, "КоллекцияКолонокДереваЗначений", //$NON-NLS-1$
        IEObjectTypeNames.VALUE_TABLE, IEObjectTypeNames.VALUE_TABLE_RU,
        IEObjectTypeNames.VALUE_TABLE_ROW, "СтрокаТаблицыЗначений",  //$NON-NLS-1$
        IEObjectTypeNames.VALUE_TABLE_COLUMN_COLLECTION, "КоллекцияКолонокТаблицыЗначений",  //$NON-NLS-1$
        IEObjectTypeNames.MAP, "Соответствие", //$NON-NLS-1$
        IEObjectTypeNames.FIXED_MAP, "ФиксированноеСоответствие",  //$NON-NLS-1$
        IEObjectTypeNames.VALUE_LIST, "СписокЗначений"  //$NON-NLS-1$
        ));
    //@formatter:on

    private final IScopeProvider scopeProvider;

    /**
     * Instantiates a new field definition type with link reference to constructor function check.
     *
     * @param scopeProvider the scope provider service, cannot be {@code null}.
     */
    @Inject
    public FieldDefinitionTypeWithLinkRefCheck(IScopeProvider scopeProvider)
    {
        this.scopeProvider = scopeProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FieldDefinitionTypeWithLinkRefCheck_title)
            .description(Messages.FieldDefinitionTypeWithLinkRefCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(FieldDefinition.class);
        builder.parameter(PARAMETER_COLLECTION_TYPES, String.class, DEFAULT_COLLECTION_TYPES,
            Messages.CollectionTypeDefinitionCheck_Collection_types);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        String parameterCollectionTypes = parameters.getString(PARAMETER_COLLECTION_TYPES);
        if (StringUtils.isBlank(parameterCollectionTypes))
        {
            return;
        }

        Set<String> types = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String[] paramTypes = parameterCollectionTypes.split(TYPE_DELIMITER);
        types.addAll(List.of(paramTypes));

        FieldDefinition fieldDef = (FieldDefinition)object;
        if (isFieldTypeWithLinkRef(fieldDef, types, root.getMethod()))
        {
            String message = MessageFormat.format(
                Messages.FieldDefinitionTypeWithLinkRefCheck_Field__F__use_declaration_of_complex_type_instead_of_link,
                fieldDef.getName());

            resultAceptor.addIssue(message, fieldDef.getName().length());
        }
    }

    private boolean isFieldTypeWithLinkRef(FieldDefinition fieldDef, Set<String> types, EObject context)
    {
        if (fieldDef.getTypeSections().size() == 1 && isComplexType(fieldDef.getTypeSections().get(0), types))
        {
            LinkPart linkPart = getSingleLinkPartForField(fieldDef);
            return linkPart != null && isLinkPartObjectExist(linkPart, scopeProvider, context);
        }
        return false;
    }

    private boolean isComplexType(TypeSection typeSection, Set<String> types)
    {
        if (typeSection.getTypeDefinitions().size() == 1)
        {
            TypeDefinition type = typeSection.getTypeDefinitions().get(0);
            if (!type.getContainTypes().isEmpty())
            {
                return false;
            }
            String typeName = type.getTypeName();
            return types.contains(typeName);
        }
        return false;
    }

    private LinkPart getSingleLinkPartForField(FieldDefinition fieldDef)
    {
        if (fieldDef.getTypeSections().size() != 1)
        {
            return null;
        }

        TypeSection typeSection = fieldDef.getTypeSections().get(0);

        return getSingleLinkPart(typeSection.getDescription());
    }

}
