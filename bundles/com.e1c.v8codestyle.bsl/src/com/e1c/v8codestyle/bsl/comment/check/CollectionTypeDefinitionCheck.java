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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.LinkContainsTypeDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
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
import com.google.inject.Inject;

/**
 * Check type definition that collection type (like array, map etc.) has type of contain item.
 *
 * @author Dmitriy Marmyshev
 */
public class CollectionTypeDefinitionCheck
    extends DocumentationCommentBasicDelegateCheck
{

    private static final String CHECK_ID = "doc-comment-collection-item-type"; //$NON-NLS-1$

    private static final String PARAMETER_COLLECTION_TYPES = "collectionTypes"; //$NON-NLS-1$

    private static final String TYPE_DELIMITER = ","; //$NON-NLS-1$

    //@formatter:off
    private static final String DEFAULT_COLLECTION_TYPES = String.join(TYPE_DELIMITER, Set.of(
        IEObjectTypeNames.ARRAY, "Массив",  //$NON-NLS-1$
        IEObjectTypeNames.FIXED_ARRAY, "ФиксированныйМассив",  //$NON-NLS-1$
        IEObjectTypeNames.MAP, "Соответствие",  //$NON-NLS-1$
        IEObjectTypeNames.FIXED_MAP, "ФиксированноеСоответствие",  //$NON-NLS-1$
        IEObjectTypeNames.VALUE_LIST, "СписокЗначений",  //$NON-NLS-1$
        "FixedCollection", "ФиксированнаяКоллекция")); //$NON-NLS-1$ //$NON-NLS-2$
    //@formatter:on

    /**
     * Constructs an instance
     *
     * @param resourceLookup service for look up workspace resources, see {@link IResourceLookup}, cannot be <code>null</code>
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     */
    @Inject
    public CollectionTypeDefinitionCheck(IResourceLookup resourceLookup, INamingService namingService,
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
        builder.title(Messages.CollectionTypeDefinitionCheck_title)
            .description(Messages.CollectionTypeDefinitionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(453, getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(TypeDefinition.class);
        builder.parameter(PARAMETER_COLLECTION_TYPES, String.class, DEFAULT_COLLECTION_TYPES,
            Messages.CollectionTypeDefinitionCheck_Collection_types);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        if (object instanceof LinkContainsTypeDefinition)
        {
            return;
        }

        String parameterCollectionTypes = parameters.getString(PARAMETER_COLLECTION_TYPES);
        if (StringUtils.isBlank(parameterCollectionTypes))
        {
            return;
        }

        TypeDefinition typeDef = (TypeDefinition)object;

        String typeName = typeDef.getTypeName();

        if (StringUtils.isEmpty(typeName))
        {
            return;
        }

        Set<String> types = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String[] paramTypes = parameterCollectionTypes.split(TYPE_DELIMITER);
        types.addAll(List.of(paramTypes));
        if (typeDef.getContainTypes().isEmpty() && types.contains(typeName))
        {
            resultAceptor.addIssue(Messages.CollectionTypeDefinitionCheck_Collection_type_should_have_contain_item_type,
                typeName.length());
        }
    }
}
