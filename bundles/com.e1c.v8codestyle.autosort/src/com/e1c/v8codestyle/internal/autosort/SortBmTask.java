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
package com.e1c.v8codestyle.internal.autosort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.ChangeCommand;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com.e1c.v8codestyle.autosort.SortItem;

/**
 * The task to sort BM objects.
 *
 * @author Dmitriy Marmyshev
 *
 * @see IBmModel
 * @see SortItem
 */
public class SortBmTask
    extends AbstractBmTask<Void>
{

    private final Collection<SortItem> items;

    /**
     * Instantiates a new sort BM task.
     *
     * <br>
     * <b>WARNING!</b> the collection of items must be immutable or unmodifiable for callers.
     *
     * @param items the unmodifiable collection of items to sort, cannot be {@code null}.
     */
    public SortBmTask(final Collection<SortItem> items)
    {
        super("Sort Md objects"); //$NON-NLS-1$
        this.items = new ArrayList<>(items);
    }

    @Override
    public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
    {
        for (SortItem item : items)
        {
            if (monitor.isCanceled())
                return null;

            if (!item.getListRef().isMany())
                continue;

            EObject parent = transaction.getTopObjectByFqn(item.getFqn());
            if (parent != null)
            {
                Object value = parent.eGet(item.getListRef());
                if (!(value instanceof List))
                    continue;

                List<?> elements = (List<?>)value;
                if (elements.size() < 2)
                    continue;

                SortCommand command = new SortCommand(parent, item.getListRef(), item.getSorter());
                if (command.canExecute())
                    command.execute();
                command.dispose();

            }
        }
        return null;
    }

    private static class SortCommand
        extends ChangeCommand
    {

        private final EList<EObject> list;

        private final Comparator<EObject> sorter;

        @SuppressWarnings("unchecked")
        private SortCommand(EObject parent, EReference listFeature, Comparator<EObject> sorter)
        {
            super(parent);
            Assert.isNotNull(parent);
            Assert.isTrue(listFeature.isMany());
            this.list = (EList<EObject>)parent.eGet(listFeature);
            this.sorter = sorter;
        }

        @Override
        protected void doExecute()
        {
            ArrayList<EObject> sorted = new ArrayList<>(list);
            Collections.sort(sorted, this.sorter);
            for (int i = 0; i < sorted.size(); i++)
            {
                EObject element = sorted.get(i);
                int oldIndex = list.indexOf(element);
                int newIndex = sorted.indexOf(element);
                if (oldIndex != -1 && newIndex != -1 && oldIndex != newIndex)
                {
                    list.move(newIndex, element);
                }
            }
        }
    }

}
