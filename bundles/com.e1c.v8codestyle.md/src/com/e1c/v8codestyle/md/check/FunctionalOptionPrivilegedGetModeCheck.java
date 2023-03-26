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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.FUNCTIONAL_OPTION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.FUNCTIONAL_OPTION__PRIVILEGED_GET_MODE;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.FunctionalOption;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * Check functional option use privileged get mode.
 * @author Vadim Goncharov
 */
public class FunctionalOptionPrivilegedGetModeCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "functional-option-privileged-get-mode"; //$NON-NLS-1$

    public FunctionalOptionPrivilegedGetModeCheck()
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
        builder.title(Messages.FunctionalOptionPrivilegedGetModeCheck_title)
            .description(Messages.FunctionalOptionPrivilegedGetModeCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(689, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipAdoptedInExtensionMdObjectExtension())
            .topObject(FUNCTIONAL_OPTION)
            .checkTop()
            .features(FUNCTIONAL_OPTION__PRIVILEGED_GET_MODE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        FunctionalOption fo = (FunctionalOption)object;
        if (!fo.isPrivilegedGetMode())
        {
            resultAcceptor.addIssue(Messages.FunctionalOptionPrivilegedGetModeCheck_Functional_option_dont_use_privileged_get_mode, fo,
                FUNCTIONAL_OPTION__PRIVILEGED_GET_MODE);
        }

    }

}
