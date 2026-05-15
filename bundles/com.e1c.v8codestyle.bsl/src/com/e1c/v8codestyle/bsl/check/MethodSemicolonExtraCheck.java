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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.util.BslUtil;
import com.e1c.g5.v8.dt.check.BslDirectLocationIssue;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.DirectLocation;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.Issue;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks method declaration contain extra semicolon
 *
 *  @author Ivan Sergeev
 */
public class MethodSemicolonExtraCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "method-semicolon-extra"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MethodSemicolonExtraCheck_Title)
            .description(Messages.MethodSemicolonExtraCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;
        List<Statement> allItems = BslUtil.allStatements(method);
        if (allItems.isEmpty())
        {
            return;
        }

        if (allItems.get(0) instanceof EmptyStatement)
        {
            INode node = NodeModelUtils.findActualNodeFor(allItems.get(0));

            if (node == null)
            {
                return;
            }
            INode checkNode = node.getNextSibling();

            if (checkNode == null)
            {
                return;
            }
            String checkText = checkNode.getText();
            INode checkNodeNext = checkNode.getNextSibling();
            if (checkText.contains(";")) //$NON-NLS-1$
            {

                DirectLocation directLocation = new DirectLocation(checkNode.getOffset(), checkNode.getLength(),
                    checkNode.getStartLine(), allItems.get(0));

                Issue issue = new BslDirectLocationIssue(Messages.MethodSemicolonExtraCheck_Issue, directLocation);

                resultAceptor.addIssue(issue);
            }
            else if (checkNodeNext != null && checkNodeNext.getText().contains(";")) //$NON-NLS-1$
            {
                DirectLocation directLocation = new DirectLocation(checkNodeNext.getOffset(), checkNodeNext.getLength(),
                    checkNodeNext.getStartLine(), allItems.get(0));

                Issue issue = new BslDirectLocationIssue(Messages.MethodSemicolonExtraCheck_Issue, directLocation);

                resultAceptor.addIssue(issue);
            }
        }
    }
}
