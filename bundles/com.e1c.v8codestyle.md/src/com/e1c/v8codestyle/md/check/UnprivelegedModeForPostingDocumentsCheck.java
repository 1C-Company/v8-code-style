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

/**
 * The check for documents that are meant to be posted (which is equivalent to allowing posting and document
 * having registers).
 * All such documents must have privileged posting and unposting modes on.
 *
 * @author Vitaly Prolomov
 *
 */
public class UnprivelegedModeForPostingDocumentsCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "unprivileged-mode-for-posting-documents"; //$NON-NLS-1$

    public UnprivelegedModeForPostingDocumentsCheck()
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
        builder.title(Messages.UnprivilegedModeForPostingDocumentsCheck_title)
            .description(Messages.UnprivilegedModeForPostingDocumentsCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .extension(new TopObjectFilterExtension())
            .issueType(IssueType.PERFORMANCE)
            .topObject(Literals.DOCUMENT)
            .checkTop()
            .features(Literals.DOCUMENT__POSTING, Literals.DOCUMENT__POST_IN_PRIVILEGED_MODE,
                Literals.DOCUMENT__UNPOST_IN_PRIVILEGED_MODE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof Document))
        {
            return;
        }

        Document doc = (Document)object;

        final String message = Messages.UnprivilegedModeForPostingDocumentsCheck_message;

        if (doc.getPosting() == Posting.ALLOW && !doc.getRegisterRecords().isEmpty())
        {
            if (!doc.isUnpostInPrivilegedMode())
            {
                resultAcceptor.addIssue(message, Literals.DOCUMENT__POST_IN_PRIVILEGED_MODE);
            }
            if (!doc.isPostInPrivilegedMode())
            {
                resultAcceptor.addIssue(message, Literals.DOCUMENT__POST_IN_PRIVILEGED_MODE);
            }
        }
    }
}
