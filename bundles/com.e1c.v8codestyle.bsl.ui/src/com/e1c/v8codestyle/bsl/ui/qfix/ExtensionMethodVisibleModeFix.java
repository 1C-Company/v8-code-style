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

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.common.IModuleExtensionService;
import com._1c.g5.v8.dt.bsl.common.IModuleExtensionServiceProvider;
import com._1c.g5.v8.dt.bsl.model.IfPreprocessorDeclareStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.google.inject.Inject;

/**
 * Correct visible mode extension method
 *
 *  @author Ivan Sergeev
 */
@QuickFix(checkId = "extension-method-visible-mode", supplierId = "com.e1c.v8codestyle.bsl")
public class ExtensionMethodVisibleModeFix
    extends SingleVariantXtextBslModuleFix
{

    private final IV8ProjectManager v8ProjectManager;

    private String ifSting = "#If"; //$NON-NLS-1$
    private String ifStingRu = "#Если"; //$NON-NLS-1$
    private String thenString = "Then"; //$NON-NLS-1$
    private String thenStringRu = "Тогда"; //$NON-NLS-1$

    @Inject
    public ExtensionMethodVisibleModeFix(IV8ProjectManager v8ProjectManager)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.ExtensionMethodVisibleModeFix_Description)
            .details(Messages.ExtensionMethodVisibleModeFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        Method method = (Method)model.getElement();
        IModuleExtensionService service = IModuleExtensionServiceProvider.INSTANCE.getModuleExtensionService();
        Map<Pragma, Method> pragmaSourceMethod = service.getSourceMethod(method);
        Collection<Method> methods = pragmaSourceMethod.values();
        Method sourceMethod = methods.iterator().next();
        EObject sourceParent = getIfPreprocessor(sourceMethod);
        ICompositeNode node = NodeModelUtils.findActualNodeFor(sourceParent);
        if (node == null)
        {
            return null;
        }
        String sourceText = node.getText();
        IV8Project baseProject = v8ProjectManager.getProject(sourceMethod);
        ScriptVariant languageCode = baseProject.getScriptVariant();
        String visibleModeText = getVisibleModeText(sourceText, languageCode);
        if (visibleModeText == null)
        {
            return null;
        }
        ICompositeNode nodeExtMethod = NodeModelUtils.findActualNodeFor(method);
        if (nodeExtMethod == null)
        {
            return null;
        }
        String methodText = nodeExtMethod.getText();
        String replaceMethodText = newMethodText(methodText, visibleModeText, languageCode);
        ICompositeNode nodeModel = state.getParseResult().getRootNode();
        if (nodeModel == null)
        {
            return null;
        }
        if (replaceMethodText == null)
        {
            return null;
        }
        int indexStartMethod = nodeModel.getText().indexOf(methodText);
        return new ReplaceEdit(indexStartMethod + System.lineSeparator().length(), methodText.length(),
            replaceMethodText);
    }

    private String getVisibleModeText(String sourceText, ScriptVariant languageCode)
    {

        int indexVisibleText = startVisibleTextIndex(sourceText, languageCode);
        int indexEndVisibleText = endVisibleTextIndex(sourceText, languageCode);
        if (indexVisibleText == -1 || indexEndVisibleText == -1)
        {
            return null;
        }
        return sourceText.substring(indexVisibleText, indexEndVisibleText);
    }

    private String newMethodText(String methodText, String visibleModeText, ScriptVariant languageCode)
    {
        StringBuilder sb = new StringBuilder(methodText);
        if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
        {
            String insertString = ifStingRu + visibleModeText + thenStringRu;
            sb.insert(0, insertString);
            sb.insert(methodText.length() + insertString.length(),
                System.lineSeparator() + "#КонецЕсли" + System.lineSeparator()); //$NON-NLS-1$
        }
        else
        {
            String insertString = ifSting + visibleModeText + thenString;
            sb.insert(0, insertString);
            sb.insert(methodText.length() + insertString.length(),
                System.lineSeparator() + "#EndIf" + System.lineSeparator()); //$NON-NLS-1$
        }
        return sb.toString();

    }

    private int startVisibleTextIndex(String sourceText, ScriptVariant languageCode)
    {
        if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
        {
            return sourceText.toLowerCase().indexOf(ifStingRu.toLowerCase()) + 5;
        }
        else
        {
            return sourceText.toLowerCase().indexOf(ifSting.toLowerCase()) + 3;
        }
    }

    private int endVisibleTextIndex(String sourceText, ScriptVariant languageCode)
    {
        if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
        {
            return sourceText.toLowerCase().indexOf(thenStringRu.toLowerCase());
        }
        else
        {
            return sourceText.toLowerCase().indexOf(thenString.toLowerCase());
        }
    }

    private EObject getIfPreprocessor(EObject eObject)
    {
        while (!(eObject instanceof IfPreprocessorDeclareStatement))
        {
            eObject = eObject.eContainer();
        }
        return eObject;
    }
}
