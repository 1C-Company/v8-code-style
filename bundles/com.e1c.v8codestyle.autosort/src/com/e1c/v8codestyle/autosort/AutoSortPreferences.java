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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG__ATTRIBUTES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG__COMMANDS;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG__FORMS;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG__TABULAR_SECTIONS;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG__TEMPLATES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.HTTP_SERVICE__URL_TEMPLATES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.INFORMATION_REGISTER__RESOURCES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.URL_TEMPLATE__METHODS;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.WEB_SERVICE__OPERATIONS;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.ecore.EReference;
import org.osgi.service.prefs.BackingStoreException;

import com.e1c.v8codestyle.internal.autosort.AutoSortPlugin;

/**
 * Public constants and utility method for sort preferences in the project.
 *
 * @author Dmitriy Marmyshev
 */
public final class AutoSortPreferences
{

    public static final String KEY_ASCENDING = "sortAscending"; //$NON-NLS-1$

    public static final String KEY_ALL_TOP = "topObjects"; //$NON-NLS-1$

    public static final String KEY_TOP_NODE = "top"; //$NON-NLS-1$

    public static final String KEY_SUBORDINATE_OBJECTS = "subordinateObjects"; //$NON-NLS-1$

    public static final String KEY_SUBORDINATE_NODE = "subordinate"; //$NON-NLS-1$

    public static final String KEY_FORMS = CATALOG__FORMS.getName();

    public static final String KEY_TEMPLATES = CATALOG__TEMPLATES.getName();

    public static final String KEY_COMMANDS = CATALOG__COMMANDS.getName();

    public static final String KEY_OPERATIONS = WEB_SERVICE__OPERATIONS.getName();

    public static final String KEY_URL_TEMPLATES = HTTP_SERVICE__URL_TEMPLATES.getName();

    public static final String KEY_METHODS = URL_TEMPLATE__METHODS.getName();

    public static final String KEY_ATTRIBUTES = CATALOG__ATTRIBUTES.getName();

    public static final String KEY_REGISTRY_RESOURCES = INFORMATION_REGISTER__RESOURCES.getName();

    public static final String KEY_TABULAR_SECTIONS = CATALOG__TABULAR_SECTIONS.getName();

    public static final boolean DEFAULT_SORT_ASCENDING = true;

    public static final boolean DEFAULT_SORT = false;

    /**
     * Checks if the sort direction is ascending in the project.
     *
     * @param project the project to check, cannot be {@code null}.
     * @return true, if the sort is ascending, or return true as default if not set for project.
     */
    public static boolean isSortAscending(IProject project)
    {
        IEclipsePreferences rootNode = getPreferences(project);
        return rootNode.getBoolean(KEY_ASCENDING, DEFAULT_SORT_ASCENDING);
    }

    /**
     * Checks if need to sort all top object.
     *
     * @param project the project, cannot be {@code null}.
     * @return true, if need to sort all top object
     */
    public static boolean isSortAllTop(IProject project)
    {
        IEclipsePreferences rootNode = getPreferences(project);
        return rootNode.getBoolean(KEY_ALL_TOP, DEFAULT_SORT);
    }

    /**
     * Checks if need to sort sub-ordinate objects of the top object.
     *
     * @param project the project, cannot be {@code null}.
     * @return true, if need to sort sub-ordinate objects
     */
    public static boolean isSortSubOrdinateObjects(IProject project)
    {
        IEclipsePreferences rootNode = getPreferences(project);
        return rootNode.getBoolean(KEY_SUBORDINATE_OBJECTS, DEFAULT_SORT);
    }

    /**
     * Checks if it is allowed to sort the list reference in the project.
     *
     * @param project the project, cannot be {@code null}.
     * @param listRef the list reference, cannot be {@code null}.
     * @return true, if it is allowed to sort the list
     */
    public static boolean isAllowedToSort(IProject project, EReference listRef)
    {
        if (ListConstants.TOP_OPBJECT_LISTS.contains(listRef))
            return isSortAllTop(project) || isSortTopList(project, listRef);

        if (ListConstants.SUBORDINATE_OBJECT_LISTS.contains(listRef))
            return isSortSubOrdinateObjects(project) || isSortSubordinateList(project, listRef);

        return false;
    }

    /**
     * Gets the preferences for the project.
     *
     * @param project the project, cannot be {@code null}.
     * @return the preferences for the project, cannot return {@code null}.
     */
    public static IEclipsePreferences getPreferences(IProject project)
    {
        ProjectScope projectScope = new ProjectScope(project);
        IEclipsePreferences rootNode = projectScope.getNode(AutoSortPlugin.PLUGIN_ID);
        return rootNode;
    }

    /**
     * Setup the project default values.
     *
     * @param project the project to setup default, cannot be {@code null}.
     */
    public static void setupProjectDefault(IProject project)
    {
        IEclipsePreferences prefs = getPreferences(project);
        prefs.putBoolean(KEY_ALL_TOP, true);
        try
        {
            prefs.flush();
        }
        catch (BackingStoreException e)
        {
            AutoSortPlugin.logError(e);
        }
    }

    private static boolean isSortTopList(IProject project, EReference listRef)
    {
        IEclipsePreferences rootNode = getPreferences(project);

        return rootNode.node(KEY_TOP_NODE).getBoolean(listRef.getName(), DEFAULT_SORT);
    }

    private static boolean isSortSubordinateList(IProject project, EReference listRef)
    {
        IEclipsePreferences rootNode = getPreferences(project);
        return rootNode.node(KEY_SUBORDINATE_NODE).getBoolean(listRef.getName(), DEFAULT_SORT);
    }

}
