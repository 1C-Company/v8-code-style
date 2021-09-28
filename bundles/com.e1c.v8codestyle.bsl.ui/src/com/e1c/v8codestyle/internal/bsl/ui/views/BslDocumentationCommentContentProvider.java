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
package com.e1c.v8codestyle.internal.bsl.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Section;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.LinkContainsTypeDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;

/**
 * The content provider for {@link TreeViewer} bases on root {@link BslDocumentationComment doc comment model}.
 *
 * @author Dmitriy Marmyshev
 */
public class BslDocumentationCommentContentProvider
    implements ITreeContentProvider
{

    private BslDocumentationComment internalRoot;

    @Override
    public Object[] getElements(Object parent)
    {
        if (parent instanceof BslDocumentationComment)
        {
            internalRoot = (BslDocumentationComment)parent;
        }
        return getChildren(parent);
    }

    @Override
    public Object getParent(Object child)
    {
        if (child instanceof BslDocumentationComment)
        {
            return null;
        }
        else if (child instanceof ParametersSection)
        {
            return internalRoot;
        }
        else if (child instanceof ReturnSection)
        {
            return internalRoot;
        }
        else if (child instanceof Section)
        {
            return internalRoot;
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parent)
    {
        List<Object> children = new ArrayList<>();
        if (parent instanceof BslDocumentationComment)
        {
            BslDocumentationComment root = (BslDocumentationComment)parent;
            internalRoot = root;

            if (root.getDescription() != null && !root.getDescription().getParts().isEmpty())
            {
                children.add(root.getDescription());
            }
            if (root.getParametersSection() != null)
            {
                children.add(root.getParametersSection());
            }
            if (root.getCallOptionsSection() != null)
            {
                children.add(root.getCallOptionsSection());
            }
            if (root.getExampleSection() != null)
            {
                children.add(root.getExampleSection());
            }
            if (root.getReturnSection() != null)
            {
                children.add(root.getReturnSection());
            }
        }
        else if (parent instanceof Description)
        {
            children.addAll(((Description)parent).getParts());
        }
        else if (parent instanceof ParametersSection)
        {
            ParametersSection section = (ParametersSection)parent;
            if (section.getDescription() != null && !section.getDescription().getParts().isEmpty())
            {
                children.add(section.getDescription());
            }
            if (!section.getParameterDefinitions().isEmpty())
            {
                children.addAll(section.getParameterDefinitions());
            }
        }
        else if (parent instanceof ReturnSection)
        {
            ReturnSection section = (ReturnSection)parent;
            if (section.getDescription() != null && !section.getDescription().getParts().isEmpty())
            {
                children.add(section.getDescription());
            }
            if (!section.getReturnTypes().isEmpty())
            {
                children.addAll(section.getReturnTypes());
            }
        }
        else if (parent instanceof TypeSection)
        {
            TypeSection section = (TypeSection)parent;
            if (section.getDescription() != null && !section.getDescription().getParts().isEmpty())
            {
                children.add(section.getDescription());
            }
            if (!section.getTypeDefinitions().isEmpty())
            {
                children.addAll(section.getTypeDefinitions());
            }
        }
        else if (parent instanceof Section)
        {
            Section section = (Section)parent;
            if (section.getDescription() != null && !section.getDescription().getParts().isEmpty())
            {
                children.add(section.getDescription());
            }
        }
        else if (parent instanceof FieldDefinition)
        {
            FieldDefinition section = (FieldDefinition)parent;
            if (section.getDescription() != null && !section.getDescription().getParts().isEmpty())
            {
                children.add(section.getDescription());
            }
            if (!section.getTypeSections().isEmpty())
            {
                children.addAll(section.getTypeSections());
            }
        }
        else if (parent instanceof LinkContainsTypeDefinition)
        {
            LinkContainsTypeDefinition section = (LinkContainsTypeDefinition)parent;
            if (section.getLink() != null)
            {
                children.add(section.getLink());
            }
            if (!section.getContainTypes().isEmpty())
            {
                children.addAll(section.getContainTypes());
            }
            if (!section.getFieldDefinitionExtension().isEmpty())
            {
                children.addAll(section.getFieldDefinitionExtension());
            }
            if (section.getLinkToExtensionFields() != null)
            {
                children.add(section.getLinkToExtensionFields());
            }
        }
        else if (parent instanceof TypeDefinition)
        {
            TypeDefinition section = (TypeDefinition)parent;
            if (!section.getContainTypes().isEmpty())
            {
                children.addAll(section.getContainTypes());
            }
            if (!section.getFieldDefinitionExtension().isEmpty())
            {
                children.addAll(section.getFieldDefinitionExtension());
            }
            if (section.getLinkToExtensionFields() != null)
            {
                children.add(section.getLinkToExtensionFields());
            }
        }
        return children.toArray();
    }

    @Override
    public boolean hasChildren(Object parent)
    {
        if (parent instanceof BslDocumentationComment)
        {
            BslDocumentationComment root = (BslDocumentationComment)parent;

            return root.getDescription() != null && !root.getDescription().getParts().isEmpty()
                || root.getParametersSection() != null || root.getCallOptionsSection() != null
                || root.getExampleSection() != null || root.getReturnSection() != null;
        }
        else if (parent instanceof Description)
        {
            return !((Description)parent).getParts().isEmpty();
        }
        else if (parent instanceof ParametersSection)
        {
            ParametersSection section = (ParametersSection)parent;
            return section.getDescription() != null && !section.getDescription().getParts().isEmpty()
                || !section.getParameterDefinitions().isEmpty();
        }
        else if (parent instanceof ReturnSection)
        {
            ReturnSection section = (ReturnSection)parent;
            return section.getDescription() != null || !section.getReturnTypes().isEmpty();
        }
        else if (parent instanceof TypeSection)
        {
            TypeSection section = (TypeSection)parent;
            return section.getDescription() != null && !section.getDescription().getParts().isEmpty()
                || !section.getTypeDefinitions().isEmpty();
        }
        else if (parent instanceof Section)
        {
            Section section = (Section)parent;
            return section.getDescription() != null && !section.getDescription().getParts().isEmpty();
        }
        else if (parent instanceof FieldDefinition)
        {
            FieldDefinition section = (FieldDefinition)parent;
            return section.getDescription() != null && !section.getDescription().getParts().isEmpty()
                || !section.getTypeSections().isEmpty();
        }
        else if (parent instanceof LinkContainsTypeDefinition)
        {
            LinkContainsTypeDefinition section = (LinkContainsTypeDefinition)parent;
            return section.getLink() != null || !section.getContainTypes().isEmpty()
                || !section.getFieldDefinitionExtension().isEmpty() || section.getLinkToExtensionFields() != null;
        }
        else if (parent instanceof TypeDefinition)
        {
            TypeDefinition section = (TypeDefinition)parent;
            return !section.getContainTypes().isEmpty() || !section.getFieldDefinitionExtension().isEmpty()
                || section.getLinkToExtensionFields() != null;
        }
        return false;
    }
}
