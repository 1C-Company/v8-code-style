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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.ui.contentassist.BslProposalProvider;
import com._1c.g5.v8.dt.bsl.util.BslUtil;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 *  Fix for {@link OverridableModuleOnlyExportMethodsCheck}
 *
 *  @author Artem Samohvalov
 */
@QuickFix(checkId = "overridable-module-only-export-methods", supplierId = "com.e1c.v8codestyle.bsl")
public class OverridableModuleOnlyExportMethodsFix
    extends SingleVariantXtextBslModuleFix
{

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.OverridableModuleOnlyExportMethodsFix_Description)
            .details(Messages.OverridableModuleOnlyExportMethodsFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource resource, IXtextBslModuleFixModel model) throws BadLocationException
    {
        if (model.getElement() instanceof Method method)
        {
            INode node = BslUtil.getMethodSignatureLastNode(method);

            if (node != null)
            {
                return new InsertEdit(node.getOffset() + node.getLength(), getExportKeyword(model));
            }
        }

        return null;
    }

    private String getExportKeyword(IXtextBslModuleFixModel model)
    {
        return " " + BslProposalProvider.getExportLiteralName(model.getBslGrammar(), //$NON-NLS-1$
            model.getScriptVariant() == ScriptVariant.RUSSIAN);
    }
}
