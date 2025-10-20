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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
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
            .details(Messages.MethodSemicolonExtraFix_Description);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject eobject = model.getElement();
        if (!(eobject instanceof Method))
        {
            return null;
        }

        List<Statement> allItems = BslUtil.allStatements(eobject);

        INode node = NodeModelUtils.findActualNodeFor(allItems.get(0));

        int size = allItems.size();

        for (int i = 0; i < size; i++)
        {
            if (allItems.get(i) instanceof EmptyStatement)
            {
                node = NodeModelUtils.findActualNodeFor(allItems.get(i));

                if (node == null)
                {
                    return null;
                }
                INode checkNode = node.getNextSibling();
                String checkText = checkNode.getText();
                INode checkNextNode = checkNode.getNextSibling();
                if (checkText.contains(";")) //$NON-NLS-1$
                {
                    return new DeleteEdit(checkNode.getTotalOffset(), checkNode.getTotalLength());
                }
                else if (checkNextNode.getText().contains(";")) //$NON-NLS-1$
                {
                    return new DeleteEdit(checkNextNode.getTotalOffset(), checkNextNode.getTotalLength());
                }
            }
        }
        return null;
    }
}
