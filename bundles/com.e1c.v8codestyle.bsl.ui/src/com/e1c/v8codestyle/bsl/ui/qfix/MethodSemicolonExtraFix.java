/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.ui.qfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 * Removes extra semicolon
 *
 *  @author Ivan Sergeev
 */
@QuickFix(checkId = "method-semicolon-extra", supplierId = "com.e1c.v8codestyle.bsl")
public class MethodSemicolonExtraFix
    extends SingleVariantXtextBslModuleFix
{

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.MethodSemicolonExtraFix_Description)
            .details(Messages.MethodSemicolonExtraFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject eobject = model.getElement();

        if (!(eobject instanceof EmptyStatement))
        {
            return null;
        }

        INode node = NodeModelUtils.findActualNodeFor(eobject);

        if (node == null)
        {
            return null;
        }
        INode checkNode = node.getNextSibling();
        if (checkNode == null)
        {
            return null;
        }
        String checkText = checkNode.getText();
        INode checkNextNode = checkNode.getNextSibling();
        if (checkText.contains(";")) //$NON-NLS-1$
        {
            return new DeleteEdit(checkNode.getTotalOffset(), checkNode.getTotalLength());
        }
        else if (checkNextNode != null && checkNextNode.getText().contains(";")) //$NON-NLS-1$
        {
            return new DeleteEdit(checkNextNode.getTotalOffset(), checkNextNode.getTotalLength());
        }
        return null;
    }
}