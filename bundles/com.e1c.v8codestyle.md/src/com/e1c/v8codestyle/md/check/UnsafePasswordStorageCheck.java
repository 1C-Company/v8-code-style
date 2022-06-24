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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_FEATURE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_FEATURE__PASSWORD_MODE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CHART_OF_CHARACTERISTIC_TYPES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONSTANT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONSTANT__PASSWORD_MODE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.BasicFeature;
import com._1c.g5.v8.dt.metadata.mdclass.Constant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * Check not secure password storage in the information database.
 *
 * @author Artem Iliukhin
 */
public final class UnsafePasswordStorageCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "unsafe-password-ib-storage"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.UnsafePasswordStorageCheck_Avoid_storing_password_in_infobase)
            .description(Messages.UnsafePasswordStorageCheck_Avoid_storing_password_in_infobase_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.SECURITY)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(CONSTANT)
            .checkTop()
            .features(CONSTANT__PASSWORD_MODE)
            .topObject(CATALOG)
            .containment(BASIC_FEATURE)
            .features(BASIC_FEATURE__PASSWORD_MODE)
            .topObject(DOCUMENT)
            .containment(BASIC_FEATURE)
            .features(BASIC_FEATURE__PASSWORD_MODE)
            .topObject(CHART_OF_CHARACTERISTIC_TYPES)
            .containment(BASIC_FEATURE)
            .features(BASIC_FEATURE__PASSWORD_MODE); //TODO change to BASIC_DB_OBJECT, when will it work
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof BasicFeature && ((BasicFeature)object).isPasswordMode())
        {
            resultAceptor.addIssue(Messages.UnsafePasswordStorageCheck_Avoid_storing_password_in_infobase_error, object,
                BASIC_FEATURE__PASSWORD_MODE);
        }
        else if (object instanceof Constant && ((Constant)object).isPasswordMode())
        {
            resultAceptor.addIssue(Messages.UnsafePasswordStorageCheck_Avoid_storing_password_in_infobase_error, object,
                CONSTANT__PASSWORD_MODE);
        }
    }
}
