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

package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT__POSTING;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT__POST_IN_PRIVILEGED_MODE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT__UNPOST_IN_PRIVILEGED_MODE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT__REGISTER_RECORDS;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.Document;
import com._1c.g5.v8.dt.metadata.mdclass.Posting;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * The check that in the document that allow posting set flags "Post in privileged mode"
 * and "Unpost in privileged mode". 
 *
 * @author Vadim Gocnharov
 */
public class DocumentPostInPrivilegedModeCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "document-post-in-privileged-mode"; //$NON-NLS-1$

    public DocumentPostInPrivilegedModeCheck()
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
        builder.title(Messages.DocumentPostInPrivilegedModeCheck_title)
            .description(Messages.DocumentPostInPrivilegedModeCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(689, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipAdoptedInExtensionMdObjectExtension())
            .topObject(DOCUMENT)
            .checkTop()
            .features(DOCUMENT__POSTING, DOCUMENT__POST_IN_PRIVILEGED_MODE, DOCUMENT__UNPOST_IN_PRIVILEGED_MODE,
                DOCUMENT__REGISTER_RECORDS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        Document document = (Document)object;
        if (monitor.isCanceled() || !documentRequirePosting(document) || !documentHaveRegisterRecords(document))
        {
            return;
        }

        if (!document.isPostInPrivilegedMode())
        {
            resultAcceptor.addIssue(
                Messages.DocumentPostInPrivilegedModeCheck_In_document_that_allow_posting_dont_set_flag_Post_in_privileged_mode,
                DOCUMENT__POST_IN_PRIVILEGED_MODE);
        }

        if (!document.isUnpostInPrivilegedMode())
        {
            resultAcceptor.addIssue(
                Messages.DocumentPostInPrivilegedModeCheck_In_document_that_allow_posting_dont_set_flag_Unpost_in_privileged_mode,
                DOCUMENT__UNPOST_IN_PRIVILEGED_MODE);
        }

    }

    private boolean documentRequirePosting(Document doc)
    {
        return doc.getPosting() == Posting.ALLOW;
    }

    private boolean documentHaveRegisterRecords(Document doc)
    {
        return !doc.getRegisterRecords().isEmpty();
    }

}
