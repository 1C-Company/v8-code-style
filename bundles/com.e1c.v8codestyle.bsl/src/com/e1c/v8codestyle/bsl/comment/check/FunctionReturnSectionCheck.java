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

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.TypeItem;
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
 *  Checks that documentation comment return section contains valid return types.
 *
 * @author Dmitriy Marmyshev
 *
 */
public class FunctionReturnSectionCheck
    extends AbstractDocCommentTypeCheck
{

    private static final String CHECK_ID = "doc-comment-return-section-type"; //$NON-NLS-1$

    private final IResourceLookup resourceLookup;

    private final IBslPreferences bslPreferences;

    private final IScopeProvider scopeProvider;

    private final BslMultiLineCommentDocumentationProvider commentProvider;

    private final IQualifiedNameConverter qualifiedNameConverter;

    @Inject
    public FunctionReturnSectionCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IBslPreferences bslPreferences, IQualifiedNameConverter qualifiedNameConverter,
        IScopeProvider scopeProvider, BslMultiLineCommentDocumentationProvider commentProvider)
    {
        super(resourceLookup, namingService, bmModelManager);
        this.resourceLookup = resourceLookup;
        this.bslPreferences = bslPreferences;
        this.qualifiedNameConverter = qualifiedNameConverter;
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
        builder.title(Messages.FunctionReturnSectionCheck_title)
            .description(Messages.FunctionReturnSectionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(453, getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(ReturnSection.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        Method method = root.getMethod();
        if (!(method instanceof Function))
        {
            return;
        }

        ReturnSection returnSection = (ReturnSection)object;

        if (isTypeEmptyAndNoLink(returnSection.getReturnTypes(), returnSection.getDescription()))
        {

            resultAceptor.addIssue(Messages.FunctionReturnSectionCheck_Return_type_is_mandatory,
                returnSection.getHeaderKeywordLength());
            return;
        }

        IScope typeScope = scopeProvider.getScope(method, McorePackage.Literals.TYPE_DESCRIPTION__TYPES);

        Collection<TypeItem> computedReturnTypes =
            root.computeReturnTypes(typeScope, scopeProvider, qualifiedNameConverter, commentProvider,
                oldCommentFormat(root.getModule()), method, typeComputationContext);

        if (computedReturnTypes.isEmpty())
        {
            resultAceptor.addIssue(Messages.FunctionReturnSectionCheck_Return_type_unknown,
                returnSection.getHeaderKeywordLength());
        }
    }

    private boolean oldCommentFormat(EObject context)
    {
        IProject project = resourceLookup.getProject(context);

        return bslPreferences.getDocumentCommentProperties(project).oldCommentFormat();
    }

}
