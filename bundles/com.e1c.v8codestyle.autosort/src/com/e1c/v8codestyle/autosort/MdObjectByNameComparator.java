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
    private final boolean naturalSortOrder;

    /**
     * Instantiates a new metadata object comparator by name.
     *
     * @param ascending the ascending
     * @param naturalSortOrder the sort order
     */
    public MdObjectByNameComparator(boolean ascending, boolean naturalSortOrder)
    {
        this.ascending = ascending;
        this.naturalSortOrder = naturalSortOrder;
    }

    @Override
    public int compare(EObject first, EObject second)
    {
        if (first instanceof MdObject && second instanceof MdObject)
        {
            String firstName = Strings.nullToEmpty(((MdObject)first).getName());
            String secondName = Strings.nullToEmpty(((MdObject)second).getName());
            return this.ascending ? compareMdObjectNamesWithIgnoreCase(firstName, secondName, this.naturalSortOrder)
                : compareMdObjectNamesWithIgnoreCase(secondName, firstName, this.naturalSortOrder);
        }
        return 0;
    }

    private int compareMdObjectNamesWithIgnoreCase(String s1, String s2, boolean naturalSortOrder)
    {
        int n1 = s1.length();
        int n2 = s2.length();
        int min = Math.min(n1, n2);

        char lessThanDigits = '!';

        for (int i = 0; i < min; i++)
        {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 != c2)
            {
                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
                if (c1 != c2)
                {
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2)
                    {
                        if (!naturalSortOrder)
                        {
                            // Symbol "low line" must be less than digits
                            c1 = (c1 == '_' ? lessThanDigits : c1);
                            c2 = (c2 == '_' ? lessThanDigits : c2);
                        }
                        // No overflow because of numeric promotion
                        return c1 - c2;
                    }
                }
            }
        }
        return n1 - n2;
    }

}
