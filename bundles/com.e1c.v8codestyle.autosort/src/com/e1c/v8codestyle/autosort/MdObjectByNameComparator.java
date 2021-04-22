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
package com.e1c.v8codestyle.autosort;

import java.util.Comparator;

import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com.google.common.base.Strings;

/**
 * The comparator for {@link MdObject} to compare them by name.
 *
 * @author Dmitriy Marmyshev
 */
public class MdObjectByNameComparator
    implements Comparator<EObject>
{

    private final boolean ascending;

    /**
     * Instantiates a new metadata object comparator by name.
     *
     * @param ascending the ascending
     */
    public MdObjectByNameComparator(boolean ascending)
    {
        this.ascending = ascending;
    }

    @Override
    public int compare(EObject first, EObject second)
    {
        if (first instanceof MdObject && second instanceof MdObject)
        {
            String firstName = Strings.nullToEmpty(((MdObject)first).getName());
            String secondName = Strings.nullToEmpty(((MdObject)second).getName());
            return this.ascending ? firstName.compareToIgnoreCase(secondName)
                : secondName.compareToIgnoreCase(firstName);
        }
        return 0;
    }

}
