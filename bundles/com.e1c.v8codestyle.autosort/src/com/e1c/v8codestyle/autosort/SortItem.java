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
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import com._1c.g5.v8.bm.core.IBmObject;

/**
 * The Sort item represents FQN (fully qualified name) of the object and it's list reference that need to sort with
 * given sorter.
 *
 * @author Dmitriy Marmyshev
 */
public final class SortItem
{
    private final EReference listRef;

    private final String fqn;

    private final Comparator<EObject> sorter;

    private final int hashCode;

    /**
     * Instantiates a new sort item.
     *
     * @param fqn the FQN to sort, cannot be {@code null}.
     * @param listRef the list reference, cannot be {@code null}.
     * @param sorter the sorter, cannot be {@code null}.
     */
    public SortItem(String fqn, EReference listRef, Comparator<EObject> sorter)
    {
        super();
        this.fqn = fqn;
        this.listRef = listRef;
        this.sorter = sorter;
        this.hashCode = Objects.hash(fqn, listRef);
    }

    /**
     * Gets the FQN (fully qualified name) of the {@link IBmObject} to sort it's list
     *
     * @return the FQN, cannot return {@code null}.
     */
    public String getFqn()
    {
        return fqn;
    }

    /**
     * Gets the list reference to sort objects.
     *
     * @return the list reference to sort objects, cannot return {@code null}.
     */
    public EReference getListRef()
    {
        return listRef;
    }

    /**
     * Gets the sorter for the list.
     *
     * @return the sorter, cannot return {@code null}.
     */
    public Comparator<EObject> getSorter()
    {
        return sorter;
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        SortItem other = (SortItem)obj;
        return Objects.equals(fqn, other.fqn) && Objects.equals(listRef, other.listRef);
    }

}
