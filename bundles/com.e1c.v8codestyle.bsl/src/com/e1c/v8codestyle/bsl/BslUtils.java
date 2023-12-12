/**
 * Copyright (C) 2023, 1C-Soft LLC and others.
 */
package com.e1c.v8codestyle.bsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.model.Method;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;

/**
 * @author Nikolay Babin
 *
 */
public final class BslUtils
{

    private BslUtils()
    {
        // empty
    }

    /**
     * Find all internal method comments.
     *
     * @param method the method, cannot be <code>null</code>
     * @return the list of comment lists
     */
    public static List<List<INode>> findAllInternalMethodComments(Method method)
    {
        ICompositeNode root = NodeModelUtils.getNode(method);

        List<List<INode>> allComments = new ArrayList<>();

        List<INode> found = new ArrayList<>();

        int skipBefore = root.getOffset();
        int until = root.getEndOffset();
        Iterator<ILeafNode> it = root.getLeafNodes().iterator();
        while (it.hasNext())
        {
            ILeafNode leafNode = it.next();
            if (leafNode.getOffset() < skipBefore)
            {
                continue;
            }
            if (leafNode.getOffset() >= until)
            {
                return allComments;
            }

            if (leafNode.isHidden() && BslCommentUtils.isCommentNode(leafNode))
            {
                found.add(leafNode);
            }
            else if (!found.isEmpty() && (!leafNode.isHidden() || leafNode.getText().indexOf('\n') != -1))
            {
                allComments.add(found);
                found = new ArrayList<>();
            }
        }
        if (!found.isEmpty())
        {
            allComments.add(found);
        }

        return allComments;
    }

    /**
     * Gets string content of the comment lines.
     *
     * @param nodes the adjacent nodes
     * @return the comment lines
     */
    public static Map<String, List<INode>> getCommentLines(List<INode> nodes)
    {
        Map<String, List<INode>> result = new HashMap<>();
        for (INode node : nodes)
            result.compute(node.getText(), (key, list) -> {
                if (list == null)
                {
                    list = new LinkedList<>();
                }
                list.add(node);
                return list;
            });
        return result;
    }

    /**
     * Gets object of last segment of the link to method/parameter,
     * without final brackets "(See ModuleName.MethodName.)", or witn ending dot "See ModuleName.MethodName."
     *
     * @param linkPart the link part, cannot be <code>null</code>
     * @param scopeProvider the scope provider, cannot be <code>null</code>
     * @param context the context, cannot be <code>null</code>
     * @param typeComputationContext the type computation context, cannot be <code>null</code>
     * @return the link part last object
     */
    public static Optional<EObject> getLinkPartLastObject(LinkPart linkPart, IScopeProvider scopeProvider,
        EObject context,
        BmOperationContext typeComputationContext)
    {

        if (linkPart.getPartsWithOffset().size() > 1 && (linkPart.getInitialContent().startsWith("(") //$NON-NLS-1$
            || (linkPart.getPartsWithOffset().get(linkPart.getPartsWithOffset().size() - 1)).getFirst().isEmpty()))
        {
            return Optional.ofNullable(linkPart.getActualObjectForPart(linkPart.getPartsWithOffset().size() - 2,
                scopeProvider, context, typeComputationContext));
        }
        else
        {
            return Optional.ofNullable(linkPart.getActualObjectForPart(linkPart.getPartsWithOffset().size() - 1,
                scopeProvider, context, typeComputationContext));
        }
    }
}
