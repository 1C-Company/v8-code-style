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
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.google.inject.Inject;

/**
 * Replacement no SSL call to SSL call
 *
 *  @author Ivan Sergeev
 */
@QuickFix(checkId = "restriction-execute-external-code", supplierId = "com.e1c.v8codestyle.bsl")
public class RestrictionExecuteExternalCodeFix
    extends SingleVariantXtextBslModuleFix
{

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public RestrictionExecuteExternalCodeFix(IV8ProjectManager v8ProjectManager)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.RestrictionExecuteExternalCodeFix_Description)
            .details(Messages.RestrictionExecuteExternalCodeFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject eobject = model.getElement();
        if (eobject instanceof OperatorStyleCreator operatorStyleCreator)
        {
            int issueOffset = model.getIssue().getOffset();
            INode node = NodeModelUtils.findActualNodeFor(eobject);
            if (node == null)
            {
                return null;
            }
            IV8Project baseProject = v8ProjectManager.getProject(eobject);
            ScriptVariant languageCode = baseProject.getScriptVariant();
            String parametrs = parametrs(operatorStyleCreator, languageCode);
            String newText = newText(languageCode, parametrs);

            return new ReplaceEdit(issueOffset, node.getText().length() - 1, newText);
        }
        return null;
    }

    private String newText(ScriptVariant languageCode, String parametrs)
    {
        String newText = ""; //$NON-NLS-1$
        if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
        {
            if (parametrs.isEmpty())
            {
                newText = "ОбщегоНазначенияКлиентСервер.НовоеЗащищенноеСоединение"; //$NON-NLS-1$
            }
            else
            {
                newText = "ОбщегоНазначенияКлиентСервер.НовоеЗащищенноеСоединение" + parametrs; //$NON-NLS-1$
            }
        }
        else if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.ENGLISH)
        {
            if (parametrs.isEmpty())
            {
                newText = "CommonClientServer.NewSecureConnection"; //$NON-NLS-1$
            }
            else
            {
                newText = "CommonClientServer.NewSecureConnection" + parametrs; //$NON-NLS-1$
            }
        }
        return newText;
    }

    private String parametrs(OperatorStyleCreator operatorStyleCreator, ScriptVariant languageCode)
    {
        if (operatorStyleCreator.getParams().isEmpty())
        {
            return ""; //$NON-NLS-1$
        }
        String text = NodeModelUtils.findActualNodeFor(operatorStyleCreator).getText();
        String nameOperator = ""; //$NON-NLS-1$
        if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
        {
            nameOperator = McoreUtil.getTypeNameRu(operatorStyleCreator.getType());
        }
        else if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.ENGLISH)
        {
            nameOperator = McoreUtil.getTypeName(operatorStyleCreator.getType());
        }
        int index = text.toLowerCase().indexOf(nameOperator.toLowerCase());
        int indexStartParametrs = 0;
        char[] operator = text.substring(index + nameOperator.length(), text.length()).toCharArray();
        if (operator[0] != '(')
        {
            while (operator[indexStartParametrs] != '(')
            {
                indexStartParametrs = indexStartParametrs + 1;
            }
        }
        return text.substring(index + nameOperator.length() + indexStartParametrs, text.length());
    }
}
