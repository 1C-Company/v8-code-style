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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.GotoStatement;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * The check Goto operator in client code
 * @author Ivan Sergeev
 */
public class NotSupportGotoOperatorWebCheck
    extends AbstractModuleStructureCheck
{

    private static final String CHECK_ID = "not-support-goto-operator-webclient"; //$NON-NLS-1$

    @Inject
    private IBslPreferences bslPreferences;

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
            Environmental envir = EcoreUtil2.getContainerOfType(eObject, Environmental.class);
            if (bslPreferences.getLoadEnvs(eObject).contains(Environment.WEB_CLIENT))
            {
                if (envir == null)
                {
                    return;
                }
                if (envir.environments().contains(Environment.WEB_CLIENT))
                {
                    resultAcceptor.addIssue(Messages.NotSupportGotoOperatorWebCheck_Issue, object);
                }
            }
        }
    }
}
