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
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
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
 * Validates that field name in documentation comment is valid name (not starts with number or does not contains any
 * special symbols except alphabet).
 *
 * @author Dmitriy Marmyshev
 *
 */
public class FieldDefinitionNameCheck
    extends DocumentationCommentBasicDelegateCheck
{
    private static final String CHECK_ID = "doc-comment-field-name"; //$NON-NLS-1$

    /**
     * Constructs an instance
     *
     * @param resourceLookup service for look up workspace resources, see {@link IResourceLookup}, cannot be <code>null</code>
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     */
    @Inject
    public FieldDefinitionNameCheck(IResourceLookup resourceLookup, INamingService namingService,
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
        builder.title(Messages.FieldDefinitionNameCheck_title)
            .description(Messages.FieldDefinitionNameCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(FieldDefinition.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        FieldDefinition fieldDef = (FieldDefinition)object;
        if (!StringUtils.isValidName(fieldDef.getName()))
        {

            String message = MessageFormat.format(Messages.FieldDefinitionNameCheck_Field_name__N__is_incorrect_name,
                fieldDef.getName());
            resultAceptor.addIssue(message, fieldDef.getLineNumber(), fieldDef.getNameOffset(),
                fieldDef.getName().length());
        }
    }
}
