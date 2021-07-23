/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__GLOBAL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__RETURN_VALUES_REUSE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Check global common module name has "Global" suffix
 *
 * @author Dmitriy Marmyshev
 */
public final class CommonModuleNameGlobal
    extends BasicCheck
{

    private static final String CHECK_ID = "common-module-name-global"; //$NON-NLS-1$

    private static final String NAME_SUFFIX_DEFAULT = "Глобальный,Global"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        //@formatter:off
        builder.title(Messages.CommonModuleNameGlobal_title)
            .description(Messages.CommonModuleNameGlobal_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.CRITICAL)
            .issueType(IssueType.WARNING)
            .extension(new TopObjectFilterExtension())
            .extension(new MdObjectNameWithoutSuffix(NAME_SUFFIX_DEFAULT))
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(MD_OBJECT__NAME,
                COMMON_MODULE__RETURN_VALUES_REUSE,
                COMMON_MODULE__GLOBAL);
        //@formatter:on
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        CommonModule commonModule = (CommonModule)object;
        if (commonModule.isGlobal() && commonModule.getReturnValuesReuse() == ReturnValuesReuse.DONT_USE)
        {
            String message = MessageFormat.format(Messages.CommonModuleNameGlobal_message,
                parameters.getString(MdObjectNameWithoutSuffix.NAME_SUFFIX_PARAMETER_NAME));
            resultAceptor.addIssue(message, MD_OBJECT__NAME);
        }
    }

}
