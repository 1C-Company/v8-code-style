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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.LinkContainsTypeDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check type definition contains valid existing type.
 *
 * @author Dmitriy Marmyshev
 */
public class TypeDefinitionCheck
    extends AbstractDocCommentTypeCheck
{

    private static final String CHECK_ID = "doc-comment-type"; //$NON-NLS-1$

    private final IScopeProvider scopeProvider;

    private final IQualifiedNameConverter qualifiedNameConverter;

    @Inject
    public TypeDefinitionCheck(IQualifiedNameConverter qualifiedNameConverter, IScopeProvider scopeProvider)
    {
        this.qualifiedNameConverter = qualifiedNameConverter;
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
        builder.title(Messages.TypeDefinitionCheck_title)
            .description(Messages.TypeDefinitionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(TypeDefinition.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (object instanceof LinkContainsTypeDefinition)
        {
            return;
        }

        TypeDefinition typeDef = (TypeDefinition)object;

        String typeName = typeDef.getTypeName();

        if (StringUtils.isEmpty(typeName))
        {
            return;
        }

        IScope typeScope = scopeProvider.getScope(root.getMethod(), McorePackage.Literals.TYPE_DESCRIPTION__TYPES);

        IEObjectDescription obj = typeScope.getSingleElement(qualifiedNameConverter.toQualifiedName(typeName));

        if (obj == null)
        {
            String message = MessageFormat.format(Messages.TypeDefinitionCheck_Unkown_type_M_specified, typeName);

            resultAceptor.addIssue(message, typeName.length());
        }
    }

}
