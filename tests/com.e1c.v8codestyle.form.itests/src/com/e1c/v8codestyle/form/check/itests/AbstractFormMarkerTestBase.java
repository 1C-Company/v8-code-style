/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.form.check.itests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.FormItemContainer;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.internal.form.CorePlugin;

/**
 * The single project form item test base
 * 
 * @author Artem Samohvalov
 */
public abstract class AbstractFormMarkerTestBase
    extends SingleProjectReadOnlyCheckTestBase
{
    protected abstract String getCheckId();

    /**
     * Gets all markers of the check nested in the form: markers of form items, command bars and context menus.
     * Markers of the form itself are not included, markers of other checks are filtered out.
     * 
     * @param fqn the fqn
     * @return the form nested markers by FQN
     */
    protected List<Marker> getFormNestedMarkersByFQN(String fqn)
    {
        long formId = getTopObjectIdByFqn(fqn, getProject());
        Object markerContainerId = extractMarkerContainerId(formId);
        IProject workspaceProject = getProject().getWorkspaceProject();
        CheckUid uid = new CheckUid(getCheckId(), CorePlugin.PLUGIN_ID);

        return Arrays.stream(markerManager.getNestedMarkers(workspaceProject, markerContainerId))
            .filter(m -> uid.equals(checkRepository.getUidForShortUid(m.getCheckId(), workspaceProject)))
            .toList();
    }

    /**
     * Gets markers of the check attached to the form items by form FQN.
     *
     * @param fqn the fqn
     * @return the item markers by FQN
     */
    protected List<Marker> getItemMarkersByFQN(String fqn)
    {
        return getItemMarkers(getFormByFQN(fqn));
    }

    /**
     * Gets markers of the check attached to the form items: form items, command bar items and
     * items of nested containers (groups, command bars, context menus). Markers of the form itself are not included
     *
     * @param form the form
     * @return the markers
     */
    protected List<Marker> getItemMarkers(Form form)
    {
        List<Marker> markers = new ArrayList<>();

        List<Marker> innerMarkers = getMarkersByCheckIds(Set.of(getCheckId()), form, getProject());
        markers.addAll(innerMarkers);

        findMarkers(form.getItems(), markers);

        if (form.getAutoCommandBar() != null)
        {
            findMarkers(form.getAutoCommandBar().getItems(), markers);
        }

        return markers;
    }

    /**
     * Gets the form by FQN.
     *
     * @param fqn the fqn
     * @return the form by FQN
     */
    protected Form getFormByFQN(String fqn)
    {
        IBmObject object = getTopObjectByFqn(fqn, getProject());
        assertTrue(object instanceof Form);
        return (Form)object;
    }

    private void findMarkers(List<FormItem> items, List<Marker> markers)
    {
        for (FormItem item : items)
        {
            List<Marker> innerMarkers = getMarkersByCheckIds(Set.of(getCheckId()), item, getProject());

            if (!innerMarkers.isEmpty())
            {
                markers.addAll(innerMarkers);
            }

            if (item instanceof FormItemContainer container)
            {
                findMarkers(container.getItems(), markers);
            }
        }
    }
}
