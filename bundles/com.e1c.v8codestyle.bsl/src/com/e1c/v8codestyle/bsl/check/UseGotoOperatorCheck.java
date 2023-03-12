/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.BLOCK;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Block;
import com._1c.g5.v8.dt.bsl.model.GotoStatement;
import com._1c.g5.v8.dt.bsl.model.LabeledStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * The check on use Goto operator
 * @author Vadim Goncharov
 */
public class UseGotoOperatorCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "use-goto-operator"; //$NON-NLS-1$

    public UseGotoOperatorCheck()
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
        builder.title(Messages.UseGotoOperatorCheck_title)
            .description(Messages.UseGotoOperatorCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(547, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(BLOCK);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        Block block = (Block)object;
        for (Statement st : block.allStatements())
        {
            if (monitor.isCanceled())
            {
                return;
            }

            if (st instanceof GotoStatement)
            {
                resultAcceptor.addIssue(Messages.UseGotoOperatorCheck_Use_Goto_operator, st);
            }

            if (st instanceof LabeledStatement)
            {
                resultAcceptor.addIssue(Messages.UseGotoOperatorCheck_Use_Label_with_Goto_operator, st);
            }

        }

    }

}
