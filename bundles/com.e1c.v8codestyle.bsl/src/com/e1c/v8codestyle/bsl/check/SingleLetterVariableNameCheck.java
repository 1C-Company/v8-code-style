package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DECLARE_STATEMENT;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STATIC_FEATURE_ACCESS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.VARIABLE;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.DeclareStatement;
import com._1c.g5.v8.dt.bsl.model.ExplicitVariable;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Checks if variable (parameter in method, declared or initialized variable)
 * has a single letter name (which violates code style rules).
 *
 * @author Vitaly Prolomov
 *
 */
public class SingleLetterVariableNameCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "bsl-single-letter-variable-name-check"; //$NON-NLS-1$
    private static final String message = "Variable has a single letter name"; //$NON-NLS-1$

    public SingleLetterVariableNameCheck()
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
        builder.title(Messages.SingleLetterVariableNameCheck_title)
            .description(Messages.SingleLetterVariableNameCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .module()
            .checkedObjectType(VARIABLE, DECLARE_STATEMENT, STATIC_FEATURE_ACCESS);

    }

    /**
     * Checks 3 different cases: parameters in methods, declared and initialized variables for
     * having single letter name.
     */
    @Override
    protected void check(Object object, ResultAcceptor acceptor, ICheckParameters parameters, IProgressMonitor monitor)
    {

        if (monitor.isCanceled())
        {
            return;
        }

        if (object instanceof Variable && ((Variable)object).getName().length() == 1)
        {
            acceptor.addIssue(message, object);
        }
        else if (object instanceof StaticFeatureAccess)
        {
            Variable variable = ((StaticFeatureAccess)object).getImplicitVariable();

            if (((StaticFeatureAccess)object).getImplicitVariable() != null && variable.getName().length() == 1)
            {
                acceptor.addIssue(message, object);
            }
        }
        else if (object instanceof DeclareStatement)
        {
            DeclareStatement ds = (DeclareStatement)object;
            ExplicitVariable ev = ds.getVariables().get(0);

            if (ev != null && ev.getName().length() == 1)
            {
                acceptor.addIssue(message, object);
            }
        }
    }

    public static String getMessage()
    {
        return message;
    }
}
