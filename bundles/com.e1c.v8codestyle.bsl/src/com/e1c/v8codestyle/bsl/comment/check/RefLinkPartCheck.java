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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Section;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.validation.BslPreferences;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
import com.e1c.g5.v8.dt.check.BslDirectLocationIssue;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.DirectLocation;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.BslUtils;
import com.e1c.v8codestyle.bsl.check.SkipAdoptedInExtensionModuleOwnerExtension;
import com.google.inject.Inject;

/**
 * Validates {@link LinkPart} of documentation comment that it referenced to an existing object.
 * This check allows to skip  link with single word in {@link Description}
 * <pre>
 * // Method description here.
 * // And also see something here...
 * </pre>
 * but it validates this
 * <pre>
 * // Method description here.
 * // And also see some.thing here
 * </pre>
 *  as reference to method "thing" of common module "some".
 *
 * @author Dmitriy Marmyshev
 */
public class RefLinkPartCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "doc-comment-ref-link"; //$NON-NLS-1$

    private static final String PARAMETER_ALLOW_SEE_IN_DESCRIPTION = "allowSeeInDescription"; //$NON-NLS-1$

    private final IScopeProvider scopeProvider;

    private final IResourceLookup resourceLookup;

    private final BslPreferences bslPreferences;

    private final IBmModelManager bmModelManager;

    private final INamingService namingService;

    private final BslMultiLineCommentDocumentationProvider commentProvider;

    private Boolean oldCommentFormat;

    /**
     * Instantiates a new reference link part check.
     *
     * @param scopeProvider the scope provider service, cannot be {@code null}.
     */
    @Inject
    public RefLinkPartCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IScopeProvider scopeProvider, BslPreferences bslPreferences,
        BslMultiLineCommentDocumentationProvider commentProvider)
    {
        this.scopeProvider = scopeProvider;
        this.bslPreferences = bslPreferences;
        this.resourceLookup = resourceLookup;
        this.bmModelManager = bmModelManager;
        this.namingService = namingService;
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
        builder.title(Messages.RefLinkPartCheck_title)
            .description(Messages.RefLinkPartCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new SkipAdoptedInExtensionModuleOwnerExtension())
            .extension(new ModuleTopObjectNameFilterExtension())
            .module()
            .checkedObjectType(MODULE);

        builder.parameter(PARAMETER_ALLOW_SEE_IN_DESCRIPTION, Boolean.class, Boolean.TRUE.toString(),
            Messages.RefLinkPartCheck_Allow_See_in_description);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        Module module = (Module)object;
        for (Method method : module.allMethods())
        {
            checkMethodDocComment(method, module, resultAcceptor, parameters);
            checkMethodInternalComments(method, module, resultAcceptor, parameters);
        }
    }

    private void checkMethodDocComment(Method method, Module module, ResultAcceptor resultAcceptor,
        ICheckParameters parameters)
    {
        List<INode> nodes = commentProvider.getDocumentationNodes(method);

        BslDocumentationComment comment = BslCommentUtils.parseTemplateComment(method, true, commentProvider);
        List<IDescriptionPart> parts = getCommentParts(comment);

        int[] offsets = BslCommentUtils.getCommentLineLocalOffsets(nodes, method);

        checkDesctriptionPartsInternal(method, module, resultAcceptor, parameters, offsets, parts);
    }

    private void checkMethodInternalComments(Method method, Module module, BasicCheck.ResultAcceptor resultAcceptor,
        ICheckParameters parameters)
    {
        List<List<INode>> allComments = BslUtils.findAllInternalMethodComments(method);

        for (List<INode> comments : allComments)
        {
            Map<String, List<INode>> lines = BslUtils.getCommentLines(comments);

            List<INode> nodes = lines.values().stream().flatMap(List::stream).collect(Collectors.toList());

            int[] offsets = BslCommentUtils.getCommentLineLocalOffsets(nodes, method);

            List<String> text = new ArrayList<>(nodes.stream().map(INode::getText).collect(Collectors.toList()));

            BslDocumentationComment comment =
                BslCommentUtils.parseTemplateComment(text, method, isOldCommentFormat(method));

            List<IDescriptionPart> parts = getCommentParts(comment);

            checkDesctriptionPartsInternal(method, module, resultAcceptor, parameters, offsets, parts);
        }
    }

    private void checkDesctriptionPartsInternal(Method method, Module module, BasicCheck.ResultAcceptor resultAceptor,
        ICheckParameters parameters, int[] offsets, List<IDescriptionPart> parts)
    {
        for (int i = 0; i < parts.size(); i++)
        {
            IDescriptionPart part = parts.get(i);
            if (part instanceof LinkPart)
            {
                LinkPart link = (LinkPart)part;

                int globalOffset = 0;
                if (offsets.length <= link.getLineNumber())
                {
                    globalOffset = offsets[link.getLineNumber()];
                }

                link.setLinkTextOffset(globalOffset + link.getLinkTextOffset());

                checkLinkPartInternal(link, module, method, resultAceptor, parameters);
            }
        }
    }

    private List<IDescriptionPart> getCommentParts(BslDocumentationComment comment)
    {
        List<IDescriptionPart> result = new ArrayList<>();

        result.addAll(getLinkParts(comment.getDescription().getParts()));

        ReturnSection returnSection = comment.getReturnSection();
        if (returnSection != null)
        {
            result.addAll(getLinkParts(returnSection.getDescription().getParts()));

            if (returnSection.getReturnTypes() != null)
            {
               List<IDescriptionPart> parts = returnSection.getReturnTypes()
                   .stream()
                   .flatMap(t -> t.getDescription().getParts().stream())
                   .collect(Collectors.toList());
               result.addAll(getLinkParts(parts));
            }
        }

        ParametersSection parametersSection = comment.getParametersSection();
        if (parametersSection != null)
        {
            result.addAll(getLinkParts(parametersSection
                .getDescription()
                .getParts()));

            if (parametersSection.getParameterDefinitions() != null)
            {
                List<IDescriptionPart> parts = parametersSection.getParameterDefinitions()
                    .stream()
                    .flatMap(p -> p.getTypeSections().stream())
                    .flatMap(t -> t.getDescription().getParts().stream())
                    .collect(Collectors.toList());

                result.addAll(getLinkParts(parts));
            }
        }

        Section exampleSection = comment.getExampleSection();
        if (exampleSection != null)
        {
            result.addAll(getLinkParts(exampleSection.getDescription().getParts()));
        }

        return result;
    }

    private List<IDescriptionPart> getLinkParts(List<IDescriptionPart> parts)
    {
        return parts.stream()
            .filter(LinkPart.class::isInstance)
            .collect(Collectors.toList());
    }

    private void checkLinkPartInternal(LinkPart linkPart, Module module, Method method,
        BasicCheck.ResultAcceptor resultAceptor,
        ICheckParameters parameters)
    {
        boolean isSingleWordInDescription = parameters.getBoolean(PARAMETER_ALLOW_SEE_IN_DESCRIPTION)
            && linkPart.getParent() instanceof Description && linkPart.getPartsWithOffset().size() == 1;

        IDtProject project = resourceLookup.getDtProject(module);

        bmModelManager.executeReadOnlyTask(project, transaction -> {
            BmOperationContext bmOperationContext = new BmOperationContext(namingService, bmModelManager, transaction);
            if (!isWebLink(linkPart) && !isSingleWordInDescription
                && BslUtils.getLinkPartLastObject(linkPart, scopeProvider, method, bmOperationContext).isEmpty())
            {
                DirectLocation directLocation = new DirectLocation(linkPart.getLinkTextOffset(),
                    linkPart.getLinkText().length(), linkPart.getLineNumber(), module);

                BslDirectLocationIssue directLocationIssue = new BslDirectLocationIssue(
                    Messages.RefLinkPartCheck_Link_referenced_to_unexisting_object, directLocation);

                resultAceptor.addIssue(directLocationIssue);
            }
            return null;
        });
    }

    private boolean isWebLink(LinkPart linkPart)
    {
        String text = linkPart.getLinkText();
        if (StringUtils.isEmpty(text))
        {
            // just skip other checks
            return true;
        }
        return text.startsWith("http://") || text.startsWith("https://") || text.startsWith("ftp://"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private boolean isOldCommentFormat(Method method)
    {
        if (oldCommentFormat == null)
        {
            IProject project = resourceLookup.getProject(method);
            oldCommentFormat = bslPreferences.getDocumentCommentProperties(project).oldCommentFormat();
        }
        return oldCommentFormat.booleanValue();
    }
}
