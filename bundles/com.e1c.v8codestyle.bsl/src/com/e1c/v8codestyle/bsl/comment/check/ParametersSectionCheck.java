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

import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.comment.DocumentationCommentProperties;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 *  Validates that each of parameters specified in documenting comments.
 *
 * @author Maxim Degtyarev
 * @author Dmitriy Marmyshev
 *
 */
public class ParametersSectionCheck
    extends AbstractDocCommentTypeCheck
{

    private static final String CHECK_ID = "doc-comment-parameter-section"; //$NON-NLS-1$

    private static final String PARAMETER_CHECK_ONLY_EXPORT = "checkOnlyExport"; //$NON-NLS-1$

    private static final String PARAMETER_PARMA_SECT_FOR_EXPORT = "requireParameterSectionOnlyForExport"; //$NON-NLS-1$

    private final IResourceLookup resourceLookup;

    private final IBslPreferences bslPreferences;

    private final IScopeProvider scopeProvider;

    private final BslMultiLineCommentDocumentationProvider commentProvider;

    @Inject
    public ParametersSectionCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IBslPreferences bslPreferences, IScopeProvider scopeProvider,
        BslMultiLineCommentDocumentationProvider commentProvider, IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);
        this.resourceLookup = resourceLookup;
        this.bslPreferences = bslPreferences;
        this.scopeProvider = scopeProvider;
        this.commentProvider = commentProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ParametersSectionCheck_title)
            .description(Messages.ParametersSectionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(453, getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(ParametersSection.class, BslDocumentationComment.class);
        builder
            .parameter(PARAMETER_CHECK_ONLY_EXPORT, Boolean.class, Boolean.FALSE.toString(),
                Messages.ParametersSectionCheck_Check_only_export_method_parameter_section)
            .parameter(PARAMETER_PARMA_SECT_FOR_EXPORT, Boolean.class, Boolean.TRUE.toString(),
                Messages.ParametersSectionCheck_Require_parameter_section_only_for_Export_methods);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        if (root.getMethod().getFormalParams().isEmpty())
        {
            return;
        }

        if (object instanceof BslDocumentationComment)
        {
            check((BslDocumentationComment)object, resultAceptor, parameters, typeComputationContext, monitor);
        }
        else if (object instanceof ParametersSection)
        {
            check((ParametersSection)object, root, resultAceptor, parameters, monitor);
        }
    }

    private void check(ParametersSection object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (!root.getMethod().isExport() && parameters.getBoolean(PARAMETER_CHECK_ONLY_EXPORT))
        {
            return;
        }

        ParametersSection parameterSection = object;

        Set<String> parameterNames = getAbsentParameterDefinition(root.getMethod(), parameterSection);
        addIssues(root.getMethod(), parameterNames, resultAceptor);
    }

    private void check(BslDocumentationComment object, DocumentationCommentResultAcceptor resultAceptor,
        ICheckParameters parameters, BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        if (object.getParametersSection() != null
            || (!object.getMethod().isExport() && parameters.getBoolean(PARAMETER_PARMA_SECT_FOR_EXPORT)))
        {
            return;

        }

        BslDocumentationComment docComment = null;

        if (isInheritedFromLink(object))
        {
            LinkPart linkPart = getSingleLinkPart(object.getDescription());
            IProject project = resourceLookup.getProject(object.getModule());
            DocumentationCommentProperties props = bslPreferences.getDocumentCommentProperties(project);

            docComment = BslCommentUtils.getLinkPartCommentContent(linkPart, scopeProvider, commentProvider,
                props.oldCommentFormat(), object.getMethod(), v8ProjectManager, typeComputationContext);
        }
        if (docComment == null)
        {
            docComment = object;
        }

        Set<String> parameterNames =
            getAbsentParameterDefinition(object.getMethod(), docComment.getParametersSection());

        addIssues(object.getMethod(), parameterNames, resultAceptor);
    }

    private Set<String> getAbsentParameterDefinition(Method method, ParametersSection parameterSection)
    {
        Set<String> parameterNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        method.getFormalParams().forEach(p -> {
            if (p != null && StringUtils.isNotBlank(p.getName()))
            {
                parameterNames.add(p.getName());
            }
        });

        if (parameterSection == null)
        {
            return parameterNames;
        }

        for (FieldDefinition parameterDefinition : parameterSection.getParameterDefinitions())
        {
            parameterNames.remove(parameterDefinition.getName());
        }
        return parameterNames;
    }

    private void addIssues(Method method, Set<String> parameterNames, DocumentationCommentResultAcceptor resultAceptor)
    {
        if (parameterNames.isEmpty())
        {
            return;
        }

        for (FormalParam param : method.getFormalParams())
        {
            if (parameterNames.contains(param.getName()))
            {
                String message = MessageFormat
                    .format(Messages.ParametersSectionCheck_Parameter_definition_missed_for__N, param.getName());
                resultAceptor.addIssue(message, param, NAMED_ELEMENT__NAME);
                parameterNames.remove(param.getName());
            }
        }
    }

    private boolean isInheritedFromLink(BslDocumentationComment docComment)
    {
        return docComment.getReturnSection() == null && docComment.getParametersSection() == null
            && getSingleLinkPart(docComment.getDescription()) != null;
    }
}
