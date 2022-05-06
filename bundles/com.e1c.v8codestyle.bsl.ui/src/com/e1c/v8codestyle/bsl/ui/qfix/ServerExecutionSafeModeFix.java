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
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.ExecuteStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextInteractiveBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.SingleVariantXtextBslModuleFix;

/**
 * Quick fix for check com.e1c.v8codestyle.bsl.check:server-execution-safe-mode
 *
 * @author Maxim Galios
 *
 */
@QuickFix(checkId = "server-execution-safe-mode", supplierId = "com.e1c.v8codestyle.bsl")
public class ServerExecutionSafeModeFix
    extends SingleVariantXtextBslModuleFix
{
    private static final String SAFE_MODE_INVOCATION = "SetSafeMode(True);"; //$NON-NLS-1$
    private static final String SAFE_MODE_INVOCATION_RU = "УстановитьБезопасныйРежим(Истина);"; //$NON-NLS-1$

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.ServerExecutionSafeModeFix_description)
            .details(Messages.ServerExecutionSafeModeFix_details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject element = model.getElement();
        if (!(element instanceof StaticFeatureAccess) && !(element instanceof ExecuteStatement))
        {
            return null;
        }

        String safeModeStatement =
            model.getScriptVariant() == ScriptVariant.RUSSIAN ? SAFE_MODE_INVOCATION_RU : SAFE_MODE_INVOCATION;

        INode node = NodeModelUtils.findActualNodeFor(
            element instanceof StaticFeatureAccess ? EcoreUtil2.getContainerOfType(element, Statement.class) : element);

        IXtextInteractiveBslModuleFixModel interactiveModel = (IXtextInteractiveBslModuleFixModel)model;
        String indent = interactiveModel.getFormatString(element).orElse(StringUtils.EMPTY);

        String codeToInsert = safeModeStatement + model.getLineSeparator() + indent;
        return new InsertEdit(node.getOffset(), codeToInsert);
    }
}
