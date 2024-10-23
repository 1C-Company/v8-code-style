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

import org.eclipse.emf.common.util.URI;

import com._1c.g5.v8.dt.bsl.common.IBslModuleTextInsertInfo;

/**
 * Built-in language module region information with {@link String} region wrap data
 *
 * @author Kuznetsov Nikita
 */
public class BslModuleRegionsInfo
    implements IBslModuleTextInsertInfo
{
    /**
     * Has region before offset position
     */
    public static final int REGION_FLAG_HAS_REGION_BEFORE = 1;

    /**
     * Has region after offset position
     */
    public static final int REGION_FLAG_HAS_REGION_AFTER = 2;

    private final URI resourceURI;
    private final int insertPosition;
    private final int clearPosition;
    private final int clearLength;
    private final int regionFlags;
    private final String regionName;

    /**
     * {@link BslModuleRegionsInfo} constructor
     *
     * @param resourceURI current module or document resource {@link URI}, cannot be <code>null</code>
     * @param insertPosition <code>int</code> insertion offset, cannot be negative
     * @param clearPosition text clear <code>int</code> position, can be negative if no clear needed
     * @param clearLength text clear <code>int</code> length, can be negative if no clear needed
     * @param regionFlags region <code>int</code> flags represents regions states, cannot be negative
     * @param regionName {@link String} region name, can be <code>null</code>
     */
    public BslModuleRegionsInfo(URI resourceURI, int insertPosition, int clearPosition, int clearLength,
        int regionFlags, String regionName)
    {
        this.resourceURI = resourceURI;
        this.insertPosition = insertPosition;
        this.clearPosition = clearPosition;
        this.clearLength = clearLength;
        this.regionFlags = regionFlags;
        this.regionName = regionName;
    }

    @Override
    public URI getResourceURI()
    {
        return resourceURI;
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

    /**
     * Returns flags which represents regions states
     *
     * @return <code>int</code> flags, cannot be negative
     */
    public int getRegionFlags()
    {
        return regionFlags;
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
