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
package com.e1c.v8codestyle.bsl.ui.qfix;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Remove self assigne variable
 *
 *  @author Ivan Sergeev
 */
@QuickFix(checkId = "self-assign", supplierId = BslPlugin.PLUGIN_ID)
public class SelfAssignFix
    extends SingleVariantXtextBslModuleFix
{

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.SelfAssignFix_Description)
            .details(Messages.SelfAssignFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject eobject = model.getElement();
        if (!(eobject instanceof SimpleStatement))
        {
            return null;
        }

        INode node = NodeModelUtils.findActualNodeFor(eobject);

        if (Objects.isNull(eobject))
        {
            return null;
        }

        INode nextSibling = node.getNextSibling();

        if (nextSibling.getText().contains(";")) //$NON-NLS-1$
        {
            return new DeleteEdit(node.getTotalOffset(), node.getTotalLength() + nextSibling.getTotalLength());
        }
        else
        {
            return new DeleteEdit(node.getTotalOffset(), node.getTotalLength());
        }
    }

}
