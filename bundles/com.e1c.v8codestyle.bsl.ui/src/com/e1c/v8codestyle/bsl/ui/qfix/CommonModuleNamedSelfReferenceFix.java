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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 * Quick fix for check com.e1c.v8codestyle.bsl.check:common-module-named-self-reference
 *
 * @author Maxim Galios
 *
 */
@QuickFix(checkId = "common-module-named-self-reference", supplierId = "com.e1c.v8codestyle.bsl")
public class CommonModuleNamedSelfReferenceFix
    extends SingleVariantXtextBslModuleFix
{
    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.CommonModuleNamedSelfReferenceFix_description)
            .details(Messages.CommonModuleNamedSelfReferenceFix_details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject element = model.getElement();
        if (!(element instanceof StaticFeatureAccess))
        {
            return null;
        }
        INode node = NodeModelUtils.findActualNodeFor(element);
        return new DeleteEdit(node.getOffset(), node.getLength() + 1);
    }
}
