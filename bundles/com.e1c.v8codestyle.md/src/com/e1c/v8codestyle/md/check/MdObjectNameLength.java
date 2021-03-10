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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.check.CheckComplexity;
import com._1c.g5.v8.dt.check.ICheckParameters;
import com._1c.g5.v8.dt.check.components.BasicCheck;
import com._1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com._1c.g5.v8.dt.check.settings.IssueSeverity;
import com._1c.g5.v8.dt.check.settings.IssueType;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;

/**
 * Check top Metadata object name lengh that should be less then 80.
 *
 * @author Dmitriy Marmyshev
 */
public class MdObjectNameLength
    extends BasicCheck
{

    public static final String CHECK_ID = "mdo-name-length"; //$NON-NLS-1$

    public static final String MAX_NAME_LENGTH = "max-name-length"; //$NON-NLS-1$

    public static final String EXCLUDE_NAME_PATTERN_PARAMETER_NAME = "excludeNamePattern"; //$NON-NLS-1$

    public static final String MAX_NAME_LENGTH_DEFAULT = "80"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        //@formatter:off
        builder.title(Messages.MdObjectNameLength_title)
            .description(MessageFormat.format(Messages.MdObjectNameLength_description, MAX_NAME_LENGTH_DEFAULT))
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.CRITICAL)
            .extension(new TopObjectFilterExtension(
                EXCLUDE_NAME_PATTERN_PARAMETER_NAME,
                Messages.common_Exclude_name_pattern,
                StringUtils.EMPTY,
                MD_OBJECT__NAME))
            .issueType(IssueType.PORTABILITY)
            .topObject(MD_OBJECT)
            .checkTop()
            .features(MD_OBJECT__NAME)
            .parameter(MAX_NAME_LENGTH,
                Integer.class,
                MAX_NAME_LENGTH_DEFAULT,
                Messages.MdObjectNameLength_Maximum_name_length_description);
        //@formatter:on

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        MdObject mdObject = (MdObject)object;
        if (mdObject instanceof Configuration)
            return;

        String name = mdObject.getName();
        int max = parameters.getInt(MAX_NAME_LENGTH);
        if (name != null && name.length() > max && max > 0)
        {
            resultAceptor.addIssue(MessageFormat.format(Messages.MdObjectNameLength_message, max), MD_OBJECT__NAME);
        }
    }

}
