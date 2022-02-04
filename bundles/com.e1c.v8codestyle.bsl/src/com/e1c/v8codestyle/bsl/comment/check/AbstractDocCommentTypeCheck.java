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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.IBslCommentToken;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.Method;
import com._1c.g5.v8.dt.mcore.Parameter;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com.e1c.g5.v8.dt.bsl.check.DocumentationCommentBasicDelegateCheck;

/**
 * Abstract check documentation comment model when needs to process types.
 *
 * @author Dmitriy Marmyshev
 */
public abstract class AbstractDocCommentTypeCheck
    extends DocumentationCommentBasicDelegateCheck
{

    /**
     * Gets the link part from description only if the link is single element of the description.
     * This method skips empty text parts.
     *
     * @param description the description, cannot be {@link null}.
     * @return the single link part or returns {@code null} if no link or there are other parts in description.
     */
    protected LinkPart getSingleLinkPart(Description description)
    {
        List<IDescriptionPart> parts = description.getParts();

        List<LinkPart> linkParts = new ArrayList<>(parts.size());
        int lastLine = -1;
        for (IDescriptionPart part : parts)
        {
            if (part instanceof LinkPart && lastLine != part.getLineNumber())
            {
                linkParts.add((LinkPart)part);
                lastLine = part.getLineNumber();
            }
            else if (!isCanSkipEmptyTextPart(part, lastLine))
            {
                return null;
            }
        }

        return linkParts.size() == 1 ? linkParts.get(0) : null;
    }

    /**
     * Checks if the part is {@link TextPart} and can be skipped because it contains only ending symbols - dot or dash.
     *
     * @param part the part, cannot be {@link null}.
     * @param lastLine the last line, cannot be {@link null}.
     * @return true, if is can skip empty text part
     */
    protected boolean isCanSkipEmptyTextPart(IDescriptionPart part, int lastLine)
    {
        if (part instanceof TextPart)
        {
            String text = ((TextPart)part).getText();
            if (text != null && (".".equals(text.trim()) //$NON-NLS-1$
                || IBslCommentToken.TYPE_SECTION_DASH.equals(text.trim()) && lastLine != part.getLineNumber()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the field types are empty.
     *
     * @param fieldDef the field definition, cannot be {@link null}.
     * @return true, if the field type is empty
     */
    protected boolean isFieldTypeEmpty(FieldDefinition fieldDef)
    {
        List<TypeSection> typeSections = fieldDef.getTypeSections();
        Description description = fieldDef.getDescription();

        return isTypeEmptyAndNoLink(typeSections, description);
    }

    /**
     * Checks if the type is empty and no link in description.
     *
     * @param typeSections the type sections, cannot be {@link null}.
     * @param description the description, cannot be {@link null}.
     * @return true, if the type is empty and no link in description.
     */
    protected boolean isTypeEmptyAndNoLink(List<TypeSection> typeSections, Description description)
    {
        if (typeSections.isEmpty())
        {
            return getSingleLinkPart(description) == null;
        }

        for (TypeSection typeSection : typeSections)
        {
            for (TypeDefinition typeDef : typeSection.getTypeDefinitions())
            {
                if (StringUtils.isNotEmpty(typeDef.getTypeName()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if the link part referenced to the existing object.
     *
     * @param linkPart the link part, cannot be {@link null}.
     * @param scopeProvider the scope provider, cannot be {@link null}.
     * @param context the context, cannot be {@link null}.
     * @return true, if the object of link part is exist
     */
    protected boolean isLinkPartObjectExist(LinkPart linkPart, IScopeProvider scopeProvider, EObject context)
    {
        EObject object = null;

        // get object of last segment of the link to method/parameter, without final brackets "(See ModuleName.MethodName.)".
        if (linkPart.getInitialContent().startsWith("(") //$NON-NLS-1$
            && linkPart.getPartsWithOffset().size() > 1
            && (linkPart.getPartsWithOffset().get(linkPart.getPartsWithOffset().size() - 1)).getFirst().isEmpty())
        {
            object = linkPart.getActualObjectForPart(linkPart.getPartsWithOffset().size() - 2, scopeProvider, context);
        }
        else
        {
            object = linkPart.getActualObjectForPart(linkPart.getPartsWithOffset().size() - 1, scopeProvider, context);
        }

        return object instanceof Method || object instanceof Function || object instanceof Parameter
            || object instanceof FormalParam || object instanceof TypeItem;
    }
}
