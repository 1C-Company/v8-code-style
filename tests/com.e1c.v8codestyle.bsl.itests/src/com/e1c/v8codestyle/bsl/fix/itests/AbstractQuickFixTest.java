/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.fix.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.wiring.ServiceAccess;
import com.e1c.g5.v8.dt.check.ICheck;
import com.e1c.g5.v8.dt.check.qfix.FixProcessHandle;
import com.e1c.g5.v8.dt.check.qfix.FixVariantDescriptor;
import com.e1c.g5.v8.dt.check.qfix.IFixManager;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;

/**
 *  QuickFix helper for tests
 *
 *  @author Artem Samohvalov
 */
public abstract class AbstractQuickFixTest
    extends AbstractSingleModuleTestBase
{
    private IFixManager fixManager = ServiceAccess.get(IFixManager.class);

    /**
     * @param checkClass, cannot be {@code null}
     */
    @SuppressWarnings("rawtypes")
    protected AbstractQuickFixTest(Class<? extends ICheck> checkClass)
    {
        super(checkClass);
    }

    /**
     * This method perform the fix and modifying the project code
     *
     * @param marker the found marker in the project, cannot be {@code null}
     * @param fixDescription, cannot be {@code null}
     */
    protected void performFix(Marker marker, String fixDescription)
    {
        FixProcessHandle handle = fixManager.prepareFix(marker, getProject());

        FixVariantDescriptor variantDescr = null;

        Collection<FixVariantDescriptor> variants = fixManager.getApplicableFixVariants(handle);
        for (FixVariantDescriptor variant : variants)
        {
            if (variant.getDescription().equals(fixDescription))
            {
                variantDescr = variant;
                break;
            }
        }

        assertNotNull(variantDescr);

        fixManager.selectFixVariant(variantDescr, handle);
        fixManager.executeFix(handle, new NullProgressMonitor());
        fixManager.finishFix(handle);

        waitForDD(getProject());
    }

    /**
     * The method performs a validity check for the current project
     */
    protected void assertMarkerGone()
    {
        waitForDD(getProject());
        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }
}