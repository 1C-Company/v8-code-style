/**
 * Copyright (C) 2023, 1C
 */
package com.e1c.v8codestyle.internal.bsl.ui.services;

import com._1c.g5.v8.dt.bsl.common.IBslModuleInformation;
import com._1c.g5.v8.dt.bsl.model.Module;

/**
 * Built-in language module region information with {@link String} region wrap data
 *
 * @author Kuznetsov Nikita
 */
public class BslModuleRegionInformation
    implements IBslModuleInformation
{
    private final int insertPosition;
    private final Module module;
    private final String regionName;

    /**
     * {@link BslModuleRegionInformation} constructor
     *
     * @param insertPosition
     * @param module current {@link Module}, cannot be <code>null</code>
     * @param regionName
     */
    public BslModuleRegionInformation(int insertPosition, Module module, String regionName)
    {
        this.insertPosition = insertPosition;
        this.module = module;
        this.regionName = regionName;
    }

    @Override
    public int getInsertPosition()
    {
        return insertPosition;
    }

    @Override
    public Module getModule()
    {
        return module;
    }

    /**
     * Get region name
     *
     * @return {@link String} region name, can be <code>null</code>
     */
    public String getRegionName()
    {
        return regionName;
    }
}
