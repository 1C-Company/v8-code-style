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
package com.e1c.v8codestyle.md.check;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.Document;
import com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals;
import com._1c.g5.v8.dt.metadata.mdclass.Posting;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * The check for documents that are meant to be posted (which is equivalent to allowing posting and document
 * having registers).
 * All such documents must have privileged posting and unposting modes on.
 *
 * @author Vitaly Prolomov
 *
 */
public class PrivilegedModeForPostingDocumentsCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "privileged-mode-for-posting-documents"; //$NON-NLS-1$

    public PrivilegedModeForPostingDocumentsCheck()
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
        builder.title(Messages.PrivilegedModeForPostingDocumentsCheck_title)
            .description(Messages.PrivilegedModeForPostingDocumentsCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .extension(new TopObjectFilterExtension())
            .extension(new StandardCheckExtension(CHECK_ID, CorePlugin.PLUGIN_ID))
            .issueType(IssueType.PERFORMANCE)
            .topObject(Literals.DOCUMENT)
            .checkTop()
            .features(Literals.DOCUMENT__POSTING, Literals.DOCUMENT__POST_IN_PRIVILEGED_MODE,
                Literals.DOCUMENT__UNPOST_IN_PRIVILEGED_MODE, Literals.DOCUMENT__REGISTER_RECORDS,
                Literals.DOCUMENT__REGISTER_RECORDS_DELETION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        final String message = Messages.PrivilegedModeForPostingDocumentsCheck_message;

        if (((Document)object).getPosting() == Posting.ALLOW && !((Document)object).getRegisterRecords().isEmpty())
        {
            if (!((Document)object).isUnpostInPrivilegedMode())
            {
                resultAcceptor.addIssue(message, Literals.DOCUMENT__POST_IN_PRIVILEGED_MODE);
            }
            if (!((Document)object).isPostInPrivilegedMode())
            {
                resultAcceptor.addIssue(message, Literals.DOCUMENT__POST_IN_PRIVILEGED_MODE);
            }
        }
    }
}
