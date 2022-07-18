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
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 * Quick fix for check com.e1c.v8codestyle.bsl.check:form-self-reference-outdated
 *
 * @author Maxim Galios
 *
 */
@QuickFix(checkId = "form-self-reference", supplierId = "com.e1c.v8codestyle.bsl")
public class FormSelfReferenceOutdatedFix
    extends SingleVariantXtextBslModuleFix
{
    private static final String OUTDATED_SELF_REFERENCE = "ThisForm"; //$NON-NLS-1$

    private static final String SELF_REFERENCE = "ThisObject"; //$NON-NLS-1$
    private static final String SELF_REFERENCE_RU = "ЭтотОбъект"; //$NON-NLS-1$

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.FormSelfReferenceOutdatedFix_description)
            .details(Messages.FormSelfReferenceOutdatedFix_details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject element = model.getElement();
        if (!(element instanceof StaticFeatureAccess))
        {
            return null;
        }
        StaticFeatureAccess staticFeatureAccess = (StaticFeatureAccess) element;
        String name = staticFeatureAccess.getName();

        String replacement = name.equals(OUTDATED_SELF_REFERENCE) ? SELF_REFERENCE : SELF_REFERENCE_RU;
        INode node = NodeModelUtils.findActualNodeFor(element);
        return new ReplaceEdit(node.getOffset(), node.getLength(), replacement);
    }

}
