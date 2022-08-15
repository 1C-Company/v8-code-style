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
package com.e1c.v8codestyle.bsl.qfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 * This fix move event to correct region
 *
 * @author Artem Iliukhin
 */
@QuickFix(checkId = "module-structure-form-event-regions", supplierId = "com.e1c.v8codestyle.bsl")
public class ModuleStructureEventFormRegionsFix
    extends SingleVariantXtextBslModuleFix
{

    private static final int LENGTH_END_REGION_RU = "#КонецОбласти".length(); //$NON-NLS-1$
    private static final int LENGTH_END_REGION = "#EndRegion".length(); //$NON-NLS-1$

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.description("Move").details("Move to the correct area");
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject element = model.getElement();

        if (element != null)
        {
            Module module = EcoreUtil2.getContainerOfType(element, Module.class);
            if (module == null)
            {
                return null;
            }

            ScriptVariant scriptVariant = model.getScriptVariant();
            ICompositeNode nodeElement = NodeModelUtils.getNode(element);
            if (nodeElement == null)
            {
                return null;
            }

            for (RegionPreprocessor region : BslUtil.getAllRegionPreprocessors(module))
            {
                if (isFormHandlerRegion(model, region, scriptVariant))
                {
                    return getTextEdit(nodeElement, region, scriptVariant);
                }
            }
        }
        return null;
    }

    private TextEdit getTextEdit(ICompositeNode nodeElement, RegionPreprocessor region,
        ScriptVariant scriptVariant)
    {
        ICompositeNode nodeRegion = NodeModelUtils.getNode(region);

        INode nodeItem = NodeModelUtils.findActualNodeFor(region.getItemAfter());
        int until;
        if (nodeItem != null)
        {
            until = nodeItem.getTotalOffset();
        }
        else
        {
            until = nodeRegion.getTotalEndOffset();
        }

        if (nodeRegion != null)
        {
            return getEdit(nodeElement, until, scriptVariant);
        }
        return null;
    }

    private TextEdit getEdit(ICompositeNode node, int until, ScriptVariant variant)
    {
        TextEdit edit = new MultiTextEdit();
        edit.addChild(new DeleteEdit(node.getOffset(), node.getLength()));
        edit.addChild(new InsertEdit(until - lengthEndRegion(variant) - 1, node.getText()));

        return edit;
    }

    private boolean isFormHandlerRegion(IXtextBslModuleFixModel model, RegionPreprocessor region, ScriptVariant variant)
    {
        return model.getIssue().getMessage().contains(region.getName());
    }

    private int lengthEndRegion(ScriptVariant scriptVariant)
    {
        return scriptVariant.equals(ScriptVariant.ENGLISH) ? LENGTH_END_REGION : LENGTH_END_REGION_RU;
    }
}
