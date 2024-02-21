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
package com.e1c.v8codestyle.form.fix.itests;

import java.util.Collection;

import org.eclipse.core.runtime.NullProgressMonitor;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.wiring.ServiceAccess;
import com.e1c.g5.v8.dt.check.qfix.FixProcessHandle;
import com.e1c.g5.v8.dt.check.qfix.FixVariantDescriptor;
import com.e1c.g5.v8.dt.check.qfix.IFixManager;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;

/**
 * The abstract test base class to test fixes in forms.
 *
 * @author Dmitriy Marmyshev
 */
public abstract class FormFixTestBase
    extends CheckTestBase
{

    private final String fixDescriptionPattern;
    private IFixManager fixManager = ServiceAccess.get(IFixManager.class);

    /**
     * Instantiates a new form fix test base.
     *
     * @param fixDescriptionPattern the fix description pattern to match string of fix description,
     * cannot be {@code null}.
     */
    protected FormFixTestBase(String fixDescriptionPattern)
    {
        this.fixDescriptionPattern = fixDescriptionPattern;
    }

    /**
     * Apply fix for the marker.
     *
     * @param marker the marker
     * @param dtProject the DT project of the marker
     */
    protected void applyFix(Marker marker, IDtProject dtProject)
    {
        FixProcessHandle handle = fixManager.prepareFix(marker, dtProject);

        FixVariantDescriptor variantDescr = null;

        Collection<FixVariantDescriptor> variants = fixManager.getApplicableFixVariants(handle);
        for (FixVariantDescriptor variant : variants)
        {
            if (variant.getDescription().matches(fixDescriptionPattern))
            {
                variantDescr = variant;
            }
        }

        fixManager.selectFixVariant(variantDescr, handle);
        fixManager.executeFix(handle, new NullProgressMonitor());
        fixManager.finishFix(handle);
    }

}
