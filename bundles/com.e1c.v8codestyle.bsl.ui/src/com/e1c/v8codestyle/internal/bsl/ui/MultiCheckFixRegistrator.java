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
package com.e1c.v8codestyle.internal.bsl.ui;

import com._1c.g5.wiring.IManagedService;
import com.e1c.g5.v8.dt.check.qfix.IFix;
import com.e1c.g5.v8.dt.check.qfix.IFixContext;
import com.e1c.g5.v8.dt.check.qfix.IFixRepository;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.v8codestyle.bsl.ui.qfix.OpenBslDocCommentViewFix;
import com.google.inject.Inject;

/**
 * The registrar of quick-fix instance that applicable for several checks.
 * The quick-fix should have public constructor with 1 parameter of {@link CheckUid}.
 *
 * @author Dmitriy Marmyshev
 */
public class MultiCheckFixRegistrator
    implements IManagedService
{
    private static final String CHECK_BUNDLE = "com.e1c.v8codestyle.bsl"; //$NON-NLS-1$

    @Inject
    private IFixRepository fixRepository;

    /**
     * Multi-check fix should be added here
     */
    @Override
    public void activate()
    {
        registerOpenBslDocCommentViewFix();
    }

    @Override
    public void deactivate()
    {
        // do nothing
    }

    private void registerOpenBslDocCommentViewFix()
    {
        for (String checkId : OpenBslDocCommentViewFix.getCheckIds())
        {
            CheckUid id = new CheckUid(checkId, CHECK_BUNDLE);

            IFix<? extends IFixContext> instance = new OpenBslDocCommentViewFix(id);
            fixRepository.registerFix(instance);
        }
    }
}
