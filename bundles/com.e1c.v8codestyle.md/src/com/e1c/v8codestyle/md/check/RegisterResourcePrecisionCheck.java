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

import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.TYPE_DESCRIPTION;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.TYPE_DESCRIPTION__NUMBER_QUALIFIERS;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.ACCOUNTING_REGISTER;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.ACCUMULATION_REGISTER;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.mcore.TypeDescription;
import com._1c.g5.v8.dt.metadata.mdclass.RegisterResource;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * Check accounting or accumulation register resource precision that should be no more than 25.
 *
 * @author Timur Mukhamedishin
 */
public final class RegisterResourcePrecisionCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "register-resource-precision"; //$NON-NLS-1$

    public static final String MAX_PRECISION = "max-precision"; //$NON-NLS-1$

    public static final String MAX_PRECISION_DEFAULT = "25"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RegisterResourcePrecisionCheck_title)
            .description(Messages.RegisterResourcePrecisionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.PORTABILITY)
            .extension(new StandardCheckExtension(467, getCheckId(), CorePlugin.PLUGIN_ID))
            .parameter(MAX_PRECISION, Integer.class, MAX_PRECISION_DEFAULT,
                Messages.RegisterResourcePrecisionCheck_message);

        builder.topObject(ACCUMULATION_REGISTER)
            .checkTop()
            .containment(TYPE_DESCRIPTION)
            .features(TYPE_DESCRIPTION__NUMBER_QUALIFIERS);

        builder.topObject(ACCOUNTING_REGISTER)
            .checkTop()
            .containment(TYPE_DESCRIPTION)
            .features(TYPE_DESCRIPTION__NUMBER_QUALIFIERS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (!(object instanceof TypeDescription))
        {
            return;
        }

        TypeDescription td = (TypeDescription)object;
        if (!(td.eContainer() instanceof RegisterResource))
        {
            return;
        }

        int maxPrecision = parameters.getInt(MAX_PRECISION);
        int precision = td.getNumberQualifiers().getPrecision();

        if (precision > maxPrecision)
        {
            RegisterResource resource = (RegisterResource)(td.eContainer());
            resultAceptor.addIssue(
                MessageFormat.format(Messages.RegisterResourcePrecisionCheck_message, resource.getName(), maxPrecision),
                td);
        }
    }
}
