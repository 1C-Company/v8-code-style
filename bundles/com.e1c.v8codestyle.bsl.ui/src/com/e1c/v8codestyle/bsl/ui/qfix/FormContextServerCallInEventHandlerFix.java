/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.common.Symbols;
import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 * Replaces the compilation directive of the called method with НаСервереБезКонтекста or AtServerNoContext.
 *
 * @author Ivan Sergeev
 */
@QuickFix(checkId = "form-context-server-call-in-event-handler", supplierId = "com.e1c.v8codestyle.bsl")
public class FormContextServerCallInEventHandlerFix
    extends SingleVariantXtextBslModuleFix
{
    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.FormContextServerCallInEventHandlerFix_Description)
            .details(Messages.FormContextServerCallInEventHandlerFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject target = model.getElement();
        if (!(target instanceof Invocation))
        {
            return null;
        }

        Method callMethod = getLocalMethod(((Invocation)target).getMethodAccess());
        if (callMethod == null)
        {
            return null;
        }

        for (Pragma pragma : callMethod.getPragmas())
        {
            String symbol = pragma.getSymbol();
            if (!isServerDirective(symbol))
            {
                continue;
            }

            ICompositeNode node = NodeModelUtils.findActualNodeFor(pragma);
            if (node == null)
            {
                return null;
            }

            String newSymbol =
                isRussian(symbol) ? Symbols.AT_SERVER_NO_CONTEXT_RUS : Symbols.AT_SERVER_NO_CONTEXT_INTNL;

            return new ReplaceEdit(node.getOffset(), node.getLength(), "&" + newSymbol); //$NON-NLS-1$
        }
        return null;
    }

    private boolean isServerDirective(String symbol)
    {
        return Symbols.AT_SERVER_RUS.equalsIgnoreCase(symbol) || Symbols.AT_SERVER_INTNL.equalsIgnoreCase(symbol);
    }

    private boolean isRussian(String symbol)
    {
        return Symbols.AT_SERVER_RUS.equalsIgnoreCase(symbol);
    }

    private Method getLocalMethod(FeatureAccess featureAccess)
    {
        if (featureAccess instanceof StaticFeatureAccess sfa)
        {
            for (FeatureEntry entry : sfa.getFeatureEntries())
            {
                if (entry.getFeature() instanceof Method method)
                {
                    return method;
                }
            }
        }
        return null;
    }
}