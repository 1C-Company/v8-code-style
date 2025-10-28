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

package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.GOTO_STATEMENT;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.GotoStatement;
import com._1c.g5.v8.dt.bsl.model.IfPreprocessor;
import com._1c.g5.v8.dt.bsl.model.IfPreprocessorDeclareStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * The check Goto operator in client code
 * @author Ivan Sergeev
 */

public class NotSupportGotoOperatorWebCheck
    extends AbstractModuleStructureCheck
{

    private static final String CHECK_ID = "not-support-goto-operator"; //$NON-NLS-1$

    public NotSupportGotoOperatorWebCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.NotSupportGotoOperatorWebCheck_Title)
            .description(Messages.NotSupportGotoOperatorWebCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.CRITICAL)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(GOTO_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof GotoStatement)
        {
            EObject eObject = (EObject)object;
            Module module = EcoreUtil2.getContainerOfType(eObject, Module.class);
            Method method = EcoreUtil2.getContainerOfType(eObject, Method.class);
            String pragmas = method.getPragmas().get(0).getSymbol();

            if (module.getModuleType() == ModuleType.COMMON_MODULE)
            {
                if (module.environments().containsAll(Environments.ORDINARY_CLIENTS))
                {
                    resultAcceptor.addIssue(Messages.NotSupportGotoOperatorWebCheck_Issue, object);
                }
            }
            else if (pragmas.toLowerCase().contains("AtClient".toLowerCase()) //$NON-NLS-1$
                || pragmas.toLowerCase().contains("НаКлиенте".toLowerCase())) //$NON-NLS-1$
            {
                List<IfPreprocessor> allItems = BslUtil.getAllIfPreprocessorsFromBlock(method);
                if (allItems.isEmpty())
                {
                    resultAcceptor.addIssue(Messages.NotSupportGotoOperatorWebCheck_Issue, object);
                    return;
                }
                for (IfPreprocessor ifPreprocessor : allItems)
                {
                    if (ifPreprocessor instanceof IfPreprocessorDeclareStatement)
                    {
                        ICompositeNode node = NodeModelUtils.findActualNodeFor(ifPreprocessor);
                        if (node == null)
                        {
                            return;
                        }
                        if (node.getText().toLowerCase().contains("Перейти".toLowerCase())) //$NON-NLS-1$
                        {
                            checkPreprocessorIf(ifPreprocessor, resultAcceptor, object);
                        }
                        else
                        {
                            resultAcceptor.addIssue(Messages.NotSupportGotoOperatorWebCheck_Issue, object);
                        }
                    }
                    else
                    {
                        resultAcceptor.addIssue(Messages.NotSupportGotoOperatorWebCheck_Issue, object);
                    }
                }

            }
        }
    }

    protected void checkPreprocessorIf(IfPreprocessor ifPreprocessor, ResultAcceptor resultAcceptor, Object object)
    {
        EList<EObject> listStatement = ifPreprocessor.eContents();
        for (EObject eObject : listStatement)
        {
            ICompositeNode node = NodeModelUtils.findActualNodeFor(eObject);
            if (node == null)
            {
                return;
            }
            if (node.getText().toLowerCase().contains("Перейти".toLowerCase()) //$NON-NLS-1$
                | node.getText().toLowerCase().contains("GoTo".toLowerCase())) //$NON-NLS-1$
            {
                if (!node.getText().toLowerCase().contains("НЕ ВебКлиент".toLowerCase()) //$NON-NLS-1$
                    & !node.getText().toLowerCase().contains("NOT WebClient".toLowerCase())) //$NON-NLS-1$
                {
                    resultAcceptor.addIssue(Messages.NotSupportGotoOperatorWebCheck_Issue, object);
                }
            }
        }
    }
}
