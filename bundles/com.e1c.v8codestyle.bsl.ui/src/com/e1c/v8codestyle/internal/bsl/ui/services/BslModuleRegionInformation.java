/**
 * Copyright (C) 2023, 1C
 */
package com.e1c.v8codestyle.internal.bsl.ui.services;

import com._1c.g5.v8.dt.bsl.common.IBslModuleInformation;
import com._1c.g5.v8.dt.bsl.model.Module;

/**
 * BSL module region information with {@link String} region wrap data
 *
 * @author Kuznetsov Nikita
 */
public class BslModuleRegionInformation
    implements IBslModuleInformation
{
    private final Module module;
    private final int insertPosition;
    private final String regionName;

    public BslModuleRegionInformation(Module module, int insertPosition, String regionName)
    {
        this.module = module;
        this.insertPosition = insertPosition;
        this.regionName = regionName;
    }

    @Override
    public Module getModule()
    {
        return module;
    }

    @Override
    public int getInsertPosition()
    {
        return insertPosition;
    }

    public String getRegionName()
    {
        return regionName;
    }
}
