/*******************************************************************************
 * Copyright (C) 2023-2024, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.internal.bsl.ui.services;

import com._1c.g5.v8.dt.bsl.common.IBslModuleTextInsertInfo;
import com._1c.g5.v8.dt.bsl.model.Module;

/**
 * Built-in language module region information with {@link String} region wrap data
 *
 * @author Kuznetsov Nikita
 */
public class BslModuleRegionsInfo
    implements IBslModuleTextInsertInfo
{
    private final int insertPosition;
    private final int clearPosition;
    private final int clearLength;
    private final Module module;
    private final String regionName;

    /**
     * {@link BslModuleRegionsInfo} constructor
     *
     * @param insertPosition <code>int</code> insertion offset, cannot be negative
     * @param clearPosition text clear <code>int</code> position, can be negative if no clear needed
     * @param clearLength text clear <code>int</code> length, can be negative if no clear needed
     * @param module current {@link Module}, cannot be <code>null</code>
     * @param regionName {@link String} region name, can be <code>null</code>
     */
    public BslModuleRegionsInfo(int insertPosition, int clearPosition, int clearLength, Module module,
        String regionName)
    {
        this.insertPosition = insertPosition;
        this.clearPosition = clearPosition;
        this.clearLength = clearLength;
        this.module = module;
        this.regionName = regionName;
    }

    @Override
    public int getPosition()
    {
        return clearLength > 0 ? clearPosition : insertPosition;
    }

    @Override
    public int getClearLength()
    {
        return clearLength;
    }

    @Override
    public Module getModule()
    {
        return module;
    }

    /**
     * Returns region name
     *
     * @return {@link String} region name, can be <code>null</code>
     */
    public String getRegionName()
    {
        return regionName;
    }
}
