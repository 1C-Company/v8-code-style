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
package com.e1c.v8codestyle.ql.check.itests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck.IQlResultAcceptor;

/**
 * Testing result acceptor implementation for QL check tests.
 *
 * @author Dmitriy Marmyshev
 */
class TestingQlResultAcceptor
    implements IQlResultAcceptor
{

    private final List<QueryMarker> markers = new ArrayList<>();

    @Override
    public void addIssue(String message)
    {
        markers.add(new QueryMarker(message));
    }

    @Override
    public void addIssue(String message, EObject target, EStructuralFeature feature)
    {
        markers.add(new QueryMarker(message, target, feature));
    }

    @Override
    public void addIssue(String message, EObject target, EStructuralFeature manyFeature, int index)
    {
        markers.add(new QueryMarker(message, target, manyFeature, index));
    }

    @Override
    public void addIssue(String message, int length)
    {
        markers.add(new QueryMarker(message));
    }

    @Override
    public void addIssue(String message, int lineNumber, int offset, int length)
    {
        markers.add(new QueryMarker(message, lineNumber, offset, length));
    }

    public List<QueryMarker> getMarkers()
    {
        return markers;
    }

    public final class QueryMarker
    {
        private final String message;

        private final int lineNumber;

        private final int offset;

        private final int length;

        private final EObject target;

        private final EStructuralFeature feature;

        private final int index;

        private QueryMarker(String message)
        {
            this.message = message;
            this.lineNumber = -1;
            this.offset = -1;
            this.length = -1;
            this.target = null;
            this.feature = null;
            this.index = -1;

        }

        private QueryMarker(String message, int lineNumber, int offset, int length)
        {
            this.message = message;
            this.lineNumber = lineNumber;
            this.offset = offset;
            this.length = length;
            this.target = null;
            this.feature = null;
            this.index = -1;

        }

        private QueryMarker(String message, EObject target)
        {
            this(message, target, null, -1);
        }

        private QueryMarker(String message, EObject target, EStructuralFeature feature)
        {
            this(message, target, feature, -1);
        }

        private QueryMarker(String message, EObject target, EStructuralFeature manyFeature, int index)
        {
            this.message = message;
            this.target = target;
            this.feature = manyFeature;
            this.index = index;

            INode node = null;
            if (feature == null)
            {
                node = NodeModelUtils.findActualNodeFor(target);
            }
            else
            {
                List<INode> nodes = NodeModelUtils.findNodesForFeature(target, manyFeature);

                if (!nodes.isEmpty() && index > -1 && index < nodes.size())
                {
                    node = nodes.get(index);
                }
                else if (!nodes.isEmpty())
                {
                    node = nodes.get(0);
                }
            }

            if (node == null)
            {
                this.lineNumber = -1;
                this.offset = -1;
                this.length = -1;
            }
            else
            {
                this.lineNumber = NodeModelUtils.getLineAndColumn(node.getRootNode(), node.getOffset()).getLine();
                this.offset = node.getOffset();
                this.length = node.getLength();
            }

        }

        public String getMessage()
        {
            return message;
        }

        public EObject getTarget()
        {
            return target;
        }

        public EStructuralFeature getFeature()
        {
            return feature;
        }

        public int getFeatureIndex()
        {
            return index;
        }

        public int getLineNumber()
        {
            return lineNumber;
        }

        public int getOffset()
        {
            return offset;
        }

        public int getLength()
        {
            return length;
        }

    }
}
