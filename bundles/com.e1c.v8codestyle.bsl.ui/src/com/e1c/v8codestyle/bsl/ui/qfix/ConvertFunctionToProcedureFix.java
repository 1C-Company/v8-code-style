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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.Function;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.SingleVariantXtextBslModuleFix;
import com.google.common.collect.Lists;


/**
 * Quick fix for check com.e1c.v8codestyle.bsl.strict.check:function-return-value-type
 *
 * @author Timur Mukhamedishin
 *
 */
@QuickFix(checkId = "function-return-value-type", supplierId = "com.e1c.v8codestyle.bsl")
public class ConvertFunctionToProcedureFix
    extends SingleVariantXtextBslModuleFix
{
    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.ConvertFunctionToProcedureFix_description)
            .details(Messages.ConvertFunctionToProcedureFix_details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model)
    {
        EObject element = model.getElement();

        if (!(element instanceof Function))
        {
            return null;
        }

        INode node = NodeModelUtils.findActualNodeFor(element);
        List<ILeafNode> allLeafNodes = Lists.newArrayList(node.getLeafNodes());

        MultiTextEdit result = new MultiTextEdit();

        for (int i = 0; i < allLeafNodes.size(); ++i)
        {
            ILeafNode leafNode = allLeafNodes.get(i);

            if (leafNode.getText().equals(QuickFixMethodsHelper.getTypeMethodName(model, true)))
            {
                result.addChild(new ReplaceEdit(leafNode.getTotalOffset(), leafNode.getTotalLength(),
                    QuickFixMethodsHelper.getTypeMethodName(model, false)));

                break;
            }
        }

        for (int i = allLeafNodes.size() - 1; i >= 0; --i)
        {
            ILeafNode leafNode = allLeafNodes.get(i);

            if (leafNode.getText().equals(QuickFixMethodsHelper.getTypeEndMethodName(model, true)))
            {
                result.addChild(new ReplaceEdit(leafNode.getTotalOffset(), leafNode.getTotalLength(),
                    QuickFixMethodsHelper.getTypeEndMethodName(model, false)));

                return result;
            }
        }

        return null;
    }
}
