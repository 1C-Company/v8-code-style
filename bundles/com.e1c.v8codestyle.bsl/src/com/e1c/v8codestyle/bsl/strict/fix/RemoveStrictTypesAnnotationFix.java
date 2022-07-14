/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.strict.fix;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * The fix for all strict types checks that allows to remove annotation.
 *
 * @author Dmitriy Marmyshev
 */
public class RemoveStrictTypesAnnotationFix
    extends SingleVariantXtextBslModuleFix
{

    public static Collection<String> getCheckIds()
    {
        return Set.of("property-return-type", "doc-comment-field-type-strict", "dynamic-access-method-not-found", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "constructor-function-return-section", "function-return-value-type", "invocation-parameter-type-intersect", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "method-param-value-type", "statement-type-change", "structure-consructor-value-type", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "variable-value-type"); //$NON-NLS-1$
    }

    private final CheckUid checkUid;

    /**
     * Instantiates a new removes the strict types annotation fix.
     *
     * @param checkId the check id, cannot be {@code null}.
     */
    public RemoveStrictTypesAnnotationFix(CheckUid checkId)
    {
        this.checkUid = checkId;
    }

    @Override
    public CheckUid getCheckId()
    {
        return checkUid;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.description(Messages.RemoveStrictTypesAnnotationFix_Description)
            .details(Messages.RemoveStrictTypesAnnotationFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        if (model.getElement() != null)
        {
            ICompositeNode node = NodeModelUtils.getNode(model.getElement());
            if (node == null)
            {
                IStatus status =
                    BslPlugin.createErrorStatus("RemoveStrictTypesAnnotationFix: cannot get node for module object: " //$NON-NLS-1$
                        + EcoreUtil.getURI(model.getElement()), null);
                BslPlugin.log(status);
                return null;
            }
            node = node.getRootNode();
            ILeafNode annotatioNode = StrictTypeUtil.getStrictTypeAnnotationNode(node);
            if (annotatioNode != null)
            {
                return new DeleteEdit(annotatioNode.getTotalOffset(), annotatioNode.getTotalLength());
            }
        }

        return null;
    }

}
