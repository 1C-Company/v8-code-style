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
package com.e1c.v8codestyle.internal.bsl;

import com._1c.g5.wiring.IManagedService;
import com.e1c.g5.v8.dt.check.qfix.IFix;
import com.e1c.g5.v8.dt.check.qfix.IFixContext;
import com.e1c.g5.v8.dt.check.qfix.IFixRepository;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.v8codestyle.bsl.qfix.RemoveExportFix;
import com.e1c.v8codestyle.bsl.strict.fix.RemoveStrictTypesAnnotationFix;
import com.google.inject.Inject;

/**
 * The registrar of quick-fix instance that applicable for several checks.
 * The registering quick-fix should accept {@link CheckUid} as parameter in public constructor.
 *
 * @author Dmitriy Marmyshev
 */
public class MultiCheckFixRegistrator
    implements IManagedService
{

    @Inject
    private IFixRepository fixRepository;

    /**
     * Multi-check fix should be added here
     */
    @Override
    public void activate()
    {
        registerRemoveStrictTypesAnnotationFix();
        registerRemoveExportFix();
    }

    @Override
    public void deactivate()
    {
        // do nothing
    }

    private void registerRemoveStrictTypesAnnotationFix()
    {
        for (String checkId : RemoveStrictTypesAnnotationFix.getCheckIds())
        {
            CheckUid id = new CheckUid(checkId, BslPlugin.PLUGIN_ID);

            IFix<? extends IFixContext> instance = new RemoveStrictTypesAnnotationFix(id);
            fixRepository.registerFix(instance);
        }
    }

    private void registerRemoveExportFix()
    {
        for (String checkId : RemoveExportFix.getCheckIds())
        {
            CheckUid id = new CheckUid(checkId, BslPlugin.PLUGIN_ID);

            IFix<? extends IFixContext> instance = new RemoveExportFix(id);
            fixRepository.registerFix(instance);
        }
    }
}
