/**
 * Copyright (C) 2025, 1C
 */
package com.e1c.v8codestyle.bsl.check;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.util.Triple;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.bsl.stringliteral.contenttypes.BslBuiltInLanguagePreferences;
import com._1c.g5.v8.dt.bsl.stringliteral.contenttypes.TypeUtil;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.v8.dt.check.BslDirectLocationIssue;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.DirectLocation;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks the correct placement of annotations for typing string literals.
 *
 * @author Babin Nikolay
 *
 */
public class StringLiteralTypeAnnotationCheck
    extends BasicCheck<Void>
{
    private static final String CHECK_ID = "string-literal-type-annotation-invalid-place"; //$NON-NLS-1$

    @Inject
    private IV8ProjectManager projectManager;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.StringLiteralTypeAnnotationCheck_Title)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.WARNING)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module();
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (!(object instanceof Module))
            return;

        Module module = (Module)object;

        if (!isApplyTagsToEntireExpression(module))
            return;

        ICompositeNode moduleNode = NodeModelUtils.findActualNodeFor(module);
        List<StringLiteral> moduleStringLiterals = new ArrayList<>();
        List<INode> moduleAnnotations = new ArrayList<>();
        if (moduleNode != null)
        {
            for (ILeafNode child : moduleNode.getLeafNodes())
            {
                if (monitor.isCanceled())
                    return;

                EObject semantic = NodeModelUtils.findActualSemanticObjectFor(child);
                if (semantic instanceof StringLiteral literal)
                {
                    moduleStringLiterals.add(literal);
                }
                if (child.isHidden() && BslCommentUtils.isCommentNode(child) && isAllowAnnotation(child.getText()))
                {
                    moduleAnnotations.add(child);
                }
            }
        }

        List<INode> invalidAnnotations = getInvalidAnnotations(monitor, moduleStringLiterals, moduleAnnotations);

        addIssues(resultAceptor, module, invalidAnnotations, monitor);
    }

    private void addIssues(ResultAcceptor resultAceptor, Module module, List<INode> invalidAnnotations,
        IProgressMonitor monitor)
    {
        for (INode annotation : invalidAnnotations)
        {
            if (monitor.isCanceled())
                return;

            int index = annotation.getText().indexOf("@"); //$NON-NLS-1$
            int offset = annotation.getTotalOffset() + index;

            int length = annotation.getText()
                .trim()
                .replaceFirst(BslCommentUtils.START_COMMENT_TAG_BSL, StringUtils.EMPTY)
                .trim()
                .toLowerCase()
                .length();

            DirectLocation directLocation = new DirectLocation(offset, length, annotation.getStartLine(), module);
            BslDirectLocationIssue directLocationIssue =
                new BslDirectLocationIssue(
                    Messages.StringLiteralTypeAnnotationCheck_Incorrect_annotation_location, directLocation,
                    StringUtils.EMPTY);

            resultAceptor.addIssue(directLocationIssue);
        }
    }

    private List<INode> getInvalidAnnotations(IProgressMonitor monitor, List<StringLiteral> moduleStringLiterals,
        List<INode> moduleAnnotations)
    {
        List<INode> invalidAnnotations = new ArrayList<>();

        Set<INode> correctAnnotations = new HashSet<>();
        for (StringLiteral literal : moduleStringLiterals)
        {
            if (monitor.isCanceled())
                return invalidAnnotations;

            EObject literalParent = findLiteralParent(literal);

            if (literalParent != null)
            {
                List<INode> rightLines = TypeUtil.getCommentLinesFromRight(literalParent)
                    .stream()
                    .filter(node -> isAllowAnnotation(node.getText()))
                    .toList();
                correctAnnotations.addAll(rightLines);
            }
        }

        for (INode node : moduleAnnotations)
        {
            if (monitor.isCanceled())
                return invalidAnnotations;

            if (!correctAnnotations.contains(node))
            {
                invalidAnnotations.add(node);
            }
        }
        return invalidAnnotations;
    }

    private boolean isApplyTagsToEntireExpression(EObject object)
    {
        IV8Project project = projectManager.getProject(object);

        return project != null && project.getProject() != null
            && BslBuiltInLanguagePreferences.isApplyTagsToEntireExpression(project.getProject());
    }

    private EObject findLiteralParent(StringLiteral literal)
    {
        for (EObject e = literal; e != null; e = e.eContainer())
        {
            EObject container = e.eContainer();
            //@formatter:off
            if (container instanceof com._1c.g5.v8.dt.bsl.model.Method
                || container instanceof RegionPreprocessor
                || container instanceof PreprocessorItem
                || container instanceof Conditional
                || container instanceof IfStatement
                || container instanceof TryExceptStatement
                || container instanceof LoopStatement)
            {
            //@formatter:on
                return e;
            }
        }
        return null;
    }

    private boolean isAllowAnnotation(String text)
    {
        List<Triple<String, Integer, String>> commentAnnotations = TypeUtil.parseHeaderAnnotations(text);
        return !commentAnnotations.isEmpty();
    }
}
