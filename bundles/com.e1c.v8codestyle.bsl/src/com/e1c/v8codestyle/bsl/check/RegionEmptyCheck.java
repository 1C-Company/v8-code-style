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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.REGION_PREPROCESSOR;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Check that region is empty.
 *
 * @author Dmitriy Marmyshev
 */
public class RegionEmptyCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "module-region-empty"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RegionEmptyCheck_title)
            .description(Messages.RegionEmptyCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .module()
            .checkedObjectType(REGION_PREPROCESSOR);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        RegionPreprocessor region = (RegionPreprocessor)object;

        if (isEmpty(region))
        {
            resultAceptor.addIssue(Messages.RegionEmptyCheck_Region_is_empty, NAMED_ELEMENT__NAME);
        }
    }

}
