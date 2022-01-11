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
package com.e1c.v8codestyle.check;

import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.settings.CheckUid;

/**
 * The Standard Check Extension.
 *
 * @author Dmitriy Marmyshev
 */
public class StandardCheckExtension
    implements IBasicCheckExtension
{

    private final CheckUid check;

    /**
     * Instantiates a new standard check extension.
     *
     * @param checkId the check id, cannot be {@code null}.
     * @param contributorId the contributor id, cannot be {@code null}.
     */
    public StandardCheckExtension(String checkId, String contributorId)
    {
        check = new CheckUid(checkId, contributorId);
    }

    @Override
    public void configureContextCollector(ICheckDefinition definition)
    {
        boolean enable = CheckUtils.isStandardCheckDefaultEnable();
        definition.setEnabled(enable);

        StandardCheckRegistry.getInstance().registerCheck(check);
    }

}
